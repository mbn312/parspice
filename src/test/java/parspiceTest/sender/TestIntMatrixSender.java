package parspiceTest.sender;

import org.junit.jupiter.api.TestInstance;
import parspiceTest.ParSPICEInstance;
import parspice.sender.IntMatrixSender;
import parspice.worker.OWorker;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestIntMatrixSender extends OWorker<int[][]> {
    ArrayList<int[][]> parResults;
    int numTestTasks = 10;

    public TestIntMatrixSender() {
        super(new IntMatrixSender());
    }

    @Override
    public int[][] task(int i) throws Exception {
        System.out.println(i);
        int[][] results = {{1,2},{1,2}};
        return results;
    }

    @Test
    @BeforeAll
    public void testRun() {
        assertDoesNotThrow(() -> {
            parResults = (new TestIntMatrixSender())
                    .init(2, numTestTasks)
                    .run(ParSPICEInstance.par);
        });

    }

    @Test
    public void testCorrectness() {
        List<int[][]> directResults = new ArrayList<int[][]>(numTestTasks);
        for (int i = 0; i < numTestTasks; i++) {
            int[][] x = {{1,2},{1,2}};
            directResults.add(x);
        }

        assertArrayEquals(parResults.toArray(), directResults.toArray());
    }
}
