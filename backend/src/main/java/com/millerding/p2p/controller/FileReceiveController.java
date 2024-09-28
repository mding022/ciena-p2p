package com.millerding.p2p.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RestController
@CrossOrigin("http://localhost:3000")
public class FileReceiveController {

    // Endpoint to receive a file from another peer
    @PostMapping("/receive")
public ResponseEntity<String> receiveFile(
        @RequestParam("file") MultipartFile file, 
        @RequestParam("filename") String filename) {
    if (file.isEmpty()) {
        return ResponseEntity.badRequest().body("File is empty.");
    }

    try {
        // Save the received file locally
        File receivedFile = new File(filename);
        try (FileOutputStream fos = new FileOutputStream("src/main/resources/static/"+receivedFile)) {
            fos.write(file.getBytes());
        }

        return ResponseEntity.ok("File received and saved as: localhost:8080/" + filename);

    } catch (IOException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File receive failed.");
    }
}
}
