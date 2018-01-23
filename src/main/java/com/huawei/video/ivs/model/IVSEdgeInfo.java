package com.huawei.video.ivs.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.*;

@JsonTypeName("IVSEdgeNode")
public class IVSEdgeInfo {
    private String name;
    private Map<String, IVSCameraInfo> attachedCameras;
    private Map<String, IVSAppInfo> services;
    private Map<String, IVSAlgorithm> algorithms;

    public IVSEdgeInfo(final String name) {
        this.name = name;
        this.attachedCameras = new HashMap<>();
        this.services = new HashMap<>();
        this.algorithms = new HashMap<>();
    }

    @JsonGetter("edgeNodeName")
    public String getName() {
        return name;
    }

    @JsonSetter("edgeNodeName")
    public void setName(String name) {
        this.name = name;
    }

    @JsonGetter("cameras")
    public Collection<IVSCameraInfo> getAttachedCameras() {
        if (attachedCameras.isEmpty()) {
            return Collections.EMPTY_LIST;
        } else {
            return Collections.unmodifiableCollection(attachedCameras.values());
        }
    }

    @JsonSetter("cameras")
    public void setAttachedCameras(final Collection<IVSCameraInfo> cameras) {
        if (!cameras.isEmpty()) { // only do this when the input list of cameras is NOT empty
            attachedCameras.clear();
            cameras.stream().forEach(ivsCameraInfo -> attachedCameras.put(ivsCameraInfo.getId(), ivsCameraInfo));
        }
    }

    @JsonGetter("services")
    public Collection<IVSAppInfo> getProvidedServices() {
        if (services.isEmpty()) {
            return Collections.EMPTY_LIST;
        } else {
            return Collections.unmodifiableCollection(services.values());
        }
    }

    @JsonSetter("services")
    public void setProvidedServices(final Collection<IVSAppInfo> providedServices) {
        if (!providedServices.isEmpty()) {
            services.clear();
            providedServices.stream().forEach(ivsAppInfo -> services.put(ivsAppInfo.getName(), ivsAppInfo));
        }
    }

    @JsonGetter("algorithms")
    public Collection<IVSAlgorithm> getInstalledAlgorithms() {
        if (algorithms.isEmpty()) {
            return Collections.EMPTY_LIST;
        } else {
            return Collections.unmodifiableCollection(algorithms.values());
        }
    }

    @JsonSetter("algorithms")
    public void installAlgorithms(final Collection<IVSAlgorithm> algorithmsToInstall) {
        if (!algorithmsToInstall.isEmpty()) {
            algorithms.clear();
            algorithmsToInstall.stream().forEach(ivsAlgorithm -> algorithms.put(ivsAlgorithm.getName(), ivsAlgorithm));
        }
    }

    // utility methods
    @JsonIgnore
    public boolean isCameraAttached(final String cameraId) {
        return attachedCameras.containsKey(cameraId);
    }

    @JsonIgnore
    public Optional<IVSCameraInfo> getCameraById(final String cameraId) {
        if (attachedCameras.containsKey(cameraId)) {
            return Optional.of(attachedCameras.get(cameraId));
        } else {
            return Optional.ofNullable(null);
        }
    }

    @JsonIgnore
    public boolean anyCameraAttached(final Collection<String> cameraIds) {
        return cameraIds.stream().filter(cameraId -> isCameraAttached(cameraId)).count() != 0L;
    }

    @JsonIgnore
    public boolean isAlgorithmInstalled(final String algorithmName) {
        return this.algorithms.containsKey(algorithmName);
    }

    @JsonIgnore
    public boolean algorithmsInstalled(final Collection<String> algorithms, boolean all) {
        long targetNumberOfAlgorithms = algorithms.size();
        return algorithms.stream().filter(algorithmName -> isAlgorithmInstalled(algorithmName)).count() == targetNumberOfAlgorithms;
    }

}
