package parspice.sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Sender implementation for Byte.
 */
public class ByteSender implements Sender<Byte> {
    @Override
    public Byte read(ObjectInputStream ois) throws IOException {
        return ois.readByte();
    }

    @Override
    public void write(Byte out, ObjectOutputStream oos) throws IOException {
        oos.writeByte(out);
    }
}
