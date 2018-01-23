package com.huawei.video.ivs.demo.server.resources;

import com.google.inject.Inject;
import com.huawei.video.ivs.demo.server.Main;
import com.huawei.video.ivs.model.IVSAppTaskInfo;
import com.huawei.video.ivs.model.IVSEdgeRegistry;
import com.huawei.video.ivs.scheduler.IVSAppScheduler;
import com.netflix.conductor.client.http.TaskClient;
import com.netflix.conductor.client.http.WorkflowClient;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.*;
import java.util.stream.Collectors;

@Path("/demoapp")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class ApiResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiResource.class);

    private static String DEMO_WORKFLOW = "ivs_demo_app";
    private static String DEMO_SERVICE_NAME = "IvsDemoApp";
    private static String DEMO_APP_SCHEDULING_TASK_NAME = "ivs_demo_app_scheduling_task";
    private static String DEMO_APP_SCHEDULED_TASK_NAME = "ivs_demo_app_scheduled_task";
    private static Integer POLL_TASK_TIMEOUT = 5000; // in milliseconds

    private final IVSAppScheduler scheduler;
    private final IVSEdgeRegistry edgeRegistry;

    private String conductorUrl;

    @Inject
    public ApiResource(final IVSAppScheduler scheduler, final IVSEdgeRegistry edgeRegistry) {
        this.scheduler = scheduler;
        this.edgeRegistry = edgeRegistry;
    }

    @PostConstruct
    public void init() {
        conductorUrl = Main.DEFAULT_CONDUCTOR_URL;
        if (System.getProperty(Main.CONDUCTOR_URL_PROPERTY) != null) {
            conductorUrl = System.getProperty(Main.CONDUCTOR_URL_PROPERTY);
        }
    }

    @POST
    @Produces({ MediaType.TEXT_PLAIN })
    public String startDemoApp(final Map<String, Object> input) {
        // Call conductor to start the workflow "demo"
        final WorkflowClient workflowClient = new WorkflowClient();
        workflowClient.setRootURI(conductorUrl);

        final StartWorkflowRequest startWorkflowRequest = new StartWorkflowRequest();

        // get the list of domains for scheduled tasks
        Map<String, String> domains = new HashMap<>();
        final Collection<String> listOfDomains = new LinkedList<>();
        listOfDomains.add(IVSAppScheduler.DEFAULT_NODE_NAME);
        edgeRegistry.getEdges().stream().forEach(ivsEdgeInfo -> listOfDomains.add(ivsEdgeInfo.getName()));
        domains.put(DEMO_APP_SCHEDULED_TASK_NAME, listOfDomains.stream().collect(Collectors.joining(",")));

        startWorkflowRequest.withVersion(1)
                .withName(DEMO_WORKFLOW)
                .withInput(input)
                .withTaskToDomain(domains);
        final String workflowId = workflowClient.startWorkflow(startWorkflowRequest);
        LOGGER.info("Workflow %s has started, instanceId: %s", DEMO_WORKFLOW, workflowId);

        // Pull the first task of "demo" workflow that is always a fork-dynamic-join task
        TaskClient taskClient = new TaskClient();
        final List<Task> pendingTasks = taskClient.poll(DEMO_APP_SCHEDULING_TASK_NAME, DEMO_SERVICE_NAME, 1, POLL_TASK_TIMEOUT);
        final String taskId = pendingTasks.get(0).getTaskId();
        LOGGER.info("Task Id for Demo Application Scheduling task is: %s", taskId);

        // Schedule demo app task to proper edges
        Map<String, IVSAppTaskInfo> scheduledTasks = scheduler.scheduleAppTask(DEMO_SERVICE_NAME, input, edgeRegistry.getEdges());
        LOGGER.info("Got %d scheduled tasks", scheduledTasks.size());

        TaskResult taskResult = new TaskResult();
        taskResult.setTaskId(taskId);
        taskResult.setStatus(TaskResult.Status.COMPLETED);
        Map<String, Object> taskOutput = new HashMap<>();
        final Collection<Map<String, String>> listOfTasks = new LinkedList<>();
        final Map<String, Object> taskInputs = new HashMap<>();
        final int[] taskIndex = new int[1];
        taskIndex[0] = 0;

        scheduledTasks.forEach((hostName, taskInfo) -> {
            ++taskIndex[0];
            Map<String, String> task = new HashMap<>();
            task.put("name", DEMO_APP_SCHEDULED_TASK_NAME);
            final String taskReferenceName = workflowId + "_" + taskId + "_" + taskIndex[0];
            task.put("taskReferenceName", taskReferenceName);
            task.put("type", "SIMPLE");
            listOfTasks.add(task);
            taskInputs.put(taskReferenceName, taskInfo.getInput());
        });

        taskOutput.put("tasks", listOfTasks);
        taskOutput.put("taskInputs", taskInputs);
        taskResult.setOutputData(taskOutput);

        // Mark the first task completed with output being the scheduled tasks
        taskClient.updateTask(taskResult);

        return workflowId;
    }
}
