package com.huawei.video.ivs.demo.server;

public class Main {
    private static final String IVS_DEMO_APP_PORT_PROPERTY = "ivs.demo.app.port";
    private static final int IVS_DEMO_APP_DEFAULT_PORT = 7001;

    public static final String CONDUCTOR_URL_PROPERTY = "ivs.demo.conductor.url";
    public static final String DEFAULT_CONDUCTOR_URL = "http://localhost:8080/";

    public static void main(String[] args) throws Exception {
        int port = IVS_DEMO_APP_DEFAULT_PORT;
        if (System.getProperty(IVS_DEMO_APP_PORT_PROPERTY) != null) {
            port = Integer.parseInt(System.getProperty(IVS_DEMO_APP_PORT_PROPERTY));
        }

        String conductorUrl = DEFAULT_CONDUCTOR_URL;
        if (System.getProperty(CONDUCTOR_URL_PROPERTY) != null) {
            conductorUrl = System.getProperty(CONDUCTOR_URL_PROPERTY);
        }

        final IVSDemoAppServer server = new IVSDemoAppServer();
        System.out.println("Starting IVS Demo Application Server at port " + port);

        server.start(conductorUrl, port, true);
    }
}
