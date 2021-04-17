package parspice.sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Sender implementation for char[][].
 *
 * Accepts matrices of fixed dimensions or dynamic dimensions. If fixed dims, the size should
 * be declared on initialization. If the size is declared, then using matrices of
 * any other size is undefined behavior, and may or may not cause a runtime error.
 *
 * When sending dynamic matrices, it first sends the dimensions of the matrix, increasing
 * network usage slightly. It doesn't matter significantly for more tasks, but
 * remember to specify the lengths for optimal performance.
 */
public class CharMatrixSender implements Sender<char[][]> {
    private final int rows;
    private final int columns;

    public CharMatrixSender() {
        rows = -1;
        columns = -1;
    }

    /**
     * Creates an instance of CharMatrix.
     *
     * @param rows the number of rows (first dimension) of the matrix.
     * @param columns the number of columns (second dimension) of the matrix.
     */
    public CharMatrixSender(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
    }

    @Override
    public char[][] read(ObjectInputStream ois) throws IOException {
        int localRows = rows;
        int localColumns = columns;
        if (rows == -1) {
            localRows = ois.readInt();
            localColumns = ois.readInt();
        }
        char[][] in = new char[localRows][localColumns];
        for (int i = 0; i < localRows; i++) {
            for (int j = 0; j < localColumns; j++) {
                in[i][j] = ois.readChar();
            }
        }
        return in;
    }

    @Override
    public void write(char[][] out, ObjectOutputStream oos) throws IOException {
        if (rows == -1) {
            oos.writeInt(out.length);
            oos.writeInt(out[0].length);
        }
        for (char[] row : out) {
            for (char b : row) {
                oos.writeChar(b);
            }
        }
    }
}
