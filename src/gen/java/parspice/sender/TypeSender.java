package parspice.sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Sender implementation for ###TYPE###.
 */
public class ###NAME###Sender implements Sender<###TYPE###> {
    @Override
    public ###TYPE### read(ObjectInputStream ois) throws IOException {
        return ois.read###STREAM###();
    }

    @Override
    public void write(###TYPE### out, ObjectOutputStream oos) throws IOException {
        oos.write###STREAM###(out);
    }
}
