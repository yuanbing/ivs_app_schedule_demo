package com.huawei.video.ivs.demo.server;

import com.google.inject.AbstractModule;
import com.huawei.video.ivs.model.IVSEdgeRegistry;
import com.huawei.video.ivs.scheduler.IVSAppScheduler;
import com.huawei.video.ivs.scheduler.IVSDemoAppScheduler;

public class IVSDemoAppModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(IVSAppScheduler.class).to(IVSDemoAppScheduler.class).asEagerSingleton();
        bind(IVSEdgeRegistry.class).to(IVSDemoEdgeRegistry.class).asEagerSingleton();

        install(new JerseyModule());
    }
}
