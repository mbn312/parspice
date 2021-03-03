package parspice.functions.###UPPER_NAME###;

import parspice.Call;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//import spice.basic.GFSearchUtils;
//import spice.basic.GFScalarQuantity;
import parspice.rpc.###UPPER_NAME###Request.###UPPER_NAME###Input;
import parspice.rpc.###UPPER_NAME###Response.###UPPER_NAME###Output;
import parspice.rpc.RepeatedDouble;
import parspice.rpc.RepeatedInteger;

public class ###UPPER_NAME###Call extends Call {
    ###FIELDS###

    public ###UPPER_NAME###Call(###ARGS###){
        ###ASSIGN_FIELDS###
    }

    public ###UPPER_NAME###Input pack() {
        ###NESTED_BUILDERS###
        return ###UPPER_NAME###Input.newBuilder()
            ###BUILDERS###
            .build();
    }

    public void unpack(###UPPER_NAME###Output output) {
        ###GETTERS###
        this.error = output.getError();
    }
}