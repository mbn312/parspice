package parspice.sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class BooleanMatrixSender implements Sender<boolean[][]> {
    private final int rows;
    private final int columns;

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
