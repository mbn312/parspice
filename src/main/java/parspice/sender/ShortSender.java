// THIS FILE WAS GENERATED. DO NOT EDIT IT DIRECTLY.
// See `src/gen/README.md` for details.

package parspice.sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Sender implementation for Short.
 */
public class ShortSender implements Sender<Short> {
    @Override
    public Short read(ObjectInputStream ois) throws IOException {
        return ois.readShort();
    }

    @Override
    public void write(Short out, ObjectOutputStream oos) throws IOException {
        oos.writeShort(out);
    }
}
