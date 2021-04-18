package parspiceTest.worker;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import parspice.sender.DoubleSender;
import parspice.sender.IntSender;
import parspice.worker.IOWorker;
import parspice.worker.SIOWorker;
import parspiceTest.ParSPICEInstance;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestSIOWorker extends SIOWorker<Double, Integer, Double> {
    ArrayList<Double> parResults;
    int numIterations = 10;

    double offset = 0;

    public TestSIOWorker() {
        super(new DoubleSender(), new IntSender(), new DoubleSender());
    }

    @Override
    public void setup(Double d) {
        offset = d;
    }

    @Override
    public Double task(Integer i) throws Exception {
        return i + offset;
    }

    @Test
    @BeforeAll
    public void testRun() {
        assertDoesNotThrow(() -> {
            List<Integer> inputs = new ArrayList<>(numIterations);
            for (int i = 0; i < numIterations; i++) {
                inputs.add(i * 2);
            }
            parResults = ParSPICEInstance.par.run(
                    (new TestSIOWorker()).job().setupInput(3.0).inputs(inputs),
                    2
            ).getOutputs();
        });
    }

    @Test
    public void testCorrectness() {
        List<Double> directResults = new ArrayList<>(numIterations);
        for (int i = 0; i < numIterations; i++) {
            directResults.add(2*i + 3.0);
        }
        assertArrayEquals(directResults.toArray(), parResults.toArray());
    }
}
