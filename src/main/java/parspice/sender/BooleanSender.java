package parspice.sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Sender implementation for Boolean.
 */
public class BooleanSender implements Sender<Boolean> {
    @Override
    public Boolean read(ObjectInputStream ois) throws IOException {
        return ois.readBoolean();
    }

    @Override
    public void write(Boolean out, ObjectOutputStream oos) throws IOException {
        oos.writeBoolean(out);
    }
}
