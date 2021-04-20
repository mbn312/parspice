package parspiceTest.sender;

import org.junit.jupiter.api.TestInstance;
import parspice.job.OJob;
import parspiceTest.ParSPICEInstance;
import parspice.sender.DoubleArraySender;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestDoubleArraySender extends OJob<double[]> {
    ArrayList<double[]> parResults;
    int numTestTasks = 10;

    public TestDoubleArraySender() {
        super(new DoubleArraySender());
    }

    @Override
    public double[] task(int i) throws Exception {
        System.out.println(i);
        double[] results = {1.1,2.2};
        return results;
    }

    @Test
    @BeforeAll
    public void testRun() {
        assertDoesNotThrow(() -> {
            parResults = (new TestDoubleArraySender())
                    .init(2, numTestTasks)
                    .run(ParSPICEInstance.par);
        });

    }

    @Test
    public void testCorrectness() {
        List<double[]> directResults = new ArrayList<double[]>(numTestTasks);
        for (int i = 0; i < numTestTasks; i++) {
            double[] x = {1.1,2.2};
            directResults.add(x);
        }
        assertArrayEquals(parResults.toArray(), directResults.toArray());
    }
}
