package parspice.functions.###UPPER_NAME###;

import java.util.ArrayList;
import parspice.Batch;
//import spice.basic.GFSearchUtils;
//import spice.basic.GFScalarQuantity;
import parspice.rpc.###UPPER_NAME###Request;

public class ###UPPER_NAME###Batch extends Batch<###UPPER_NAME###Call> {

    private ArrayList<###UPPER_NAME###Call> calls = new ArrayList<###UPPER_NAME###Call>();

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
            // all named after jnispice documentation
//            bundleBuilder.addRequests(###UPPER_NAME###Req.newBuilder()
//                    ###BUILDERS###
//                    .build());
                    // .setTarget(o1.arg1)
                    // .setEt(o1.arg2)
                    // .setRef(o1.arg3)
                    // .setAbcorr(o1.arg4)
                    // .setObserver(o1.arg5)
                    // .addAllPos(o1.arg6)
                    // .addAllLt(o1.arg7)
        }
//        ###UPPER_NAME###Bundle bundle = bundleBuilder.build();

        // SEND -> RECEIVE -> MODIFY SELF IN PLACE

        // send off with stub
        // ParResponse results;
        // try{
        //     SpkposRep response = stub.parSpkpos(bundle);
        //     results = new ParResponse(response.getTimeList());
        // }
        // catch (StatusRuntimeException e) {
        //     System.out.println(e.getStatus());
        //     results = null;
        // }

        // compose results
        // return results;
    }
}