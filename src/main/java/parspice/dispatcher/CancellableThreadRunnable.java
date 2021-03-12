package parspice.dispatcher;

import util.concurrent.CancellationToken;

public interface CancellableThreadRunnable<T_Arg> {
    void run(T_Arg Arg, CancellationToken Token) throws Throwable;
}
