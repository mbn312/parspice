package parspice;

public abstract class Batch {
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

    protected int unsentCalls() {
        return declaredCalls - sentCalls;
    }

    public void run() {
        run(unsentCalls());
        sentCalls = declaredCalls;
    }

    protected abstract void run(int howMany);
}
