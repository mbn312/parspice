package parspiceTest.worker;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import parspice.sender.DoubleSender;
import parspice.sender.IntSender;
import parspice.worker.SIWorker;
import parspice.worker.SWorker;
import parspiceTest.ParSPICEInstance;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestSWorker extends SWorker<Double> {
    int numIterations = 10;

    double offset = 0;

    public TestSWorker() {
        super(new DoubleSender());
    }

    @Override
    public void setup(Double d) {
        offset = d;
    }

    @Override
    public void task(int i) throws Exception {
        double hello = i + offset;
    }

    @Test
    @BeforeAll
    public void testRun() {
        assertDoesNotThrow(() -> {
            ParSPICEInstance.par.run(
                    (new TestSWorker()).job().setupInput(3.0).numTasks(numIterations),
                    2
            );
        });
    }
}
