package parspice;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import parspice.ParSPICE;
import parspice.rpc.ParSPICEGrpc;

public class ParSPICEBuilder {
    private String uri = "localhost";
    private String port = "50051";

    public ParSPICEBuilder setURI(String uri) {
        this.uri = uri;
        return this;
    }

    public ParSPICEBuilder setPort(String port) {
        this.port = port;
        return this;
    }

    public ParSPICE build() {
        String target = this.uri + ":" + this.port;
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                .usePlaintext()
                .build();
        return new ParSPICE(ParSPICEGrpc.newFutureStub(channel), ParSPICEGrpc.newBlockingStub(channel));
    }
}
