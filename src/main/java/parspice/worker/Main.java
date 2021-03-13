package parspice.worker;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class Main {
    public static void main(String[] args) throws Exception {

        System.loadLibrary("JNISpice");

        int serverPort = Integer.parseInt(args[0]);

        // create sever
        Server server = ServerBuilder.forPort(serverPort)
                .addService(new SpiceService())
                .build()
                .start();

        if (server != null) {
            server.awaitTermination();
        }
    }
}
