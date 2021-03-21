package parspice.sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Sender implementation for String[].
 *
 * All arrays serialized and deserialized by StringArraySender must be the same length.
 * Interacting with arrays of other lengths is undefined behavior, and may or may not
 * cause a runtime error. Inputs are not sanitized.
 */
public class StringArraySender implements Sender<String[]> {
    private final int length;

    /**
     * Creates an instance of StringArraySender.
     *
     * @param len the length of all arrays to be serialized an deserialized
     */
    public StringArraySender(int len) {
        length = len;
    }

    @Override
    public String[] read(ObjectInputStream ois) throws IOException {
        String[] in = new String[length];
        for (int i = 0; i < length; i++) {
            in[i] = ois.readUTF();
        }
        return in;
    }

    @Override
    public void write(String[] out, ObjectOutputStream oos) throws IOException {
        for (int i = 0; i < length; i++) {
            oos.writeUTF(out[i]);
        }
    }
}
