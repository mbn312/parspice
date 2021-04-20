package parspiceTest.sender;

import org.junit.jupiter.api.TestInstance;
import parspiceTest.ParSPICEInstance;
import parspice.sender.StringMatrixSender;
import parspice.job.OJob;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestStringMatrixSender extends OJob<String[][]> {
    ArrayList<String[][]> parResults;
    int numTestTasks = 10;

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
            parResults = (new TestStringMatrixSender())
                    .init(2, numTestTasks)
                    .run(ParSPICEInstance.par);
        });

    }

    @Test
    public void testCorrectness() {
        List<String[][]> directResults = new ArrayList<String[][]>(numTestTasks);
        for (int i = 0; i < numTestTasks; i++) {
            String[][] x = {{"Test","Correct"},{"Test","Correct"}};
            directResults.add(x);
        }

        assertArrayEquals(parResults.toArray(), directResults.toArray());
    }
}
