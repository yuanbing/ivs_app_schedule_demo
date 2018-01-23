package com.huawei.video.ivs.model;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("Algorithm")
public class IVSAlgorithm {
    private String name;
    private String version;

    public IVSAlgorithm(final String name, final String version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
