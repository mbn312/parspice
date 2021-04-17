// THIS FILE WAS GENERATED. DO NOT EDIT IT DIRECTLY.
// See `src/gen/README.md` for details.

package parspice.sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Sender implementation for Float.
 */
public class FloatSender implements Sender<Float> {
    @Override
    public Float read(ObjectInputStream ois) throws IOException {
        return ois.readFloat();
    }

    @Override
    public void write(Float out, ObjectOutputStream oos) throws IOException {
        oos.writeFloat(out);
    }
}
