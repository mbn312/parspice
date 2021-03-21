package parspice;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface Returner<T> {
    public T read(ObjectInputStream ois) throws IOException;
    public void write(T out, ObjectOutputStream oos) throws IOException;
}
