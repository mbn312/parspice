package parspice.functions.###UPPER_NAME###;

import parspice.Call;
import java.util.ArrayList;
//import spice.basic.GFSearchUtils;
//import spice.basic.GFScalarQuantity;

public class ###UPPER_NAME###Call extends Call {
###FIELDS###
    public String error;

    public ###UPPER_NAME###Call(###ARGS###){
###ASSIGN_FIELDS###
        error = "";
    }
}