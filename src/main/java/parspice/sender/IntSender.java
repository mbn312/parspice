package parspice.sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Sender implementation for Integer.
 */
public class IntSender implements Sender<Integer> {
    @Override
    public Integer read(ObjectInputStream ois) throws IOException {
        return ois.readInt();
    }

    @Override
    public void write(Integer out, ObjectOutputStream oos) throws IOException {
        oos.writeInt(out);
    }
}
