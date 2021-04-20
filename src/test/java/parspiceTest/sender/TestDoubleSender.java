package parspiceTest.sender;

import org.junit.jupiter.api.TestInstance;
import parspice.job.OJob;
import parspiceTest.ParSPICEInstance;
import parspice.sender.DoubleSender;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestDoubleSender extends OJob<Double> {
    ArrayList<Double> parResults;
    int numTestTasks = 10;

    public TestDoubleSender() {
        super(new DoubleSender());
    }

    @Override
    public Double task(int i) throws Exception {
        return i/2.;
    }

    @Test
    @BeforeAll
    public void testRun() {
        assertDoesNotThrow(() -> {
            parResults = (new TestDoubleSender())
                    .init(2, numTestTasks)
                    .run(ParSPICEInstance.par);
        });
    }

    @Test
    public void testCorrectness() {
        List<Double> directResults = new ArrayList<>(numTestTasks);
        for (int i = 0; i < numTestTasks; i++) {
            directResults.add(i/2.);
        }
        assertArrayEquals(parResults.toArray(), directResults.toArray());
    }
}
