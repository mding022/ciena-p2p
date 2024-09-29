package com.millerding.p2p.service;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

@Service
public class NodeService {
    private ArrayList<String> nodes = new ArrayList<String>();

    public ArrayList<String> getNodes() {
        return nodes;
    }

    public void addNode(String tunnelUrl) {
        nodes.add(tunnelUrl);
    }

    public void removeNode(String tunnelUrl) { 
        nodes.remove(tunnelUrl);
    }
    
}
