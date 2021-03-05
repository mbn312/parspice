package parspice;

import io.grpc.stub.StreamObserver;
import parspice.rpc.ParSPICEGrpc;

import java.util.ArrayList;

public abstract class Batch<C extends Call, R extends com.google.protobuf.GeneratedMessageV3> {
    protected ArrayList<C> calls = new ArrayList<C>();
    protected int unsentIndex = 0;

    public abstract void sendRequest(ParSPICEGrpc.ParSPICEStub stub, int howMany, StreamObserver<R> awaiterTask);

    public abstract void receiveResponse(R response);

    public void run() {
        // dispatcher.performDistributedTask(this);

        // OR maybe
        /* dispatcher.performDistributedTask(
                this::sendRequest,
                this::receiveResponse
           );
         */
        // doesn't matter to me
    }
}
