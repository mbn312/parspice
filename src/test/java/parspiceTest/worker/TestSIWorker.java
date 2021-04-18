package parspiceTest.worker;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import parspice.sender.DoubleSender;
import parspice.sender.IntSender;
import parspice.worker.SIWorker;
import parspiceTest.ParSPICEInstance;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestSIWorker extends SIWorker<Double, Integer> {
    int numIterations = 10;

    double offset = 0;

    public TestSIWorker() {
        super(new DoubleSender(), new IntSender());
    }

    @Override
    public void setup(Double d) {
        offset = d;
    }

    @Override
    public void task(Integer i) throws Exception {
        double hello = i + offset;
    }

    @Test
    @BeforeAll
    public void testRun() {
        assertDoesNotThrow(() -> {
            List<Integer> inputs = new ArrayList<>(numIterations);
            for (int i = 0; i < numIterations; i++) {
                inputs.add(i * 2);
            }
            ParSPICEInstance.par.run(
                    (new TestSIWorker()).job().setupInput(3.0).inputs(inputs),
                    2
            );
        });
    }
}
