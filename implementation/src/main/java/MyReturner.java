import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MyReturner implements Returner<double[]> {
    @Override
    public double[] read(ObjectInputStream ois) throws IOException {
        double[] result = new double[3];
        result[0] = ois.readDouble();
        result[1] = ois.readDouble();
        result[2] = ois.readDouble();
        return result;
    }

    @Override
    public void write(double[] out, ObjectOutputStream oos) throws IOException {
        oos.writeDouble(out[0]);
        oos.writeDouble(out[1]);
        oos.writeDouble(out[2]);
    }
}
