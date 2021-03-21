package parspice.sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class IntArraySender implements Sender<int[]> {
    private final int length;

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
