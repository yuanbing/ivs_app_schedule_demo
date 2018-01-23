package com.huawei.video.ivs.demo.server;

import com.google.inject.Guice;
import com.google.inject.servlet.GuiceFilter;
import com.sun.jersey.api.client.Client;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.EnumSet;

public class IVSDemoAppServer {
    private static Logger logger = LoggerFactory.getLogger(IVSDemoAppServer.class);

    private IVSDemoAppModule sm;
    private Server server;

    public IVSDemoAppServer() {
        this.sm = new IVSDemoAppModule();
    }

    public void start(String conductorUrl, int port, boolean join) throws Exception {
        if(server != null) {
            throw new IllegalStateException("Server is already running");
        }

        Guice.createInjector(sm);

        //Swagger
        //String resourceBasePath = Main.class.getResource("/swagger-ui").toExternalForm();
        this.server = new Server(port);

        ServletContextHandler context = new ServletContextHandler();
        context.addFilter(GuiceFilter.class, "/*", EnumSet.allOf(DispatcherType.class));
        //context.setResourceBase(resourceBasePath);
        //context.setWelcomeFiles(new String[] { "index.html" });

        server.setHandler(context);


        DefaultServlet staticServlet = new DefaultServlet();
        context.addServlet(new ServletHolder(staticServlet), "/*");

        server.start();

        logger.info("Started server on http://localhost:%d/", port);

        logger.info("Create demo workflow and tasks");
        registryDemoWorkflowAndTaskDefs(conductorUrl);

        if(join) {
            server.join();
        }
    }

    private void registryDemoWorkflowAndTaskDefs(String conductorUrl) {
        final Client client = Client.create();

        // register tasks
        InputStream stream = IVSDemoAppServer.class.getResourceAsStream("/ivsdemoapptasks.json");
        client.resource(conductorUrl + "/api/metadata/taskdefs").type(MediaType.APPLICATION_JSON).post(stream);
        logger.info("IVS Demo Workflow tasks registered");

        // register workflow
        stream = IVSDemoAppServer.class.getResourceAsStream("/ivsdemoapp.json");
        client.resource(conductorUrl + "/api/metadata/workflow").type(MediaType.APPLICATION_JSON).post(stream);
        logger.info("IVS Demo Workflow registered");
    }
}
