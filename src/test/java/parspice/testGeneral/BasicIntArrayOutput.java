package parspice.testGeneral;

import org.junit.jupiter.api.TestInstance;
import parspice.ParSPICEInstance;
import parspice.sender.IntArraySender;
import parspice.sender.Sender;
import parspice.worker.OWorker;
import parspice.worker.IOWorker;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BasicIntArrayOutput extends OWorker<int[]> {
    ArrayList<int[]> parResults;
    int numIterations = 10;

    public BasicIntArrayOutput() {
        super(new IntArraySender());
    }

    @Override
    public int[] task(int i) throws Exception {
        System.out.println(i);
        int[] results = {1,2};
        return results;
    }

    @Test
    @BeforeAll
    public void testRun() {
        assertDoesNotThrow(() -> {
            parResults = ParSPICEInstance.par.run(
                    new BasicIntArrayOutput(),
                    numIterations,
                    2
            );
        });

    }

    @Test
    public void testCorrectness() {
        List<int[]> directResults = new ArrayList<int[]>(numIterations);
        for (int i = 0; i < numIterations; i++) {
            int[] x = {1,2};
            directResults.add(x);
        }
        assertArrayEquals(parResults.toArray(), directResults.toArray());
    }
}
