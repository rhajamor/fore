package org.fore.app;

import io.quarkus.arc.Arc;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.fore.core.Engine;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * GLFW on macOS requires the Cocoa main thread (Thread 0). Quarkus runs
 * {@code QuarkusApplication.run()} on a separate thread, so we start
 * Quarkus in background for CDI and run the render loop from our own
 * {@code main()} which IS Thread 0 (with {@code -XstartOnFirstThread}).
 */
public class ForeApplication {

    static final CountDownLatch CDI_READY = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {
        Thread quarkusThread = new Thread(
                () -> Quarkus.run(EngineBootstrap.class, (code, err) -> {}, args),
                "quarkus-bootstrap"
        );
        quarkusThread.setDaemon(true);
        quarkusThread.start();

        if (!CDI_READY.await(15, TimeUnit.SECONDS)) {
            System.err.println("FORE: Quarkus CDI container failed to start within 15s.");
            System.exit(1);
        }

        Engine engine = Arc.container().instance(Engine.class).get();
        try {
            engine.run();
        } finally {
            Quarkus.asyncExit();
        }
    }

    @QuarkusMain
    public static class EngineBootstrap implements QuarkusApplication {
        @Override
        public int run(String... args) {
            CDI_READY.countDown();
            Quarkus.waitForExit();
            return 0;
        }
    }
}
