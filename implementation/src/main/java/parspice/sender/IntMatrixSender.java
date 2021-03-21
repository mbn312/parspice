package parspice.sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Sender implementation for int[][].
 *
 * All matrices serialized and deserialized by IntMatrixSender must be the same dimensions.
 * Interacting with arrays of other dimensions is undefined behavior, and may or may not
 * cause a runtime error. Inputs are not sanitized.
 */
public class IntMatrixSender implements Sender<int[][]> {
    private final int rows;
    private final int columns;

    /**
     * Creates an instance of IntMatrix.
     *
     * @param r the number of rows (first dimension) of the matrix.
     * @param c the number of columns (second dimension) of the matrix.
     */
    public IntMatrixSender(int r, int c) {
        rows = r;
        columns = c;
    }

    @Override
    public int[][] read(ObjectInputStream ois) throws IOException {
        int[][] in = new int[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                in[i][j] = ois.readInt();
            }
        }
        return in;
    }

    @Override
    public void write(int[][] out, ObjectOutputStream oos) throws IOException {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                oos.writeInt(out[i][j]);
            }
        }
    }
}
