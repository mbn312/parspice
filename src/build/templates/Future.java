package parspice.functions.###UPPER_NAME###;

import java.util.concurrent.Future;
import java.util.ArrayList;
import java.util.List;
import java.lang.InterruptedException;
import java.util.concurrent.ExecutionException;
import parspice.rpc.###UPPER_NAME###Response;
import parspice.rpc.###UPPER_NAME###Response.###UPPER_NAME###Output;
import parspice.rpc.###UPPER_NAME###Response.###UPPER_NAME###Output;
import parspice.rpc.RepeatedDouble;
import parspice.rpc.RepeatedInteger;

public class ###UPPER_NAME###Future {
    private ArrayList<###UPPER_NAME###Call> calls;
    private Future<###UPPER_NAME###Response> futureResponse;
    private boolean received = false;

    public ###UPPER_NAME###Future(ArrayList<###UPPER_NAME###Call> calls, Future<###UPPER_NAME###Response> futureResponse) {
        this.calls = calls;
        this.futureResponse = futureResponse;
    }

    public ###UPPER_NAME###Call get(int index) throws InterruptedException, ExecutionException {
        if (!received) {
            List<###UPPER_NAME###Output> outputs = futureResponse.get().getOutputsList();
            for (int i = 0; i < outputs.size(); i++) {
                ###UPPER_NAME###Call call = calls.get(i);
                ###UPPER_NAME###Output output = outputs.get(i);
                ###GETTERS###
            }
            received = true;
        }
        return calls.get(index);
    }
}