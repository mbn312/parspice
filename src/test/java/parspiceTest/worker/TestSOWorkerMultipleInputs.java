package parspiceTest.worker;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import parspice.sender.DoubleSender;
import parspice.worker.SOWorker;
import parspiceTest.ParSPICEInstance;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestSOWorkerMultipleInputs extends SOWorker<Double, Double> {
    ArrayList<Double> parResults;
    int numIterations = 10;

    double offset = 0;

    public TestSOWorkerMultipleInputs() {
        super(new DoubleSender(), new DoubleSender());
    }

    @Override
    public void setup(Double d) {
        offset = d;
    }

    @Override
    public Double task(int i) throws Exception {
        return i + offset;
    }

    @Test
    @BeforeAll
    public void testRun() {
        assertDoesNotThrow(() -> {
            List<Double> setupInputs = new ArrayList<>(numIterations);
            for (int i = 0; i < 2; i++) {
                setupInputs.add((double) i);
            }
            parResults = ParSPICEInstance.par.run(
                    (new TestSOWorkerMultipleInputs()).job().setupInputs(setupInputs).numTasks(numIterations),
                    2
            ).getOutputs();
        });
    }

    @Test
    public void testCorrectness() {
        List<Double> directResults = new ArrayList<>(numIterations);
        for (int i = 0; i < numIterations; i++) {
            if (i < numIterations/2)
                directResults.add((double) i);
            else
                directResults.add((double) i + 1);
        }
        assertArrayEquals(directResults.toArray(), parResults.toArray());
    }
}
