package com.millerding.p2p.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.json.JSONObject;
import org.json.JSONException;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ChunkBuilderController {
    @PostMapping("/build")
    public String combineChunks(@RequestParam("uuid") String uuid) {
        File uuidFolder = new File("src/main/resources/static/" + uuid);
        if (!uuidFolder.exists() || !uuidFolder.isDirectory()) {
            return null;
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
                return null;
            }

            return null;
        } catch (IOException | InterruptedException e) {
            return null;
        } catch (JSONException e) {
            return null;
        }
    }
}