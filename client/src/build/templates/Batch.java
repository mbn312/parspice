package parspiceTest;

import java.util.ArrayList;
import parspice.Batch;

public class ###UPPER_NAME###Batch extends Batch {

    private ArrayList<###UPPER_NAME###Call> args = new ArrayList<###UPPER_NAME###Call>();

    private int totalCalls;
    private int declaredCalls;
    private int receivedCalls;

    public ###UPPER_NAME###Batch(int totalCalls) {
        super(totalCalls);
    }

    public void add(###ARGS###){
        args.add(new ###UPPER_NAME###Call(###ARGS_NO_TYPES###));
    }

    public ###UPPER_NAME###Call get(int index) {
        return args.get(index);
    }

    public ArrayList getAll() {
        return args;
    }

    public void execute() {
        ###UPPER_NAME###Bundle.Builder bundleBuilder = ###UPPER_NAME###Bundle.newBuilder();
        for (###UPPER_NAME###Call call : args) {
            // all named after jnispice documentation
            bundleBuilder.addRequests(###UPPER_NAME###Req.newBuilder()
//                    ###BUILDERS###
                    .build());
                    // .setTarget(o1.arg1)
                    // .setEt(o1.arg2)
                    // .setRef(o1.arg3)
                    // .setAbcorr(o1.arg4)
                    // .setObserver(o1.arg5)
                    // .addAllPos(o1.arg6)
                    // .addAllLt(o1.arg7)
        }
        ###UPPER_NAME###Bundle bundle = bundleBuilder.build();

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