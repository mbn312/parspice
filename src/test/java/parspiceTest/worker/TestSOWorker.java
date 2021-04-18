package parspiceTest.worker;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import parspice.sender.DoubleSender;
import parspice.sender.IntSender;
import parspice.worker.SIOWorker;
import parspice.worker.SOWorker;
import parspiceTest.ParSPICEInstance;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestSOWorker extends SOWorker<Double, Double> {
    ArrayList<Double> parResults;
    int numIterations = 10;

    double offset = 0;

    public TestSOWorker() {
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
            parResults = ParSPICEInstance.par.run(
                    (new TestSOWorker()).job().setupInput(3.0).numTasks(numIterations),
                    2
            ).getOutputs();
        });
    }

    @Test
    public void testCorrectness() {
        List<Double> directResults = new ArrayList<>(numIterations);
        for (int i = 0; i < numIterations; i++) {
            directResults.add(i + 3.0);
        }
        assertArrayEquals(directResults.toArray(), parResults.toArray());
    }
}
