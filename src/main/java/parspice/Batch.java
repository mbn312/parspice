package parspice;

import java.util.concurrent.Future;
import jdk.jshell.spi.ExecutionControl;
import parspice.rpc.ParSpiceGrpc;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public abstract class Batch<T extends Call> {
    protected ArrayList<T> unsentCalls;
    protected ArrayList<Future<ArrayList<T>>> futures;

    protected ParSpiceGrpc.ParSpiceFutureStub stub;

    private static final int BATCH_SIZE = 1000;

    public Batch(ParSpiceGrpc.ParSpiceFutureStub stub) {
        this.stub = stub;
        this.unsentCalls = new ArrayList<T>();
    }

    protected void registerCall() {
        if (unsentCalls.size() >= BATCH_SIZE) {
            run();
        }
    }

    public T get(int index) throws ExecutionException, InterruptedException {
        return futures.get(index % BATCH_SIZE).get().get(index - (index % BATCH_SIZE));
    }

    public abstract void run();
}
