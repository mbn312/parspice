package parspiceTest.worker;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import parspice.sender.DoubleSender;
import parspice.job.SOJob;
import parspiceTest.ParSPICEInstance;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestSOJob extends SOJob<Double, Double> {
    ArrayList<Double> parResults;
    int numTestTasks = 10;

    double offset = 0;

    public TestSOJob() {
        super(new DoubleSender(), new DoubleSender());
    }

    @Override
    public void setup(Double d) {
        offset = d;
    }

    @Override
    public Double task(int i) throws Exception {
        return i + offset;
    }

    @Test
    @BeforeAll
    public void testRun() {
        assertDoesNotThrow(() -> {
            parResults = (new TestSOJob())
                    .init(2, numTestTasks, 3.0)
                    .run(ParSPICEInstance.par);
        });
    }

    @Test
    public void testCorrectness() {
        List<Double> directResults = new ArrayList<>(numTestTasks);
        for (int i = 0; i < numTestTasks; i++) {
            directResults.add(i + 3.0);
        }
        assertArrayEquals(directResults.toArray(), parResults.toArray());
    }
}
