package parspiceTest.sender;

import org.junit.jupiter.api.TestInstance;
import parspice.worker.OWorker;
import parspiceTest.ParSPICEInstance;
import parspice.sender.BooleanSender;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestBooleanSender extends OWorker<Boolean> {
    ArrayList<Boolean> parResults;
    int numTestTasks = 10;

    public TestBooleanSender() {
        super(new BooleanSender());
    }

    @Override
    public Boolean task(int i) throws Exception {
        return true;
    }

    @Test
    @BeforeAll
    public void testRun() {
        assertDoesNotThrow(() -> {
            parResults = (new TestBooleanSender())
                    .init(2, numTestTasks)
                    .run(ParSPICEInstance.par);
        });
    }

    @Test
    public void testCorrectness() {
        List<Boolean> directResults = new ArrayList<>(numTestTasks);
        for (int i = 0; i < numTestTasks; i++) {
            directResults.add(true);
        }
        assertArrayEquals(parResults.toArray(), directResults.toArray());
    }
}
