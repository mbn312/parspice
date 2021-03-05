package parspice.worker;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import parspice.rpc.*;

import io.grpc.stub.StreamObserver;

import spice.basic.CSPICE;
import spice.basic.IDCodeNotFoundException;
import spice.basic.SpiceErrorException;


import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class SpiceService extends ParSPICEGrpc.ParSPICEImplBase {

    private static List<RepeatedDouble> doubleArrayToRep(double[][] array) {
        List<RepeatedDouble> result = new ArrayList<RepeatedDouble>();
        for (int i = 0; i < array[0].length; i++) {
            RepeatedDouble rep = RepeatedDouble.newBuilder().addAllArray(Arrays.asList(primToObj(array[i]))).build();
            result.add(rep);
        }
        return result;
    }

    private static List<RepeatedInteger> intArrayToRep(int[][] array) {
        List<RepeatedInteger> result = new ArrayList<RepeatedInteger>();
        for (int i = 0; i < array[0].length; i++) {
            RepeatedInteger rep = RepeatedInteger.newBuilder().addAllArray(Arrays.asList(primToObj(array[i]))).build();
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

    private static Integer[] primToObj(int[] in) {
        Integer[] result = new Integer[in.length];
        for (int i = 0; i < in.length; i++) {
            result[i] = in[i];
        }
        return result;
    }

    private static Double[] primToObj(double[] in) {
        Double[] result = new Double[in.length];
        for (int i = 0; i < in.length; i++) {
            result[i] = in[i];
        }
        return result;
    }

    private static Boolean[] primToObj(boolean[] in) {
        Boolean[] result = new Boolean[in.length];
        for (int i = 0; i < in.length; i++) {
            result[i] = in[i];
        }
        return result;
    }

    private static String[] primToObj(String[] in ) { return in; }

    private static Integer[][] primToObj(int[][] in) {
        Integer[][] result = new Integer[in.length][in[0].length];
        for (int i = 0; i < in.length; i++) {
            for (int j = 0; j < in[0].length; j++) {
                result[i][j] = in[i][j];
            }
        }
        return result;
    }

    private static Double[][] primToObj(double[][] in) {
        Double[][] result = new Double[in.length][in[0].length];
        for (int i = 0; i < in.length; i++) {
            for (int j = 0; j < in[0].length; j++) {
                result[i][j] = in[i][j];
            }
        }
        return result;
    }

    private static int[] objToPrim(Integer[] in) {
        int[] result = new int[in.length];
        for (int i = 0; i < in.length; i++) {
            result[i] = in[i];
        }
        return result;
    }

    private static double[] objToPrim(Double[] in) {
        double[] result = new double[in.length];
        for (int i = 0; i < in.length; i++) {
            result[i] = in[i];
        }
        return result;
    }

    private static int[][] objToPrim(Integer[][] in) {
        int[][] result = new int[in.length][in[0].length];
        for (int i = 0; i < in.length; i++) {
            for (int j = 0; j < in[0].length; i++) {
                result[i][j] = in[i][j];
            }
        }
        return result;
    }

    private static double[][] objToPrim(Double[][] in) {
        double[][] result = new double[in.length][in[0].length];
        for (int i = 0; i < in.length; i++) {
            for (int j = 0; j < in[0].length; i++) {
                result[i][j] = in[i][j];
            }
        }
        return result;
    }



    ###WORKERS###
}
