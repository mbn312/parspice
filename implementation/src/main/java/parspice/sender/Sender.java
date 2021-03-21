package parspice.sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface Sender<T> {
    public T read(ObjectInputStream ois) throws IOException;
    public void write(T out, ObjectOutputStream oos) throws IOException;
}
