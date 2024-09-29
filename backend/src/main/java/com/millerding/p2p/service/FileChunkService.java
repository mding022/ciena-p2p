package com.millerding.p2p.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileChunkService {
    public List<byte[]> chunkFile(String filePath) throws IOException {
        List<byte[]> fileChunks = new ArrayList<>();
        File file = new File(filePath);

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[512];
            int bytesRead;

            // Read the file in chunks of 512 bytes
            while ((bytesRead = fis.read(buffer)) != -1) {
                if (bytesRead < 512) {
                    // If the last chunk is smaller than 512 bytes, resize the array
                    byte[] lastChunk = new byte[bytesRead];
                    System.arraycopy(buffer, 0, lastChunk, 0, bytesRead);
                    fileChunks.add(lastChunk);
                } else {
                    fileChunks.add(buffer.clone());
                }
            }
        }

        return fileChunks;
    }
}