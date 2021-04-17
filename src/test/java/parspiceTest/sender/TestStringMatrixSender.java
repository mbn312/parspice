package parspiceTest.sender;

import org.junit.jupiter.api.TestInstance;
import parspiceTest.ParSPICEInstance;
import parspice.sender.StringMatrixSender;
import parspice.worker.OWorker;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestStringMatrixSender extends OWorker<String[][]> {
    ArrayList<String[][]> parResults;
    int numIterations = 10;

    public TestStringMatrixSender() {
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
                    new TestStringMatrixSender(),
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
