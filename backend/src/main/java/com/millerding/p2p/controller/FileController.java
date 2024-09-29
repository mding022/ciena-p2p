package com.millerding.p2p.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.millerding.p2p.model.Metadata;
import com.millerding.p2p.service.FileChunkService;
import com.millerding.p2p.service.NodeService;

import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("http://localhost:3000")
public class FileController {

    @Autowired
    private NodeService ns;

    FileChunkService fcs = new FileChunkService();

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String uuid = UUID.randomUUID().toString();
        String outputFolder = "src/main/resources/cache/";
        try {
            File outputDir = new File(outputFolder);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            File convFile = new File(file.getOriginalFilename());
            try (FileOutputStream fos = new FileOutputStream(convFile)) {
                fos.write(file.getBytes());
            }

            ProcessBuilder processBuilder = new ProcessBuilder("python3", "chunker.py", convFile.getAbsolutePath(),
                    outputFolder);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to chunk the file.");
            }

            List<File> chunkFiles = Arrays.stream(Objects.requireNonNull(outputDir.listFiles()))
                    .filter(file1 -> file1.getName().startsWith("chunk_") && file1.getName().endsWith(".bin"))
                    .collect(Collectors.toList());

            RestTemplate restTemplate = new RestTemplate();
            List<String> peers = ns.getNodes();
            sendMetadata(file.getOriginalFilename(), chunkFiles.size(), uuid, peers);
            System.out.println("peers:" + peers);
            int peerCount = peers.size();
            if (peerCount == 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No peers available.");
            }

            for (int i = 0; i < chunkFiles.size(); i++) {
                String peerUrl = peers.get(i % peerCount) + "/receive";

                File chunkFile = chunkFiles.get(i);
                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                body.add("file", new FileSystemResource(chunkFile));
                body.add("filename", chunkFile.getName());
                body.add("uuid", uuid);

                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body);

                ResponseEntity<String> response = restTemplate.postForEntity(peerUrl, requestEntity, String.class);
                System.out.println(response.getStatusCode() + ": " + response.getBody());
                if (!response.getStatusCode().is2xxSuccessful()) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Failed to forward chunk " + chunkFile.getName() + " to peer: " + peerUrl);
                }

                chunkFile.delete();
            }

            return ResponseEntity.ok("File uploaded and spread across peers.");

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed.");
        }
    }

    private void sendMetadata(String filename, int totalChunks, String uuid, List<String> peers) {
        try {
            // Create metadata object
            Metadata metadata = new Metadata(filename, totalChunks, uuid);

            // Serialize metadata to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String metadataJson = objectMapper.writeValueAsString(metadata);

            // Create metadata.json file
            File metadataFile = new File("metadata.json");
            try (FileOutputStream fos = new FileOutputStream(metadataFile)) {
                fos.write(metadataJson.getBytes());
            }

            // Send metadata.json to all peers
            RestTemplate restTemplate = new RestTemplate();
            for (String peerUrl : peers) {
                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                body.add("file", new FileSystemResource(metadataFile));
                body.add("filename", "metadata.json");
                body.add("uuid", uuid);

                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body);
                ResponseEntity<String> response = restTemplate.postForEntity(peerUrl + "/receive",
                        requestEntity, String.class);

                if (!response.getStatusCode().is2xxSuccessful()) {
                    System.err.println("Failed to send metadata to peer: " + peerUrl);
                }
            }

            // Clean up metadata.json file after sending
            metadataFile.delete();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
