// THIS FILE WAS GENERATED. DO NOT EDIT IT DIRECTLY.
// See `src/gen/README.md` for details.

package parspice.sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Sender implementation for float[][].
 *
 * Accepts matrices of fixed dimensions or dynamic dimensions. If fixed dims, the size should
 * be declared on initialization. If the size is declared, then using matrices of
 * any other size is undefined behavior, and may or may not cause a runtime error.
 *
 * When sending dynamic matrices, it first sends the dimensions of the matrix, increasing
 * network usage slightly. It doesn't matter significantly for more tasks, but
 * remember to specify the lengths for optimal performance.
 */
public class FloatMatrixSender implements Sender<float[][]> {
    private final int rows;
    private final int columns;

    public FloatMatrixSender() {
        rows = -1;
        columns = -1;
    }

    /**
     * Creates an instance of FloatMatrix.
     *
     * @param rows the number of rows (first dimension) of the matrix.
     * @param columns the number of columns (second dimension) of the matrix.
     */
    public FloatMatrixSender(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
    }

    @Override
    public float[][] read(ObjectInputStream ois) throws IOException {
        int localRows = rows;
        int localColumns = columns;
        if (rows == -1) {
            localRows = ois.readInt();
            localColumns = ois.readInt();
        }
        float[][] in = new float[localRows][localColumns];
        for (int i = 0; i < localRows; i++) {
            for (int j = 0; j < localColumns; j++) {
                in[i][j] = ois.readFloat();
            }
        }
        return in;
    }

    @Override
    public void write(float[][] out, ObjectOutputStream oos) throws IOException {
        if (rows == -1) {
            oos.writeInt(out.length);
            oos.writeInt(out[0].length);
        }
        for (float[] row : out) {
            for (float b : row) {
                oos.writeFloat(b);
            }
        }
    }
}
