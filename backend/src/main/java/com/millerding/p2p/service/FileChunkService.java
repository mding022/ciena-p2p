package com.millerding.p2p.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileChunkService {

    public List<byte[]> chunkFile(String filePath) throws IOException {
        File file = new File(filePath);
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        List<byte[]> chunks = new ArrayList<>();
        
        for (int i = 0; i < fileBytes.length; i += 512) {
            int length = Math.min(512, fileBytes.length - i);
            byte[] chunk = new byte[length];
            System.arraycopy(fileBytes, i, chunk, 0, length);
            chunks.add(chunk);
        }
        
        return chunks;
    }
}
