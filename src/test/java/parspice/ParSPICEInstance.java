package parspice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
