package com.millerding.p2p.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin("http://localhost:3000")
public class FileController {

    // Endpoint for uploading a file and forwarding it to a peer's tunnel
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
            @RequestParam("tunnelUrl") String tunnelUrl) {
        try {
            // Save the uploaded file locally
            File convFile = new File(file.getOriginalFilename());
            try (FileOutputStream fos = new FileOutputStream(convFile)) {
                fos.write(file.getBytes());
            }

            // Prepare to forward the file to another peer (tunnel URL)
            RestTemplate restTemplate = new RestTemplate();
            String peerUrl = tunnelUrl + "/receive";

            // Create a MultiValueMap to hold the file and filename
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(convFile)); // Use FileSystemResource to send the file
            body.add("filename", file.getOriginalFilename());

            // Create the HTTP entity with the body and appropriate headers
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body);

            // Make the POST request to the peer
            ResponseEntity<String> response = restTemplate.postForEntity(peerUrl, requestEntity, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to forward the file to peer.");
            }

            return ResponseEntity.ok("File uploaded and forwarded to peer at: " + tunnelUrl);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed.");
        }
    }

}
