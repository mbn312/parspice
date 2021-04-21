package parspiceTest.worker;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import parspiceTest.ParSPICEInstance;
import parspice.sender.IntSender;


import org.junit.jupiter.api.Test;
import parspice.worker.OWorker;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestSetup extends OWorker<Integer> {
    ArrayList<Integer> parResults;
    int numTestTasks = 10;

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
            parResults = (new TestSetup())
                    .init(2, numTestTasks)
                    .run(ParSPICEInstance.par);
        });
    }

    @Test
    public void testCorrectness() {
        List<Integer> directResults = new ArrayList<>(numTestTasks);
        for (int i = 0; i < numTestTasks; i++) {
            directResults.add(i + 2);
        }
        assertArrayEquals(parResults.toArray(), directResults.toArray());
    }
}
