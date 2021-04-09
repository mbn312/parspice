package parspice.testGeneral;

import org.junit.jupiter.api.TestInstance;
import parspice.ParSPICEInstance;
import parspice.sender.StringMatrixSender;
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
public class BasicStringMatrixOutput extends OWorker<String[][]> {
    List<String[][]> parResults;
    int numIterations = 10;

    public BasicStringMatrixOutput() {
        super(new StringMatrixSender());
    }

    @Override
    public String[][] task(int i) throws Exception {
        System.out.println(i);
        String[][] results = {{"Test","Correct"},{"Test","Correct"}};
        return results;
    }

    @Test
    @BeforeAll
    public void testRun() {
        assertDoesNotThrow(() -> {
            parResults = ParSPICEInstance.par.run(
                    new BasicStringMatrixOutput(),
                    numIterations,
                    2
            );
        });

    }

    @Test
    public void testCorrectness() {
        List<String[][]> directResults = new ArrayList<String[][]>(numIterations);
        for (int i = 0; i < numIterations; i++) {
            String[][] x = {{"Test","Correct"},{"Test","Correct"}};
            directResults.add(x);
        }

        assertArrayEquals(parResults.toArray(), directResults.toArray());
    }
}
