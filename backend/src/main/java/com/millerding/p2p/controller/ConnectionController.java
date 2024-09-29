package com.millerding.p2p.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.millerding.p2p.service.NodeService;
import com.millerding.p2p.service.TunnelService;

@RestController
@CrossOrigin("http://localhost:3000")
public class ConnectionController {

    TunnelService ts = new TunnelService();

    @Autowired
    private NodeService ns;

    @GetMapping("/auth")
    public String connect() {
        try {
            String selfNode = ts.createTunnel().get();
            ns.addNode(selfNode);
            return selfNode;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @PostMapping("/addnode")
    public String addNode(@RequestParam("tunnel") String tunnelUrl) {
        ns.addNode(tunnelUrl);
        System.out.println("Added : " + tunnelUrl);
        return "Success";
    }

    @GetMapping("/")
    public String success() {
        return "Successfully connected to this server";
    }
}
