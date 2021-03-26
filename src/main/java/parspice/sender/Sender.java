package parspice.sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * A serializer/deserializer that reads/writes a data type to an ObjectStream.
 *
 * Implementers of Sender are used on to send and receive arguments and return values.
 * Implementers should avoid using writeObject or readObject, and instead should
 * use their knowledge of the data type's structure to generate compact messages
 * that can be processed without reflection.
 *
 * @param <T> The data type read and written by the Sender.
 */
public interface Sender<T> {

    /**
     * Reads an instance of the type from an ObjectInputStream.
     *
     * @param ois ObjectInputStream to read from.
     * @return the instance of the type read from the stream.
     * @throws IOException
     */
    T read(ObjectInputStream ois) throws IOException;

    /**
     * Writes an instance of the type to an ObjectOutputStream.
     *
     * @param out the given instance to write.
     * @param oos ObjectOutputStream to write to.
     * @throws IOException
     */
    void write(T out, ObjectOutputStream oos) throws IOException;
}
