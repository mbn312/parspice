package parspice.sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Sender implementation for int[].
 *
 * All arrays serialized and deserialized by IntArraySender must be the same length.
 * Interacting with arrays of other lengths is undefined behavior, and may or may not
 * cause a runtime error. Inputs are not sanitized.
 */
public class IntArraySender implements Sender<int[]> {
    private final int length;

    /**
     * Creates an instance of IntArraySender.
     *
     * @param len the length of all arrays to be serialized an deserialized
     */
    public IntArraySender(int len) {
        length = len;
    }

    @Override
    public int[] read(ObjectInputStream ois) throws IOException {
        int[] in = new int[length];
        for (int i = 0; i < length; i++) {
            in[i] = ois.readInt();
        }
        return in;
    }

    @Override
    public void write(int[] out, ObjectOutputStream oos) throws IOException {
        for (int i = 0; i < length; i++) {
            oos.writeInt(out[i]);
        }
    }
}
