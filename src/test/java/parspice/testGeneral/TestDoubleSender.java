package parspice.testGeneral;

import org.junit.jupiter.api.TestInstance;
import parspice.ParSPICEInstance;
import parspice.sender.DoubleSender;
import parspice.worker.OWorker;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestDoubleSender extends OWorker<Double> {
    ArrayList<Double> parResults;
    int numIterations = 10;

    public TestDoubleSender() {
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
                    new TestDoubleSender(),
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
