package parspice;

public abstract class Batch {
    private int declaredCalls = 0;
    private int receivedCalls = 0;

    private static final int BATCH_SIZE = 1000;

    protected void registerCall() {
        declaredCalls++;
        if (declaredCalls % BATCH_SIZE == 0) {
            run(unsentCalls());
        }
    }

    protected int unsentCalls() {
        return declaredCalls % BATCH_SIZE;
    }

    public void run() {
        run(unsentCalls());
    }

    protected abstract void run(int howMany);
}
