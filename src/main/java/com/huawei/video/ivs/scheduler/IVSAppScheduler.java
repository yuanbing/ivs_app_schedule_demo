package com.huawei.video.ivs.scheduler;

import com.huawei.video.ivs.model.IVSAppTaskInfo;
import com.huawei.video.ivs.model.IVSEdgeInfo;

import java.util.Collection;
import java.util.Map;

public interface IVSAppScheduler {
    String DEFAULT_NODE_NAME = "LOCAL";

    Map<String, IVSAppTaskInfo> scheduleAppTask(final String appName, final Map<String, Object> appInput, final Collection<IVSEdgeInfo> edgeNodes);
}
