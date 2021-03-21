package parspice.sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class StringArraySender implements Sender<String[]> {
    private final int length;

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
