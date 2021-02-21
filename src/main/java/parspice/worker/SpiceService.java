package parspice.worker;

import com.google.common.primitives.Doubles;
import parspice.rpc.*;

import io.grpc.stub.StreamObserver;

import spice.basic.CSPICE;
import spice.basic.IDCodeNotFoundException;
import spice.basic.SpiceErrorException;


import java.util.List;

public class SpiceService extends ParSpiceGrpc.ParSpiceImplBase {

    // one arg simple return
    @Override
    public void bodn2cRPC(Bodn2cRequest request, StreamObserver<Bodn2cResponse> responseObserver) {
        // get args
        List<Bodn2cRequest.Bodn2cInput> args = request.getInputsList();

        // make request to spice and build responses
        Bodn2cResponse.Builder replyBuilder = Bodn2cResponse.newBuilder();
        for (int i = 0; i< request.getInputsCount(); i++) {
            try {
                int response = CSPICE.bodn2c(args.get(i).getName());
                Bodn2cResponse.Bodn2cOutput output = Bodn2cResponse.Bodn2cOutput.newBuilder().setRet(response).build();
                replyBuilder.setOutputs(i ,output).build();
            } catch (SpiceErrorException | IDCodeNotFoundException err) {
                System.out.println(err);
            }
        }
        Bodn2cResponse reply = replyBuilder.build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }


    // multiple arguments double array return
    @Override
    public void dpgrdrRPC(DpgrdrRequest request, StreamObserver<DpgrdrResponse> responseObserver) {

        // get args
        List<DpgrdrRequest.DpgrdrInput> args = request.getInputsList();

        // make requests to spice and build responses
        DpgrdrResponse.Builder replyBuilder = DpgrdrResponse.newBuilder();

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

                // build repeated double to return.
                RepeatedDouble.Builder repDoubleBuilder = RepeatedDouble.newBuilder();
                for (int j = 0; j< response.length; j++) {
                    List<Double> list = Doubles.asList(response[j]);
                    repDoubleBuilder.addAllArray(list);
                }

                // build reply and return
                RepeatedDouble ret = repDoubleBuilder.build();
                DpgrdrResponse.DpgrdrOutput output = DpgrdrResponse.DpgrdrOutput.newBuilder().setRet(i, ret).build();
                replyBuilder.addOutputs(i, output);
            } catch (SpiceErrorException err) {
                System.out.println(err);
            }
        }

        DpgrdrResponse reply = replyBuilder.build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    // double array input simple output example


}
