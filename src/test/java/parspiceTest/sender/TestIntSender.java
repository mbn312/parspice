package parspiceTest.sender;

import org.junit.jupiter.api.TestInstance;
import parspiceTest.ParSPICEInstance;
import parspice.sender.IntSender;
import parspice.worker.OWorker;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestIntSender extends OWorker<Integer> {
    ArrayList<Integer> parResults;
    int numIterations = 10;

    public TestIntSender() {
        super(new IntSender());
    }

    @Override
    public Integer task(int i) throws Exception {
        return i;
    }

    @Test
    @BeforeAll
    public void testRun() {
        assertDoesNotThrow(() -> {
            parResults = ParSPICEInstance.par.run(
                    (new TestIntSender()).job().numTasks(numIterations),
                    2
            ).getOutputs();
        });
    }

    @Test
    public void testCorrectness() {
        List<Integer> directResults = new ArrayList<Integer>(numIterations);
        for (int i = 0; i < numIterations; i++) {
            directResults.add(i);
        }
        assertArrayEquals(parResults.toArray(), directResults.toArray());
    }
}
