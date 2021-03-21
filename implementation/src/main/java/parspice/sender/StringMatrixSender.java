package parspice.sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class StringMatrixSender implements Sender<String[][]> {
    private final int rows;
    private final int columns;

    public StringMatrixSender(int r, int c) {
        rows = r;
        columns = c;
    }

    @Override
    public String[][] read(ObjectInputStream ois) throws IOException {
        String[][] in = new String[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                in[i][j] = ois.readUTF();
            }
        }
        return in;
    }

    @Override
    public void write(String[][] out, ObjectOutputStream oos) throws IOException {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                oos.writeUTF(out[i][j]);
            }
        }
    }
}
