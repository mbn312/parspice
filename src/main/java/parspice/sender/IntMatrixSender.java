// THIS FILE WAS GENERATED. DO NOT EDIT IT DIRECTLY.
// See `src/gen/README.md` for details.

package parspice.sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Sender implementation for int[][].
 *
 * Accepts matrices of fixed dimensions or dynamic dimensions. If fixed dims, the size should
 * be declared on initialization. If the size is declared, then using matrices of
 * any other size is undefined behavior, and may or may not cause a runtime error.
 *
 * When sending dynamic matrices, it first sends the dimensions of the matrix, increasing
 * network usage slightly. It doesn't matter significantly for more tasks, but
 * remember to specify the lengths for optimal performance.
 */
public class IntMatrixSender implements Sender<int[][]> {
    private final int rows;
    private final int columns;

    public IntMatrixSender() {
        rows = -1;
        columns = -1;
    }

    /**
     * Creates an instance of IntMatrix.
     *
     * @param rows the number of rows (first dimension) of the matrix.
     * @param columns the number of columns (second dimension) of the matrix.
     */
    public IntMatrixSender(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
    }

    @Override
    public int[][] read(ObjectInputStream ois) throws IOException {
        int localRows = rows;
        int localColumns = columns;
        if (rows == -1) {
            localRows = ois.readInt();
            localColumns = ois.readInt();
        }
        int[][] in = new int[localRows][localColumns];
        for (int i = 0; i < localRows; i++) {
            for (int j = 0; j < localColumns; j++) {
                in[i][j] = ois.readInt();
            }
        }
        return in;
    }

    @Override
    public void write(int[][] out, ObjectOutputStream oos) throws IOException {
        if (rows == -1) {
            oos.writeInt(out.length);
            oos.writeInt(out[0].length);
        }
        for (int[] row : out) {
            for (int b : row) {
                oos.writeInt(b);
            }
        }
    }
}
