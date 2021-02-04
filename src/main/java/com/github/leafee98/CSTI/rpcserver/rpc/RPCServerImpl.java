package com.github.leafee98.CSTI.rpcserver.rpc;

import com.github.leafee98.CSTI.core.bean.ScheduleObject;
import com.github.leafee98.CSTI.core.bean.loader.JSONLoader;
import com.github.leafee98.CSTI.core.generate.Generator;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RPCServerImpl {
    private static final Logger logger = LoggerFactory.getLogger(RPCServerImpl.class);

    private final Server server;

    public void start() throws IOException {
        logger.info("starting rpc server ...");
        server.start();
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    public RPCServerImpl(int port) {
        this.server = ServerBuilder.forPort(port)
                .addService(new RPCService())
                .addService(ProtoReflectionService.newInstance())
                .build();
    }

    private static class RPCService extends CSTIRpcServerGrpc.CSTIRpcServerImplBase {
        private final JSONLoader jsonLoader;

        public RPCService() {
            this.jsonLoader = new JSONLoader();
        }

        @Override
        public void jsonGenerate(ConfJson request, StreamObserver<ResultIcal> responseObserver) {
            try {
                String content = request.getContent();
                ResultIcal.Builder builder =  ResultIcal.newBuilder();
                ScheduleObject scheduleObject = jsonLoader.load(content);
                Generator generator = new Generator(scheduleObject);
                String generated = generator.generate().toString();
                builder.setContent(generated);

                responseObserver.onNext(builder.build());
                responseObserver.onCompleted();

                logger.info("handle rpc jsonGenerate success");
            } catch (Exception e) {
                responseObserver.onError(new StatusRuntimeException(Status.ABORTED.withDescription(
                        "error while transforming, maybe the request content format wrong.\n" +
                                "exception detail: " + e.toString())));

                logger.error("handle rpc jsonGenerate failed, exception detail follow:");
                logger.error(e.toString());
            }
        }

        @Override
        public void tomlGenerate(ConfToml request, StreamObserver<ResultIcal> responseObserver) {
            String errMsg = "not implemented rpc tomlGenerate called, respond error";
            logger.warn(errMsg);
            responseObserver.onError(new Exception(errMsg));
        }
    }
}
