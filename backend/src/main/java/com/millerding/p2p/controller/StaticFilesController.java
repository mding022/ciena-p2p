package com.millerding.p2p.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/files")
@CrossOrigin(origins = "http://localhost:3000")
public class StaticFilesController {

    /*@Autowired
    private ResourceLoader resourceLoader;*/

    @GetMapping
    public ResponseEntity<List<String>> listStaticFiles() throws IOException {
        List<String> fileDetails = new ArrayList<>();
        Path staticPath = Paths.get("src/main/resources/static/");

        try (Stream<Path> directories = Files.walk(staticPath, 1).filter(Files::isDirectory)) {
            directories.forEach(folder -> {
                try {
                    File metadataFile = new File(folder.toFile(), "metadata.json");
                    if (metadataFile.exists()) {
                        String metadata = new String(Files.readAllBytes(metadataFile.toPath()));
                        JSONObject jsonObject = new JSONObject(metadata);
                        String originalFilename = jsonObject.getString("filename");
                        String uuid = jsonObject.getString("uuid");
                        int totalChunks = jsonObject.getInt("totalChunks"); 
                        long existingChunksCount = Files.list(folder)
                                .filter(p -> p.getFileName().toString().startsWith("chunk_")
                                        && p.getFileName().toString().endsWith(".bin"))
                                .count();
                            String chunkStatus = String.format("%d/%d", existingChunksCount, totalChunks);
                            fileDetails.add(String.format("%s;%s;%s", originalFilename, uuid, chunkStatus)); // Return
                                                                                                                 // chunk
                                                                                                                 // status
                    }
                } catch (IOException | JSONException ioe) {
                    ioe.printStackTrace();
                }
            });
        }

        return ResponseEntity.ok(fileDetails);
    }
}
