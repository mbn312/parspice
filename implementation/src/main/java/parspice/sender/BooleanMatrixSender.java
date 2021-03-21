package parspice.sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Sender implementation for boolean[][].
 *
 * All matrices serialized and deserialized by BooleanMatrixSender must be the same dimensions.
 * Interacting with arrays of other dimensions is undefined behavior, and may or may not
 * cause a runtime error. Inputs are not sanitized.
 */
public class BooleanMatrixSender implements Sender<boolean[][]> {
    private final int rows;
    private final int columns;

    /**
     * Creates an instance of BooleanMatrix.
     *
     * @param r the number of rows (first dimension) of the matrix.
     * @param c the number of columns (second dimension) of the matrix.
     */
    public BooleanMatrixSender(int r, int c) {
        rows = r;
        columns = c;
    }

    @Override
    public boolean[][] read(ObjectInputStream ois) throws IOException {
        boolean[][] in = new boolean[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                in[i][j] = ois.readBoolean();
            }
        }
        return in;
    }

    @Override
    public void write(boolean[][] out, ObjectOutputStream oos) throws IOException {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                oos.writeBoolean(out[i][j]);
            }
        }
    }
}
