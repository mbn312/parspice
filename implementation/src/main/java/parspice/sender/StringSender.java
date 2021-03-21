package parspice.sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Sender implementation for int.
 */
public class StringSender implements Sender<String> {
    @Override
    public String read(ObjectInputStream ois) throws IOException {
        return ois.readUTF();
    }

    @Override
    public void write(String out, ObjectOutputStream oos) throws IOException {
        oos.writeUTF(out);
    }
}
