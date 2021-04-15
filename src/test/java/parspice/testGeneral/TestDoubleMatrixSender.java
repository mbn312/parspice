package parspice.testGeneral;

import org.junit.jupiter.api.TestInstance;
import parspice.ParSPICEInstance;
import parspice.sender.DoubleMatrixSender;
import parspice.worker.OWorker;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestDoubleMatrixSender extends OWorker<double[][]> {
    ArrayList<double[][]> parResults;
    int numIterations = 10;

    public TestDoubleMatrixSender() {
        super(new DoubleMatrixSender());
    }

    @Override
    public double[][] task(int i) throws Exception {
        System.out.println(i);
        double[][] results = {{1.1,2.2},{1.1,2.2}};
        return results;
    }

    @Test
    @BeforeAll
    public void testRun() {
        assertDoesNotThrow(() -> {
            parResults = ParSPICEInstance.par.run(
                    new TestDoubleMatrixSender(),
                    numIterations,
                    2
            );
        });

    }

    @Test
    public void testCorrectness() {
        List<double[][]> directResults = new ArrayList<double[][]>(numIterations);
        for (int i = 0; i < numIterations; i++) {
            double[][] x = {{1.1,2.2},{1.1,2.2}};
            directResults.add(x);
        }

        assertArrayEquals(parResults.toArray(), directResults.toArray());
    }
}
