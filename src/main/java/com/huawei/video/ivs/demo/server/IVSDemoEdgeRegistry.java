package com.huawei.video.ivs.demo.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huawei.video.ivs.model.IVSEdgeInfo;
import com.huawei.video.ivs.model.IVSEdgeRegistry;

import javax.annotation.PostConstruct;
import java.util.Collection;

public class IVSDemoEdgeRegistry implements IVSEdgeRegistry {
    private Collection<IVSEdgeInfo> edges;

    @PostConstruct
    public void init() throws Exception {
        TypeReference<Collection<IVSEdgeInfo>> typeInfo = new TypeReference<Collection<IVSEdgeInfo>>() {};
        ObjectMapper objectMapper = new ObjectMapper();
        edges = objectMapper.readValue(Main.class.getClass().getResource("ivsdemoedgeinfo.json"), typeInfo);
    }

    @Override
    public Collection<IVSEdgeInfo> getEdges() {
        return edges;
    }
}
