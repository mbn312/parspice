package parspiceBench.workers;

import org.jetbrains.annotations.NotNull;
import parspice.sender.DoubleArraySender;
import parspiceBench.BenchWorker;
import spice.basic.CSPICE;

/**
 * A java copy of the MxvhatWorker kotlin class,
 * to verify there is no significant performance difference.
 *
 * No performance difference was found.
 */
public class MxvhatWorkerJava extends BenchWorker<double[]> {

    @Override
    public int getBytes() {
        return 3*Double.BYTES;
    }

    @NotNull
    @Override
    public String getDescription() {
        return "vhat(mxm( ... )) but in Java";
    }

    @Override
    public boolean getJava() {
        return true;
    }

    static final double[][] mat = new double[][]{
            {1.0, 2.0, 3.0},
            {10.0, -2.0, 0.0},
            {1.3, 1.0, 0.5},
    };

    public MxvhatWorkerJava() {
        super(new DoubleArraySender(3));
    }

    @Override
    public void setup() {
        System.loadLibrary("JNISpice");
    }

    @Override
    public double[] task(int i) throws Exception {
        return CSPICE.vhat(CSPICE.mxv(mat, new double[]{1, 2, i}));
    }
}
