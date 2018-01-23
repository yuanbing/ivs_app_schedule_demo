package com.huawei.video.ivs.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("IVSCamera")
public class IVSCameraInfo {
    public enum OutputFormat {VIDEO_H264, PNG, JPEG}

    private String id;
    private OutputFormat outputFormat;

    public IVSCameraInfo(final String cameraId) {
        this.id = cameraId;
        this.outputFormat = OutputFormat.VIDEO_H264;
    }

    public IVSCameraInfo(final String cameraId, final OutputFormat outputFormat) {
        this.id = cameraId;
        this.outputFormat = outputFormat;
    }

    @JsonGetter("cameraId")
    public String getId() {
        return id;
    }

    @JsonSetter("cameraId")
    public void setId(String id) {
        this.id = id;
    }

    @JsonGetter("cameraOutputFormat")
    public OutputFormat getOutputFormat() {
        return outputFormat;
    }

    @JsonSetter("cameraOutputFormat")
    public void setOutputFormat(OutputFormat outputFormat) {
        this.outputFormat = outputFormat;
    }
}
