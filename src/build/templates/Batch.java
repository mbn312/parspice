package parspice.functions.###UPPER_NAME###;

import java.util.ArrayList;
import java.util.Arrays;
import parspice.Batch;
//import spice.basic.GFSearchUtils;
//import spice.basic.GFScalarQuantity;
import parspice.rpc.###UPPER_NAME###Request;
import parspice.rpc.###UPPER_NAME###Request.###UPPER_NAME###Input;
import parspice.rpc.###UPPER_NAME###Response;
import parspice.rpc.###UPPER_NAME###Response.###UPPER_NAME###Output;
import parspice.rpc.RepeatedDouble;
import parspice.rpc.RepeatedInteger;
import parspice.rpc.ParSPICEGrpc;
import java.util.concurrent.Future;

public class ###UPPER_NAME###Batch extends Batch<###UPPER_NAME###Call> {

    public ###UPPER_NAME###Batch(ParSPICEGrpc.ParSPICEFutureStub stub) {
        super(stub);
    }

    public void call(###ARGS###) {
        unsentCalls.add(new ###UPPER_NAME###Call(###ARGS_NO_TYPES###));
        registerCall();
    }

    private ###UPPER_NAME###Request pack() {
        ###UPPER_NAME###Request.Builder requestBuilder = ###UPPER_NAME###Request.newBuilder();
        for (###UPPER_NAME###Call call : unsentCalls) {
            requestBuilder.addInputs(call.pack());
        }
        requestBuilder.setBatchID(0);
        return requestBuilder.build();
    }

    public void run() {
        ###UPPER_NAME###Request request = pack();
        ###UPPER_NAME###Future ###LOWER_NAME###Future;
        try{
            Future<###UPPER_NAME###Response> responseFuture = stub.###LOWER_NAME###RPC(request);
            ###LOWER_NAME###Future = new ###UPPER_NAME###Future(unsentCalls, responseFuture);
            unsentCalls = new ArrayList<###UPPER_NAME###Call>();
        }
        catch (io.grpc.StatusRuntimeException e) {
            System.out.println(e.getStatus());
            ###LOWER_NAME###Future = null;
        }
    }
}