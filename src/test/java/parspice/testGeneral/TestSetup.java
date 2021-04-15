package parspice.testGeneral;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import parspice.ParSPICEInstance;
import parspice.sender.IntSender;
import parspice.worker.OWorker;


import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestSetup extends OWorker<Integer> {
    ArrayList<Integer> parResults;
    int numIterations = 10;

    private static int n = 0;

    public TestSetup() {
        super(new IntSender());
    }

    @Override
    public void setup() throws Exception {
        n = 2;
    }

    @Override
    public Integer task(int i) throws Exception {
        return n + i;
    }

    @Test
    @BeforeAll
    public void testRun() {
        assertDoesNotThrow(() -> {
            parResults = ParSPICEInstance.par.run(
                    new TestSetup(),
                    numIterations,
                    2
            );
        });
    }

    @Test
    public void testCorrectness() {
        List<Integer> directResults = new ArrayList<>(numIterations);
        for (int i = 0; i < numIterations; i++) {
            directResults.add(i + 2);
        }
        assertArrayEquals(parResults.toArray(), directResults.toArray());
    }
}
