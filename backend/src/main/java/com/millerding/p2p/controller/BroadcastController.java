package com.millerding.p2p.controller;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.millerding.p2p.service.FileChunkService;
import com.millerding.p2p.service.NodeService;

@RestController
@CrossOrigin("http://localhost:3000")
public class BroadcastController {

    @Autowired
    private NodeService ns;

    FileChunkService fcs = new FileChunkService();

    @PostMapping("/broadcast")
    public String broadcastRequest(@RequestParam("uuid") String uuid) {
        RestTemplate restTemplate = new RestTemplate();
        int self = 0; // prevent it from repeating
        for (String tunnelUrl : ns.getNodes()) {
            if (self == -1) { //TODO: set to 0
                self++;
            } else {
                try {
                    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                    body.add("tunnel", ns.getNodes().get(0));
                    body.add("uuid", uuid);

                    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body);

                    // Send the file to the specified tunnel URL
                    ResponseEntity<String> response = restTemplate.postForEntity(tunnelUrl + "/get_uuid", requestEntity,
                            String.class);
                } catch (Exception e) {
                    return "Error requesting for " + uuid + " from tunnel: " + tunnelUrl;
                }
            }
        }
        return "Successfully requested for " + uuid;
    }

    @PostMapping("/get_uuid")
    public ResponseEntity<String> sendUUID(@RequestParam("uuid") String uuid,
            @RequestParam("tunnel") String tunnelUrl) {
        try {
            File uuidFolder = new File("src/main/resources/static/" + uuid);
            if (!uuidFolder.exists() || !uuidFolder.isDirectory()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("UUID folder not found.");
            }

            File[] files = uuidFolder.listFiles();
            if (files == null || files.length == 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No files found in the UUID folder.");
            }

            RestTemplate restTemplate = new RestTemplate();

            // Send each file in the UUID directory
            for (File file : files) {
                if (file.isFile()) {
                    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                    body.add("file", new FileSystemResource(file));
                    body.add("filename", file.getName());
                    body.add("uuid", uuid);

                    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body);

                    // Send the file to the specified tunnel URL
                    ResponseEntity<String> response = restTemplate.postForEntity(tunnelUrl + "/receive", requestEntity,
                            String.class);
                    if (!response.getStatusCode().is2xxSuccessful()) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Failed to send file: " + file.getName() + " to tunnel: " + tunnelUrl);
                    }
                }
            }

            return ResponseEntity.ok("All files from UUID " + uuid + " sent successfully to " + tunnelUrl);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while sending files.");
        }
    }
}
