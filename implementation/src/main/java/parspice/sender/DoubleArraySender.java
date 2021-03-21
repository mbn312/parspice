package parspice.sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Sender implementation for double[].
 *
 * All arrays serialized and deserialized by DoubleArraySender must be the same length.
 * Interacting with arrays of other lengths is undefined behavior, and may or may not
 * cause a runtime error. Inputs are not sanitized.
 */
public class DoubleArraySender implements Sender<double[]> {
    private final int length;

    /**
     * Creates an instance of DoubleArraySender.
     *
     * @param len the length of all arrays to be serialized an deserialized
     */
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
