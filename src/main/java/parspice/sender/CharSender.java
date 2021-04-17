// THIS FILE WAS GENERATED. DO NOT EDIT IT DIRECTLY.
// See `src/gen/README.md` for details.

package parspice.sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Sender implementation for Character.
 */
public class CharSender implements Sender<Character> {
    @Override
    public Character read(ObjectInputStream ois) throws IOException {
        return ois.readChar();
    }

    @Override
    public void write(Character out, ObjectOutputStream oos) throws IOException {
        oos.writeChar(out);
    }
}
