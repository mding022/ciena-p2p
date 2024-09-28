package com.millerding.p2p.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.millerding.p2p.service.TunnelService;

@RestController
@CrossOrigin("http://localhost:3000")
public class ConnectionController {
    TunnelService ts = new TunnelService();

    @GetMapping("/auth")
    public String connect() {
        try {
            return ts.createTunnel().get();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @GetMapping("/")
    public String success() {
        return "Successfully connected to this server";
    }
}
