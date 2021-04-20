package parspiceTest.worker;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import parspice.sender.DoubleSender;
import parspice.sender.IntSender;
import parspice.job.SIJob;
import parspiceTest.ParSPICEInstance;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestSIJob extends SIJob<Double, Integer> {
    int numTestTasks = 10;

    double offset = 0;

    public TestSIJob() {
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
            List<Integer> inputs = new ArrayList<>(numTestTasks);
            for (int i = 0; i < numTestTasks; i++) {
                inputs.add(i * 2);
            }
            (new TestSIJob())
                    .init(2, 3.0, inputs)
                    .run(ParSPICEInstance.par);
        });
    }
}
