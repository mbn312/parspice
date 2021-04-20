package parspiceTest.worker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import parspice.job.IJob;
import parspiceTest.ParSPICEInstance;
import parspice.sender.IntSender;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestIJob extends IJob<Integer> {
    int numTestTasks = 10;

    public TestIJob() {
        super(new IntSender());
    }

    @Override
    public void task(Integer i) throws Exception {
        int hello = i * 2;
    }

    @Test
    public void testRun() {
        assertDoesNotThrow(() -> {
            List<Integer> inputs = new ArrayList<>(numTestTasks);
            for (int i = 0; i < numTestTasks; i++) {
                inputs.add(i * 2);
            }
            (new TestIJob())
                    .init(2, inputs)
                    .run(ParSPICEInstance.par);
        });
    }
}
