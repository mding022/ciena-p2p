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
            byte[] metadataBytes = Files.readAllBytes(metadataFile.toPath());
            String metadata = new String(metadataBytes);
            JSONObject jsonObject = new JSONObject(metadata);
            originalFilename = jsonObject.getString("filename");

            String outputFilePath = new File(uuidFolder, originalFilename).getAbsolutePath();
            String pythonScriptPath = "builder.py"; // Adjust the path to your Python script if necessary

            ProcessBuilder processBuilder = new ProcessBuilder("python3", pythonScriptPath, uuidFolder.getAbsolutePath(),
                    outputFilePath);
            Process process = processBuilder.start();

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to combine chunks using Python script.");
            }

            return ResponseEntity.ok("Combined chunks into: " + originalFilename);
        } catch (IOException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error combining chunks: " + e.getMessage());
        } catch (JSONException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reading metadata: " + e.getMessage());
        }
    }
}