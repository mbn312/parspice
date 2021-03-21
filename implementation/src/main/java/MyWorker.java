import spice.basic.CSPICE;
import spice.basic.SpiceErrorException;

public class MyWorker extends Worker<double[]> {
    public static void main(String[] args) throws Exception {
        new MyWorker().run(new MyReturner(), args);
    }

    @Override
    public void setup() {
        System.loadLibrary("JNISpice");
    }

    @Override
    public double[] iterate(int i) throws SpiceErrorException {
        return CSPICE.vhat(new double[]{1, 2, i});
    }
}
