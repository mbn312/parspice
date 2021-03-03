package parspice.worker;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import parspice.rpc.*;

import io.grpc.stub.StreamObserver;

import spice.basic.CSPICE;
import spice.basic.IDCodeNotFoundException;
import spice.basic.SpiceErrorException;


import java.util.List;

public class SpiceService extends ParSPICEGrpc.ParSPICEImplBase {

    private RepeatedDouble arrayToRepDouble(double[] response) {
        RepeatedDouble.Builder repDoubleBuilder = RepeatedDouble.newBuilder();
        List<Double> list = Doubles.asList(response);
        repDoubleBuilder.addAllArray(list);
        return repDoubleBuilder.build();
    }

    private RepeatedInteger intArrayToRepInt(int[] response) {
        RepeatedInteger.Builder repIntegerBuilder = RepeatedInteger.newBuilder();
        List<Integer> list = Ints.asList(response);
        repIntegerBuilder.addAllArray(list);
        return repIntegerBuilder.build();
    }

    private double[] repDoubleToArray(RepeatedDouble repDouble) {
        return repDouble.getArrayList().stream().mapToDouble(Double::doubleValue).toArray();
    }


    // one arg simple return example
    @Override
    public void bodn2cRPC(Bodn2cRequest request, StreamObserver<Bodn2cResponse> responseObserver) {
        
        // get args
        List<Bodn2cRequest.Bodn2cInput> args = request.getInputsList();

        // make request to spice and build responses
        Bodn2cResponse.Builder replyBuilder = Bodn2cResponse.newBuilder();
        for (int i = 0; i < request.getInputsCount(); i++) {
            try {
                // make spice call
                int result = CSPICE.bodn2c(args.get(i).getName());
                // add result to output
                Bodn2cResponse.Bodn2cOutput output = Bodn2cResponse.Bodn2cOutput.newBuilder().setRet(result).build();
                // add output to reply
                replyBuilder.setOutputs(i ,output);
            } catch (SpiceErrorException | IDCodeNotFoundException err) {
                System.out.println(err);
            }
        }
        Bodn2cResponse reply = replyBuilder.build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }


    // multiple arguments double array return example
    @Override
    public void dpgrdrRPC(DpgrdrRequest request, StreamObserver<DpgrdrResponse> responseObserver) {

        // get args
        List<DpgrdrRequest.DpgrdrInput> args = request.getInputsList();

        // make requests to spice and build responses
        DpgrdrResponse.Builder replyBuilder = DpgrdrResponse.newBuilder();

        for (int i = 0; i < request.getInputsCount(); i++) {
            try {
                DpgrdrResponse.DpgrdrOutput.Builder outputBuilder = DpgrdrResponse.DpgrdrOutput.newBuilder();
                // make spice calls
                double[][] result = CSPICE.dpgrdr(
                        args.get(i).getBody(),
                        args.get(i).getX(),
                        args.get(i).getY(),
                        args.get(i).getZ(),
                        args.get(i).getRe(),
                        args.get(i).getF()
                );
                // add result to output
                for (int j = 0; j< result.length; j++) {
                    RepeatedDouble ret = this.arrayToRepDouble(result[j]);
                    outputBuilder.setRet(j, ret);
                }
                // add output to reply
                replyBuilder.addOutputs(i, outputBuilder.build());
            } catch (SpiceErrorException err) {
                System.out.println(err);
            }
        }
        // build and return reply
        DpgrdrResponse reply = replyBuilder.build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    // double matrix and vector input, matrix output
    @Override
    public void mxmRPC(MxmRequest request, StreamObserver<MxmResponse> responseObserver) {

        List<MxmRequest.MxmInput> args = request.getInputsList();

        // make requests to spice and build responses
        MxmResponse.Builder replyBuilder = MxmResponse.newBuilder();

        for (int i = 0; i < request.getInputsCount(); i++) {
            try {
                MxmResponse.MxmOutput.Builder outputBuilder = MxmResponse.MxmOutput.newBuilder();

                // convert List<RepeatedDouble> to double[][]
                List<RepeatedDouble> m1List = args.get(i).getM1List();
                List<RepeatedDouble> m2List = args.get(i).getM2List();
                double[][] m1 = {{0,0,0},{0,0,0}, {0,0,0}};
                double[][] m2 = {{0,0,0},{0,0,0}, {0,0,0}};
                for (int j = 0; j < m1List.size(); j++) {
                     m1[j] = this.repDoubleToArray(m1List.get(j));
                     m2[j] = this.repDoubleToArray(m2List.get(j));
                }

                // make spice calls
                double[][] result = CSPICE.mxm(m1, m2);
                // add result to output
                for (int j = 0; j< result.length; j++) {
                    RepeatedDouble ret = this.arrayToRepDouble(result[j]);
                    outputBuilder.setRet(j, ret);
                }
                // add output to reply
                replyBuilder.addOutputs(i, outputBuilder.build());
            } catch (SpiceErrorException err) {
                System.out.println(err);
            }
        }
        // build and return reply
        MxmResponse reply = replyBuilder.build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
