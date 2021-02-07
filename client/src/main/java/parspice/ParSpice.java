package parspice;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import parspice.rpc.ParSpiceGrpc;

public class ParSpice {
    private ParSpiceGrpc.ParSpiceBlockingStub stub;

    public ParSpice() {
        String target = "localhost:50051";
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                .usePlaintext()
                .build();
        stub = ParSpiceGrpc.newBlockingStub(channel);
    }
}
