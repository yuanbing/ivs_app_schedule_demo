package com.huawei.video.ivs.model;


import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("IVSApplicationInfo")
public class IVSAppInfo {
    private String name;
    private String version;

    public IVSAppInfo(final String name, final String version) {
        this.name = name;
        this.version = version;
    }

    @JsonGetter("appName")
    public String getName() {
        return name;
    }

    @JsonSetter("appName")
    public void setName(String name) {
        this.name = name;
    }

    @JsonGetter("appVersion")
    public String getVersion() {
        return version;
    }

    @JsonSetter("appVersion")
    public void setVersion(String version) {
        this.version = version;
    }
}
