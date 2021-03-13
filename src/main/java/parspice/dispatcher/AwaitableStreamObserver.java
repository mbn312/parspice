package parspice.dispatcher;

import io.grpc.stub.StreamObserver;

public class AwaitableStreamObserver<T> implements StreamObserver<T> {
    AutoResetEvent _resetEvent = new AutoResetEvent(false);
    private T value;
    private Throwable _ex;

    @Override
    public void onNext(T value) {
        this.value = value;
    }

    @Override
    public void onError(Throwable t) {
        _ex = t;
        _resetEvent.set();
    }

    @Override
    public void onCompleted() {
        _resetEvent.set();
    }

    /**
     * Wait for result to come in and return the result.
     * If the result is already cached, no wait is required.
     * If the result is an error code, then an error will be thrown here
     * @return
     * @throws Throwable
     */
    public T awaitValue() throws Throwable {
        _resetEvent.waitOne();
        if(_ex != null)
        {
            throw _ex;
        }
        return value;
    }
}
