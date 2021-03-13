package parspice.worker;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import parspice.rpc.*;

import io.grpc.stub.StreamObserver;

import spice.basic.CSPICE;
import spice.basic.IDCodeNotFoundException;
import spice.basic.SpiceErrorException;
import spice.basic.SpiceException;
import spice.basic.NameNotFoundException;
import spice.basic.KernelVarNotFoundException;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.nio.file.*;
import java.io.IOException;

import org.apache.commons.lang3.ArrayUtils;

public class SpiceService extends ParSPICEGrpc.ParSPICEImplBase {

    private static List<RepeatedDouble> doubleArrayToRep(double[][] array) {
        List<RepeatedDouble> result = new ArrayList<RepeatedDouble>();
        for (int i = 0; i < array[0].length; i++) {
            RepeatedDouble rep = RepeatedDouble.newBuilder().addAllArray(Arrays.asList(ArrayUtils.toObject(array[i]))).build();
            result.add(rep);
        }
        return result;
    }

    private static List<RepeatedInteger> intArrayToRep(int[][] array) {
        List<RepeatedInteger> result = new ArrayList<RepeatedInteger>();
        for (int i = 0; i < array[0].length; i++) {
            RepeatedInteger rep = RepeatedInteger.newBuilder().addAllArray(Arrays.asList(ArrayUtils.toObject(array[i]))).build();
            result.add(rep);
        }
        return result;
    }

    private static double[] repDoubleToArray(RepeatedDouble repDouble) {
        return repDouble.getArrayList().stream().mapToDouble(Double::doubleValue).toArray();
    }

    private static int[] repIntegerToArray(RepeatedInteger repInt) {
        return repInt.getArrayList().stream().mapToInt(Integer::intValue).toArray();
    }

    ###WORKERS###
}
