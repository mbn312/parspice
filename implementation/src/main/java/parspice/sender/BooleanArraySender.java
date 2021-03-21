package parspice.sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class BooleanArraySender implements Sender<boolean[]> {
    private final int length;

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
