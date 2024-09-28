package com.millerding.p2p.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;

@Service
@CrossOrigin("http://localhost:3000")
public class TunnelService {

    @Async
    public CompletableFuture<String> createTunnel() {
        String command = "ssh -R 80:localhost:8080 serveo.net";
        StringBuilder output = new StringBuilder();
        String tunnelUrl = null;

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                    if (line.contains("https://")) {
                        tunnelUrl = line;
                        break;
                    }
                }
            }

            System.out.println("Tunnel output: " + output.toString());

            return CompletableFuture.completedFuture(tunnelUrl.substring(34));

        } catch (Exception e) {
            System.err.println("Error creating tunnel: " + e.getMessage());
            return CompletableFuture.completedFuture(null);
        }
    }
}
