package parspice.sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Sender implementation for boolean[].
 *
 * All arrays serialized and deserialized by BooleanArraySender must be the same length.
 * Interacting with arrays of other lengths is undefined behavior, and may or may not
 * cause a runtime error. Inputs are not sanitized.
 */
public class BooleanArraySender implements Sender<boolean[]> {
    private final int length;

    /**
     * Creates an instance of BooleanArraySender.
     *
     * @param len the length of all arrays to be serialized an deserialized
     */
    public BooleanArraySender(int len) {
        length = len;
    }

    @Override
    public boolean[] read(ObjectInputStream ois) throws IOException {
        boolean[] in = new boolean[length];
        for (int i = 0; i < length; i++) {
            in[i] = ois.readBoolean();
        }
        return in;
    }

    @Override
    public void write(boolean[] out, ObjectOutputStream oos) throws IOException {
        for (int i = 0; i < length; i++) {
            oos.writeBoolean(out[i]);
        }
    }
}
