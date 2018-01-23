package com.huawei.video.ivs.scheduler;

import com.huawei.video.ivs.model.IVSAppTaskInfo;
import com.huawei.video.ivs.model.IVSEdgeInfo;

import java.util.*;
import java.util.stream.Collectors;

public class IVSDemoAppScheduler implements IVSAppScheduler {

    @Override
    public Map<String, IVSAppTaskInfo> scheduleAppTask(final String appName, Map<String, Object> appInput, Collection<IVSEdgeInfo> edgeNodes) {
        // The assumed app inputs have the following structure
        // "cameras": collection of camera IDs. Each camera ID is represented as a Java String
        // "algorithms": collection of algorithm names. Each algorithm name is represented as a Java String
        // the scheduling algorithm will assign the tasks to the edge node if:
        //  1. the input camera is attached to it;
        //  2. it has the asked algorithms installed;
        // All unmatched inputs will be assigned to the default node that is the LOCAL "node"

        Collection<String> cameras = (Collection<String>)appInput.get("cameras");
        Collection<String> algorithms = (Collection<String>)appInput.get("algorithms");

        Collection<IVSEdgeInfo> assignableEdgeNodes = edgeNodes.stream().filter(ivsEdgeInfo ->
                ivsEdgeInfo.anyCameraAttached(cameras) && ivsEdgeInfo.algorithmsInstalled(algorithms, true)
        ).collect(Collectors.toList());


        final Map<String, Boolean> assignmentOfCameras = new HashMap<>();
        cameras.stream().forEach(camera-> assignmentOfCameras.put(camera, Boolean.FALSE));

        Map<String, IVSAppTaskInfo> tasks = new HashMap<>();

        assignableEdgeNodes.stream().forEach(ivsEdgeInfo -> {
            if (!tasks.containsKey(ivsEdgeInfo.getName())) {
               Map<String, Object> taskInput = new HashMap<>();
               taskInput.put("cameras", new LinkedList<String>());
               taskInput.put("algorithms", algorithms);
               tasks.put(ivsEdgeInfo.getName(), new IVSAppTaskInfo(appName, ivsEdgeInfo.getName(), taskInput));
            }

            final IVSAppTaskInfo task = tasks.get(ivsEdgeInfo.getName());
            cameras.stream().forEach(camera -> {
                if (!assignmentOfCameras.get(camera)) {
                    ((Collection<String>)task.getInput().get("cameras")).add(camera);
                    assignmentOfCameras.put(camera, Boolean.TRUE);
                }
            });
        });

        // Process any unassigned cameras
        Collection<String> unassignedCameras = cameras.stream()
                .filter(camera -> !assignmentOfCameras.get(camera))
                .collect(Collectors.toList());

        if (!unassignedCameras.isEmpty()) {
            Map<String, Object> taskInput = new HashMap<>();
            taskInput.put("cameras", unassignedCameras);
            taskInput.put("algorithms", algorithms);
            tasks.put(DEFAULT_NODE_NAME, new IVSAppTaskInfo(appName, DEFAULT_NODE_NAME, taskInput));
        }

        return tasks;
    }
}
