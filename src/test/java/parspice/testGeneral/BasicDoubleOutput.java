package parspice.testGeneral;

import org.junit.jupiter.api.TestInstance;
import parspice.ParSPICEInstance;
import parspice.sender.DoubleSender;
import parspice.sender.Sender;
import parspice.worker.OWorker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BasicDoubleOutput extends OWorker<Double> {
    List<Double> parResults;
    int numIterations = 10;

    public BasicDoubleOutput() {
        super(new DoubleSender());
    }

    @Override
    public Double task(int i) throws Exception {
        return i/2.;
    }

    @Test
    @BeforeAll
    public void testRun() {
        assertDoesNotThrow(() -> {
            parResults = ParSPICEInstance.par.run(
                    new BasicDoubleOutput(),
                    numIterations,
                    2
            );
        });
    }

    @Test
    public void testCorrectness() {
        List<Double> directResults = new ArrayList<>(numIterations);
        for (int i = 0; i < numIterations; i++) {
            directResults.add(i/2.);
        }
        assertArrayEquals(parResults.toArray(), directResults.toArray());
    }
}
