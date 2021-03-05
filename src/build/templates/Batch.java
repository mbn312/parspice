package parspice.functions.###UPPER_NAME###;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import parspice.Batch;
//import spice.basic.GFSearchUtils;
//import spice.basic.GFScalarQuantity;
import parspice.rpc.###UPPER_NAME###Request;
import parspice.rpc.###UPPER_NAME###Request.###UPPER_NAME###Input;
import parspice.rpc.###UPPER_NAME###Response;
import parspice.rpc.###UPPER_NAME###Response.###UPPER_NAME###Output;
import parspice.rpc.ParSPICEGrpc.ParSPICEStub;
import io.grpc.stub.StreamObserver;

public class ###UPPER_NAME###Batch extends Batch<###UPPER_NAME###Call, ###UPPER_NAME###Response> {
    public void call(###ARGS###) {
        calls.add(new ###UPPER_NAME###Call(###ARGS_NO_TYPES###));
    }

    public void sendRequest(ParSPICEStub stub, int howMany, StreamObserver<###UPPER_NAME###Response> awaiterTask) {
        ###UPPER_NAME###Request.Builder requestBuilder = ###UPPER_NAME###Request.newBuilder();
        int endIndex = unsentIndex + howMany;
        for (int i = unsentIndex; i < endIndex; i++) {
            requestBuilder.addInputs(calls.get(i).pack());
        }
        requestBuilder.setBatchID(unsentIndex);
        unsentIndex = endIndex;
        stub.###LOWER_NAME###RPC(requestBuilder.build(), awaiterTask);
    }

    public void receiveResponse(###UPPER_NAME###Response response) {
        List<###UPPER_NAME###Response.###UPPER_NAME###Output> outputs = response.getOutputsList();
        int startIndex = response.getBatchID();
        int endIndex = startIndex + outputs.size();
        for (int i = startIndex; i < endIndex; i++) {
            calls.get(i).unpack(outputs.get(i));
        }
    }
}