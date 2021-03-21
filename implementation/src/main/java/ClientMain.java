import spice.basic.CSPICE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientMain {
    public static void main(String[] args) throws Exception {
        final int iterations = 10000000;
        long start = System.currentTimeMillis();
        List<double[]> results = Dispatcher.run(new MyReturner(), iterations, 6);
        System.out.println("ParSPICE: " + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        System.loadLibrary("JNISpice");
        results = new ArrayList<double[]>(iterations);
        for (int i = 0; i < iterations; i++) {
            results.add(CSPICE.vhat(new double[]{1, 2, i}));
        }
        System.out.println("Direct: " + (System.currentTimeMillis() - start));
    }
}
