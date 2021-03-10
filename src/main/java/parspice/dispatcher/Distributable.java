package parspice.dispatcher;

import io.grpc.stub.StreamObserver;
import parspice.rpc.ParSPICEGrpc;

public interface Distributable<REQ, RES> {
    public REQ nextRequest(int size);
    public void sendRequest(REQ request, ParSPICEGrpc.ParSPICEStub stub, StreamObserver<RES> awaiterTask);
    public void receiveResponse(RES response);
}
