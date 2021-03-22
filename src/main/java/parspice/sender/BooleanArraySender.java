package parspice.sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Sender implementation for boolean[].
 *
 * Accepts arrays of fixed length or dynamic length. If fixed length, the length should
 * be declared on initialization. If the length is declared, then using arrays of
 * any other size is undefined behavior, and may or may not cause a runtime error.
 *
 * When sending dynamic arrays, it first sends the length of the array, increasing
 * network usage slightly. It doesn't matter significantly for more tasks, but
 * remember to specify the lengths for optimal performance.
 */
public class BooleanArraySender implements Sender<boolean[]> {
    private final int length;

    /**
     * Creates an instance of BooleanArraySender for dynamic length arrays.
     */
    public BooleanArraySender() {
        this.length = -1;
    }

    /**
     * Creates an instance of BooleanArraySender for fixed length arrays.
     *
     * @param length the length of all arrays to be serialized an deserialized
     */
    public BooleanArraySender(int length) {
        this.length = length;
    }

    @Override
    public boolean[] read(ObjectInputStream ois) throws IOException {
        int localLength = length;
        if (length == -1) {
            localLength = ois.readInt();
        }
        boolean[] in = new boolean[localLength];
        for (int i = 0; i < localLength; i++) {
            in[i] = ois.readBoolean();
        }
        return in;
    }

    @Override
    public void write(boolean[] out, ObjectOutputStream oos) throws IOException {
        if (length == -1) {
            oos.writeInt(out.length);
        }
        for (boolean b : out) {
            oos.writeBoolean(b);
        }
    }
}
