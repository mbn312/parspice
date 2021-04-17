// THIS FILE WAS GENERATED. DO NOT EDIT IT DIRECTLY.
// See `src/gen/README.md` for details.

package parspice.sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Sender implementation for float[].
 *
 * Accepts arrays of fixed length or dynamic length. If fixed length, the length should
 * be declared on initialization. If the length is declared, then using arrays of
 * any other size is undefined behavior, and may or may not cause a runtime error.
 *
 * When sending dynamic arrays, it first sends the length of the array, increasing
 * network usage slightly. It doesn't matter significantly for more tasks, but
 * remember to specify the lengths for optimal performance.
 */
public class FloatArraySender implements Sender<float[]> {
    private final int length;

    /**
     * Creates an instance of FloatArraySender for dynamic length arrays.
     */
    public FloatArraySender() {
        this.length = -1;
    }

    /**
     * Creates an instance of FloatArraySender for fixed length arrays.
     *
     * @param length the length of all arrays to be serialized an deserialized
     */
    public FloatArraySender(int length) {
        this.length = length;
    }

    @Override
    public float[] read(ObjectInputStream ois) throws IOException {
        int localLength = length;
        if (length == -1) {
            localLength = ois.readInt();
        }
        float[] in = new float[localLength];
        for (int i = 0; i < localLength; i++) {
            in[i] = ois.readFloat();
        }
        return in;
    }

    @Override
    public void write(float[] out, ObjectOutputStream oos) throws IOException {
        if (length == -1) {
            oos.writeInt(out.length);
        }
        for (float b : out) {
            oos.writeFloat(b);
        }
    }
}
