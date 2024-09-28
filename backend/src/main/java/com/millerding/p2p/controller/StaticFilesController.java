package com.millerding.p2p.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/files")
@CrossOrigin(origins = "http://localhost:3000")
public class StaticFilesController {

    @Autowired
    private ResourceLoader resourceLoader;

    @GetMapping
    public ResponseEntity<List<String>> listStaticFiles() throws IOException {
        List<String> fileDetails = new ArrayList<>();

        // Load the resources from src/main/resources/static/
        Resource resource = resourceLoader.getResource("classpath:/static/");
        Path path = Paths.get(resource.getURI());

        // Recursively list all files and return formatted strings
        fileDetails = Files.walk(path)
                .filter(Files::isRegularFile)
                .map(p -> {
                    String fileName = p.getFileName().toString(); // Extract file name
                    long fileSize = p.toFile().length(); // Get file size
                    return String.format("%s;%d;%s", fileName, fileSize, System.getProperty("user.name")); // Format as "filename;filesize;miller"
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(fileDetails);
    }
}
