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
public class TestSOJobMultipleInputs extends SOJob<Double, Double> {
    ArrayList<Double> parResults;
    int numTestTasks = 10;

    double offset = 0;

    public TestSOJobMultipleInputs() {
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
            List<Double> setupInputs = new ArrayList<>(numTestTasks);
            for (int i = 0; i < 2; i++) {
                setupInputs.add((double) i);
            }
            parResults = (new TestSOJobMultipleInputs())
                    .init(numTestTasks, setupInputs)
                    .run(ParSPICEInstance.par);
        });
    }

    @Test
    public void testCorrectness() {
        List<Double> directResults = new ArrayList<>(numTestTasks);
        for (int i = 0; i < numTestTasks; i++) {
            if (i < numTestTasks /2)
                directResults.add((double) i);
            else
                directResults.add((double) i + 1);
        }
        assertArrayEquals(directResults.toArray(), parResults.toArray());
    }
}
