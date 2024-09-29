package com.millerding.p2p.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@CrossOrigin("http://localhost:3000")
public class FileReceiveController {

    @PostMapping("/receive")
    public ResponseEntity<String> receiveFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("filename") String filename,
            @RequestParam(value = "uuid", required = false) String uuid) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty.");
        }

        try {
            if (uuid == null || uuid.isEmpty()) {
                uuid = UUID.randomUUID().toString();
            }

            File directory = new File("src/main/resources/static/" + uuid);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File receivedFile = new File(directory, filename);
            try (FileOutputStream fos = new FileOutputStream(receivedFile)) {
                fos.write(file.getBytes());
            }

            // Return a response indicating the file is saved
            String fileUrl = "localhost:8080/" + uuid + "/" + filename;
            return ResponseEntity.ok("File received and saved as: " + fileUrl);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File receive failed.");
        }
    }
}
