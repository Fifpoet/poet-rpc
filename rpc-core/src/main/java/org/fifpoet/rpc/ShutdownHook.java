package org.fifpoet.rpc;

import org.fifpoet.factory.ThreadPoolFactory;
import org.fifpoet.util.LogUtil;
import org.fifpoet.util.NacosUtil;

public class ShutdownHook {
    private static final ShutdownHook shutdownHook = new ShutdownHook();

    public static ShutdownHook getShutdownHook() {
        return shutdownHook;
    }

    public void addClearAllHook() {
        LogUtil.INFO().info("add shutdown hook: clear all registry. ");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NacosUtil.clearRegistry();
            ThreadPoolFactory.shutDownAll();
        }));
    }
}
