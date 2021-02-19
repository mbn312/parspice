package parspice;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import parspice.rpc.ParSpiceGrpc;
import spice.basic.CSPICE;

###IMPORTS###

public class ParSpice {
    private ParSpiceGrpc.ParSpiceFutureStub stub;

    public ParSpice() {
        String target = "localhost:50051";
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                .usePlaintext()
                .build();
        stub = ParSpiceGrpc.newFutureStub(channel);
    }

    ###FACTORIES###
}
