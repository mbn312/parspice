package parspice;

public abstract class Batch {
    private final int totalCalls;
    private int declaredCalls;
    private int receivedCalls;

    public Batch(int total) {
        totalCalls = total;
    }
}
