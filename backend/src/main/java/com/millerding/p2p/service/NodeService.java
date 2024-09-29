package com.millerding.p2p.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NodeService {

    private List<String> peerNodes;

    public NodeService() {
        peerNodes = new ArrayList<>();
    }

    public List<String> getNodes() {
        return peerNodes;
    }

    public void setNodes(List<String> nodes) {
        this.peerNodes = nodes;
    }

    public void addNode(String node) {
        this.peerNodes.add(node);
    }

    public void removeNode(String node) {
        this.peerNodes.remove(node);
    }
}
