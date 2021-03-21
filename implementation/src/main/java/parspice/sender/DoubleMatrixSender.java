package parspice.sender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DoubleMatrixSender implements Sender<double[][]> {
    private final int rows;
    private final int columns;

    public DoubleMatrixSender(int r, int c) {
        rows = r;
        columns = c;
    }

    @Override
    public double[][] read(ObjectInputStream ois) throws IOException {
        double[][] in = new double[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                in[i][j] = ois.readDouble();
            }
        }
        return in;
    }

    @Override
    public void write(double[][] out, ObjectOutputStream oos) throws IOException {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                oos.writeDouble(out[i][j]);
            }
        }
    }
}
