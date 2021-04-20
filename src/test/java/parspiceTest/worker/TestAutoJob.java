package parspiceTest.worker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import parspiceTest.ParSPICEInstance;
import parspice.job.AutoJob;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestAutoJob extends AutoJob {
    int numTestTasks = 10;

    @Override
    public void task(int i) throws Exception {
        int hello = i * 2;
    }

    @Test
    public void testRun() {
        assertDoesNotThrow(() -> (new TestAutoJob())
                .init(2, numTestTasks)
                .run(ParSPICEInstance.par));
    }
}
