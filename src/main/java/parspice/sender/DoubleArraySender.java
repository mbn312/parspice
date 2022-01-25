// THIS FILE WAS GENERATED. DO NOT EDIT IT DIRECTLY.
// See `src/gen/README.md` for details.

package parspice.sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Sender implementation for double[].
 *
 * Accepts arrays of fixed length or dynamic length. If fixed length, the length should
 * be declared on initialization. If the length is declared, then using arrays of
 * any other size is undefined behavior, and may or may not cause a runtime error.
 *
 * When sending dynamic arrays, it first sends the length of the array, increasing
 * network usage slightly. It doesn't matter significantly for more tasks, but
 * remember to specify the lengths for optimal performance.
 */
public class DoubleArraySender implements Sender<double[]> {
    private final int length;

    /**
     * Creates an instance of DoubleArraySender for dynamic length arrays.
     */
    public DoubleArraySender() {
        this.length = -1;
    }

    /**
     * Creates an instance of DoubleArraySender for fixed length arrays.
     *
     * @param length the length of all arrays to be serialized an deserialized
     */
    public DoubleArraySender(int length) {
        this.length = length;
    }

    @Override
    public double[] read(ObjectInputStream ois) throws IOException {
        int localLength = length;
        if (length == -1) {
            localLength = ois.readInt();
        }

        double[] in = new double[localLength];
        for (int i = 0; i < localLength; i++) {
            in[i] = ois.readDouble();
        }

        return in;
    }

    @Override
    public void write(double[] out, ObjectOutputStream oos) throws IOException {
        if (length == -1) {
            oos.writeInt(out.length);
        }
        for (double b : out) {
            oos.writeDouble(b);
        }
    }
}
