package parspiceTest.worker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import parspiceTest.ParSPICEInstance;
import parspice.worker.AutoWorker;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestAutoWorker extends AutoWorker {
    int numTestTasks = 10;

    @Override
    public void task(int i) throws Exception {
        int hello = i * 2;
    }

    @Test
    public void testRun() {
        assertDoesNotThrow(() -> (new TestAutoWorker())
                .init(2, numTestTasks)
                .run(ParSPICEInstance.par));
    }
}
