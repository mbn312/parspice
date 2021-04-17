package parspiceTest.worker;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import parspiceTest.ParSPICEInstance;
import parspice.sender.IntSender;
import parspice.worker.IOWorker;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestIOWorker extends IOWorker<Integer, Integer> {
    ArrayList<Integer> parResults;
    int numIterations = 10;

    public TestIOWorker() {
        super(new IntSender(), new IntSender());
    }

    @Override
    public Integer task(Integer i) throws Exception {
        return i*2;
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
                    new TestIOWorker(),
                    inputs,
                    2
            );
        });
    }

    @Test
    public void testCorrectness() {
        List<Integer> directResults = new ArrayList<>(numIterations);
        for (int i = 0; i < numIterations; i++) {
            directResults.add(i*4);
        }
        assertArrayEquals(parResults.toArray(), directResults.toArray());
    }
}
