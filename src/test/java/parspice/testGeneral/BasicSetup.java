package parspice.testGeneral;

import org.junit.jupiter.api.TestInstance;
import parspice.sender.DoubleArraySender;
import parspice.worker.OWorker;


import org.junit.jupiter.api.Test;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BasicSetup extends OWorker<double[]> {
    public BasicSetup() {
        super(new DoubleArraySender(3));
    }

    @Override
    public void setup() throws Exception {
        System.loadLibrary("JNISpice");
    }

    @Override
    public double[] task(int i) throws Exception {
        double ret[] = {1.1,2.2};
        return ret;
    }

    @Test
    public void testSetup(){
        try {
            this.setup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
