package parspice;

// import jdk.jshell.spi.ExecutionControl;
import parspice.rpc.ParSpiceGrpc;

import java.util.ArrayList;

public abstract class Batch<T extends Call> {
    private int declaredCalls = 0;
    private int sentCalls = 0;
    private int receivedCalls = 0;

    protected ParSpiceGrpc.ParSpiceBlockingStub stub;

    private static final int BATCH_SIZE = 1000;

    public Batch(ParSpiceGrpc.ParSpiceBlockingStub stub) {
        this.stub = stub;
    }

    protected void registerCall() {
        declaredCalls++;
        if (unsentCalls() >= BATCH_SIZE) {
            run();
        }
    }

    private int unsentCalls() {
        return declaredCalls - sentCalls;
    }

    public void run() {
        run(unsentCalls());
        sentCalls = declaredCalls;
    }

    public T get(int index) {
        T result = getUnchecked(index);
        if (result.received) {
            return result;
        } else {
//            throw new ExecutionControl.NotImplementedException("fill in when we get there");
            return result;
        }
    }

    public ArrayList<T> getAll()  {
        if (receivedCalls == declaredCalls) {
            return getAllUnchecked();
        } else {
//            throw new ExecutionControl.NotImplementedException("fill in when we get there");
            return getAllUnchecked();
        }
    }

    protected abstract void run(int howMany);
    protected abstract T getUnchecked(int index);
    protected abstract ArrayList<T> getAllUnchecked();
}
