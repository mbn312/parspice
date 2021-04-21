package parspiceTest;

import parspice.ParSPICE;

import java.io.IOException;

public class ParSPICEInstance {
    public static ParSPICE par;

    static {
        try {
            par = new ParSPICE("build/libs/testing.jar", 50050);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
