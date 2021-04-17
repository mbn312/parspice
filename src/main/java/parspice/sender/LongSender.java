// THIS FILE WAS GENERATED. DO NOT EDIT IT DIRECTLY.
// See `src/gen/README.md` for details.

package parspice.sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Sender implementation for Long.
 */
public class LongSender implements Sender<Long> {
    @Override
    public Long read(ObjectInputStream ois) throws IOException {
        return ois.readLong();
    }

    @Override
    public void write(Long out, ObjectOutputStream oos) throws IOException {
        oos.writeLong(out);
    }
}
