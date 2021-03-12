package parspice.dispatcher;

import util.concurrent.CancellationToken;
import util.concurrent.CancellationTokenSource;
import util.validation.NotNull;

public class CancellableThread<T_Arg> implements AutoCloseable
{
    private final Thread _thread;
    private final CancellableThreadDelegate<T_Arg> _threadCallback;
    private final CancellationTokenSource _tokenSource;

    public CancellableThread(@NotNull CancellableThreadRunnable<T_Arg> threadStartCallback, T_Arg arg)
    {
        _tokenSource = new CancellationTokenSource();
        _threadCallback = new CancellableThreadDelegate<>(threadStartCallback,  arg, _tokenSource.getToken());
        _thread = new Thread(_threadCallback);
    }

    public CancellableThread(@NotNull CancellableThreadRunnable<T_Arg> threadStartCallback, T_Arg arg, CancellationToken token)
    {
        _tokenSource = CancellationTokenSource.createLinkedTokenSource(token);
        _threadCallback = new CancellableThreadDelegate<>(threadStartCallback, arg, _tokenSource.getToken());
        _thread = new Thread(_threadCallback);
    }

    public void start()
    {
        _thread.start();
    }

    public void cancel()
    {
        _tokenSource.cancel();
    }

    public void close()
    {
        _tokenSource.cancel();
        _tokenSource.close();
    }

}