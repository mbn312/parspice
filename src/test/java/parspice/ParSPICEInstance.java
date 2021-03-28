package parspice;

import java.io.IOException;

public class ParSPICEInstance {
    public static ParSPICE par;

    static {
        try {
            par = new ParSPICE("build/libs/testing-1.0-SNAPSHOT.jar", 50050);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
