package parspiceTest.worker;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import parspiceTest.ParSPICEInstance;
import parspice.sender.IntSender;
import parspice.job.IOJob;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestIOJob extends IOJob<Integer, Integer> {
    ArrayList<Integer> parResults;
    int numTestTasks = 10;

    public TestIOJob() {
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
            List<Integer> inputs = new ArrayList<>(numTestTasks);
            for (int i = 0; i < numTestTasks; i++) {
                inputs.add(i * 2);
            }
            parResults = (new TestIOJob())
                    .init(2, inputs)
                    .run(ParSPICEInstance.par);
        });
    }

    @Test
    public void testCorrectness() {
        List<Integer> directResults = new ArrayList<>(numTestTasks);
        for (int i = 0; i < numTestTasks; i++) {
            directResults.add(i*4);
        }
        assertArrayEquals(directResults.toArray(), parResults.toArray());
    }
}
