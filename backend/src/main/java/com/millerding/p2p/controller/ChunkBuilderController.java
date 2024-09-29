package com.millerding.p2p.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.json.JSONObject;
import org.json.JSONException;

@RestController
@CrossOrigin(origins = "localhost:3000")
public class ChunkBuilderController {

    @GetMapping("/build")
    public ResponseEntity<String> combineChunks(@RequestParam("uuid") String uuid) {
        File uuidFolder = new File("src/main/resources/static/" + uuid);
        if (!uuidFolder.exists() || !uuidFolder.isDirectory()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("UUID folder not found.");
        }

        File metadataFile = new File(uuidFolder, "metadata.json");
        String originalFilename = null;
        try {
            StringBuilder combinedContent = new StringBuilder();

            byte[] metadataBytes = Files.readAllBytes(metadataFile.toPath());
            String metadata = new String(metadataBytes);
            JSONObject jsonObject = new JSONObject(metadata);
            originalFilename = jsonObject.getString("filename");

            for (int i = 1;; i++) {
                File chunkFile = new File(uuidFolder, "chunk_" + i + ".txt");
                if (!chunkFile.exists()) {
                    break;
                }
                byte[] chunkBytes = Files.readAllBytes(chunkFile.toPath());
                combinedContent.append(new String(chunkBytes));
            }

            File originalFile = new File(uuidFolder, originalFilename);
            try (FileOutputStream fos = new FileOutputStream(originalFile)) {
                fos.write(combinedContent.toString().getBytes());
            }

            return ResponseEntity.ok("Combined chunks into: " + originalFilename);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error combining chunks: " + e.getMessage());
        } catch (JSONException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reading metadata: " + e.getMessage());
        }
    }

}
