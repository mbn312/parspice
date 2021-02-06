package parspice;

import jdk.jshell.spi.ExecutionControl;

import java.util.ArrayList;

public abstract class Batch<T extends Call> {
    private int declaredCalls = 0;
    private int sentCalls = 0;
    private int receivedCalls = 0;

    private static final int BATCH_SIZE = 1000;

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

    public T get(int index) throws ExecutionControl.NotImplementedException {
        T result = getUnchecked(index);
        if (result.received) {
            return result;
        } else {
            throw new ExecutionControl.NotImplementedException("fill in when we get there");
        }
    }

    public ArrayList<T> getAll() throws ExecutionControl.NotImplementedException {
        if (receivedCalls == declaredCalls) {
            return getAllUnchecked();
        } else {
            throw new ExecutionControl.NotImplementedException("fill in when we get there");
        }
    }

    protected abstract void run(int howMany);
    protected abstract T getUnchecked(int index);
    protected abstract ArrayList<T> getAllUnchecked();
}
