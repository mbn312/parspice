package parspiceTest.sender;

import org.junit.jupiter.api.TestInstance;
import parspice.job.OJob;
import parspiceTest.ParSPICEInstance;
import parspice.sender.BooleanMatrixSender;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestBooleanMatrixSender extends OJob<boolean[][]> {
    ArrayList<boolean[][]> parResults;
    int numTestTasks = 10;

    public TestBooleanMatrixSender() {
        super(new BooleanMatrixSender());
    }

    @Override
    public boolean[][] task(int i) throws Exception {
        System.out.println(i);
        boolean[][] re = {{false,true},{false,true}};
        return re;
    }

    @Test
    @BeforeAll
    public void testRun() {
        assertDoesNotThrow(() -> {
            parResults = (new TestBooleanMatrixSender())
                    .init(2, numTestTasks)
                    .run(ParSPICEInstance.par);
        });

    }

    @Test
    public void testCorrectness() {
        List<boolean[][]> directResults = new ArrayList<boolean[][]>(numTestTasks);
        for (int i = 0; i < numTestTasks; i++) {
            boolean[][] x = {{false,true},{false,true}};
            directResults.add(x);
        }

        assertArrayEquals(parResults.toArray(), directResults.toArray());
    }
}
