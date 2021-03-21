import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class Worker<T> {
    public final void run(Returner<T> sender, String[] args) throws Exception {
        setup();

        Socket socket = new Socket("localhost", Integer.parseInt(args[0]));
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        int startI = Integer.parseInt(args[1]);
        int numIterations = Integer.parseInt(args[2]);
        for (int i = startI; i < startI + numIterations; i++) {
            sender.write(iterate(i), oos);
        }
        oos.close();
        socket.close();
    }

    public abstract void setup();
    public abstract T iterate(int i) throws Exception;
}
