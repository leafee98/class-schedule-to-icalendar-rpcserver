package com.github.leafee98.CSTI.rpcserver;

import java.io.IOException;

import com.github.leafee98.CSTI.rpcserver.rpc.RPCServerImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    private RPCServerImpl server;
    private int listenPort;

    private void initDefaultConfigure() {
        this.listenPort = 8047;
    }

    private void parseParameter(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-l")) {
                this.listenPort = Integer.parseInt(args[i + 1]);
                i += 1;
            }
        }
    }

    private void logConfigure() {
        logger.info("showing the configuration");
        logger.info("configure: listenPort = {}", this.listenPort);
    }

    private void startServer() throws IOException {
        server = new RPCServerImpl(this.listenPort);
        server.start();
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // use system error stream to print log
            // since logger is not available in shutdown hook
            System.err.println("rpcserver stopping ...");
            try {
                server.stop();
            } catch (InterruptedException e) {
                System.err.println("error while shutting down");
                e.printStackTrace(System.err);
            }
            System.err.println("rpcserver shutdown");
        }));
    }

    private void blockUntilShutdown() throws InterruptedException {
        server.blockUntilShutdown();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        App app = new App();
        app.initDefaultConfigure();
        app.parseParameter(args);
        app.logConfigure();

        app.startServer();
        app.addShutdownHook();
        app.blockUntilShutdown();
    }
}
