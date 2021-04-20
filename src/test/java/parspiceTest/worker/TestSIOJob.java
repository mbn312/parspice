package parspiceTest.worker;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import parspice.sender.DoubleSender;
import parspice.sender.IntSender;
import parspice.job.SIOJob;
import parspiceTest.ParSPICEInstance;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestSIOJob extends SIOJob<Double, Integer, Double> {
    ArrayList<Double> parResults;
    int numTestTasks = 10;

    double offset = 0;

    public TestSIOJob() {
        super(new DoubleSender(), new IntSender(), new DoubleSender());
    }

    @Override
    public void setup(Double d) {
        offset = d;
    }

    @Override
    public Double task(Integer i) throws Exception {
        return i + offset;
    }

    @Test
    @BeforeAll
    public void testRun() {
        assertDoesNotThrow(() -> {
            List<Integer> inputs = new ArrayList<>(numTestTasks);
            for (int i = 0; i < numTestTasks; i++) {
                inputs.add(i * 2);
            }
            parResults = (new TestSIOJob())
                    .init(2, 3.0, inputs)
                    .run(ParSPICEInstance.par);
        });
    }

    @Test
    public void testCorrectness() {
        List<Double> directResults = new ArrayList<>(numTestTasks);
        for (int i = 0; i < numTestTasks; i++) {
            directResults.add(2*i + 3.0);
        }
        assertArrayEquals(directResults.toArray(), parResults.toArray());
    }
}
