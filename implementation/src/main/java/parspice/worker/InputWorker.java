package parspice.worker;

import parspice.sender.Sender;

import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class InputWorker<I, O> {
    public final void run(Sender<I> inputSender, Sender<O> outputSender, String[] args) throws Exception {
        FileWriter writer = new FileWriter("/tmp/worker_log_" + args[0]);
        try {
            setup();

            Socket socket = new Socket("localhost", Integer.parseInt(args[0]));
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            int numIterations = Integer.parseInt(args[1]);
            List<I> inputs = new ArrayList<>(numIterations);
            for (int i = 0; i < numIterations; i++) {
                inputs.add(inputSender.read(ois));
            }
            for (int i = 0; i < numIterations; i++) {
                outputSender.write(task(inputs.get(i)), oos);
            }
            oos.close();
            ois.close();
            socket.close();
        } catch (Exception e) {
            writer.write(e.toString());
            writer.write(Arrays.toString(e.getStackTrace()));
            writer.flush();
        }
        writer.close();
    }

    protected void setup() {}

    protected abstract O task(I input) throws Exception;
}
