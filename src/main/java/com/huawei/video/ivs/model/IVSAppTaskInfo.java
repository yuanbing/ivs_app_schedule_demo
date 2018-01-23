package com.huawei.video.ivs.model;

import java.util.Map;

public class IVSAppTaskInfo {
    private String appName;
    private String location;
    private Map<String, Object> input;

    public IVSAppTaskInfo(final String appName, final String location, final Map<String, Object> input) {
        this.appName = appName;
        this.location = location;
        this.input = input;
    }

    public String getAppName() {
        return appName;
    }

    public String getLocation() {
        return location;
    }

    public Map<String, Object> getInput() {
        return input;
    }
}
