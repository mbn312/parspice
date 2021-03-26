package parspice.sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Sender implementation for double.
 */
public class DoubleSender implements Sender<Double> {
    @Override
    public Double read(ObjectInputStream ois) throws IOException {
        return ois.readDouble();
    }

    @Override
    public void write(Double out, ObjectOutputStream oos) throws IOException {
        oos.writeDouble(out);
    }
}
