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
import parspice.rpc.RepeatedInt;
import parspice.rpc.ParSpiceGrpc;

public class ###UPPER_NAME###Batch extends Batch<###UPPER_NAME###Call> {

    private ArrayList<###UPPER_NAME###Call> calls = new ArrayList<###UPPER_NAME###Call>();

    public ###UPPER_NAME###Batch(ParSpiceGrpc.ParSpiceBlockingStub stub) {
        super(stub);
    }

    public void call(###ARGS###) {
        calls.add(new ###UPPER_NAME###Call(###ARGS_NO_TYPES###));
        registerCall();
    }

    protected ###UPPER_NAME###Call getUnchecked(int index) {
        return calls.get(index);
    }

    protected ArrayList<###UPPER_NAME###Call> getAllUnchecked() {
        return calls;
    }

    protected void run(int howMany) {
        ###UPPER_NAME###Request.Builder requestBuilder = ###UPPER_NAME###Request.newBuilder();
        for (###UPPER_NAME###Call call : calls) {
            ###NESTED_BUILDERS###
            requestBuilder.addInputs(###UPPER_NAME###Input.newBuilder()
                    ###BUILDERS###
                    .build());
        }
        ###UPPER_NAME###Request request = requestBuilder.build();

        ###UPPER_NAME###Response response;
        try{
            response = stub.###LOWER_NAME###RPC(request);
        }
        catch (io.grpc.StatusRuntimeException e) {
            System.out.println(e.getStatus());
            response = null;
        }
    }
}