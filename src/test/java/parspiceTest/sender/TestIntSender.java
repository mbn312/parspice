package parspiceTest.sender;

import org.junit.jupiter.api.TestInstance;
import parspice.job.OJob;
import parspiceTest.ParSPICEInstance;
import parspice.sender.IntSender;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestIntSender extends OJob<Integer> {
    ArrayList<Integer> parResults;
    int numTestTasks = 10;

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
            parResults = (new TestIntSender())
                    .init(2, numTestTasks)
                    .run(ParSPICEInstance.par);
        });
    }

    @Test
    public void testCorrectness() {
        List<Integer> directResults = new ArrayList<Integer>(numTestTasks);
        for (int i = 0; i < numTestTasks; i++) {
            directResults.add(i);
        }
        assertArrayEquals(parResults.toArray(), directResults.toArray());
    }
}
