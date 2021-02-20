package parspice.worker;

import io.grpc.stub.StreamObserver;
import com.google.protobuf.Descriptors.FieldDescriptor;
import parspice.rpc.*;
import spice.basic.CSPICE;
import spice.basic.SpiceErrorException;

import java.util.Arrays;
import java.util.List;

public class SpiceService extends ParSpiceGrpc.ParSpiceImplBase {

    // one arg simple return
    @Override
    public void bodn2cRPC(Bodn2cRequest request, StreamObserver<Bodn2cResponse> responseObserver) {
        super.bodn2cRPC(request, responseObserver);
    }



    // multiple arguments double array return
    @Override
    public void dpgrdrRPC(DpgrdrRequest request, StreamObserver<DpgrdrResponse> responseObserver) {

        // get args
        List<DpgrdrRequest.DpgrdrInput> args = request.getInputsList();

        // take request and build output
        DpgrdrResponse.DpgrdrOutput.Builder outputBuilder = DpgrdrResponse.DpgrdrOutput.newBuilder();

        for (int i = 0; i< request.getInputsCount(); i++) {
            try {
                double[][] response = CSPICE.dpgrdr(
                        args.get(i).getBody(),
                        args.get(i).getX(),
                        args.get(i).getY(),
                        args.get(i).getZ(),
                        args.get(i).getRe(),
                        args.get(i).getF()
                );


                RepeatedDouble.Builder repDoubleBuilder = RepeatedDouble.newBuilder();
                for (int j = 0; j < response.length; j++) {
                    Iterable<Double> res = Arrays.asList(response[j]);
                    repDoubleBuilder.addArray(res);
                }

                // add output
                RepeatedDouble ret = repDoubleBuilder.build();
                outputBuilder.addRet(ret);
            } catch (SpiceErrorException err) {
                System.out.println(err);
            }
        }

        DpgrdrResponse.Builder replyBuilder = DpgrdrResponse.newBuilder();
        DpgrdrResponse reply = replyBuilder.addOutputs(outputBuilder.build()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }


}
