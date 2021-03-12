package parspice.dispatcher;

import parspice.rpc.ParSPICEGrpc.ParSPICEStub;

public interface DistributedTaskStateDistributable<
        T_Request extends com.google.protobuf.MessageOrBuilder,
        T_Response extends com.google.protobuf.MessageOrBuilder> {
    //TODO: add size tracking
    //dispatcher will ask for request of size n
    //batch object will return request with up to n args
    //returns object with request and actual arg count
    public DispatchRequest<T_Request> getNextRequest(int size);
    //TODO: make sure autogen args are in this order
    //TODO: make sure autogen uses "AwaitableStreamObserver"
    public void sendRequest(ParSPICEStub stub, T_Request request, AwaitableStreamObserver<T_Response> awaiterTask);
    public void responseCallback(T_Response response);
}
