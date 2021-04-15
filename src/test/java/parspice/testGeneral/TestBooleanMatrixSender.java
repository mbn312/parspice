package parspice.testGeneral;

import org.junit.jupiter.api.TestInstance;
import parspice.ParSPICEInstance;
import parspice.sender.BooleanMatrixSender;
import parspice.worker.OWorker;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestBooleanMatrixSender extends OWorker<boolean[][]> {
    ArrayList<boolean[][]> parResults;
    int numIterations = 10;

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
            parResults = ParSPICEInstance.par.run(
                    new TestBooleanMatrixSender(),
                    numIterations,
                    2
            );
        });

    }

    @Test
    public void testCorrectness() {
        List<boolean[][]> directResults = new ArrayList<boolean[][]>(numIterations);
        for (int i = 0; i < numIterations; i++) {
            boolean[][] x = {{false,true},{false,true}};
            directResults.add(x);
        }

        assertArrayEquals(parResults.toArray(), directResults.toArray());
    }
}
