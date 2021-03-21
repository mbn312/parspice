package parspice.sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DoubleArraySender implements Sender<double[]> {
    private final int length;

    public DoubleArraySender(int len) {
        length = len;
    }

    @Override
    public double[] read(ObjectInputStream ois) throws IOException {
        double[] in = new double[length];
        for (int i = 0; i < length; i++) {
            in[i] = ois.readDouble();
        }
        return in;
    }

    @Override
    public void write(double[] out, ObjectOutputStream oos) throws IOException {
        for (int i = 0; i < length; i++) {
            oos.writeDouble(out[i]);
        }
    }
}
