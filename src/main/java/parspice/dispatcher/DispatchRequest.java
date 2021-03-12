package parspice.dispatcher;

public class DispatchRequest<T_Request extends com.google.protobuf.MessageOrBuilder> {
    private T_Request request;
    private int size;
    private boolean isJobComplete;

    /**
     * Create new DispatchRequest
     * @param Request gRPC request object
     * @param Size Number of user arguments contained in this request
     * @param IsJobComplete Flag to signal whether the job contains more user arguments.  False if no arguments remain
     */
    public DispatchRequest(T_Request request, int size, boolean isJobComplete)
    {
        this.request = request;
        this.size = size;
        this.isJobComplete = isJobComplete;
    }

    public T_Request getRequest() {return request; }
    public int getSize() { return size; }
    public boolean getCompletionState() { return isJobComplete; }
}
