package parspice.testGeneral;

import org.junit.jupiter.api.TestInstance;
import parspice.ParSPICEInstance;
import parspice.sender.IntSender;
import parspice.sender.Sender;
import parspice.worker.OWorker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BasicIntOutput extends OWorker<Integer> {
    List<Integer> parResults;
    int numIterations = 10;

    public BasicIntOutput() {
        super(new IntSender());
    }

    @Override
    public Integer task(int i) throws Exception {
        return i;
    }

    @Test
    @BeforeAll
    public void testRun() {
        assertDoesNotThrow(() -> {
            parResults = ParSPICEInstance.par.run(
                    new BasicIntOutput(),
                    numIterations,
                    2
            );
        });
    }

    @Test
    public void testCorrectness() {
        List<Integer> directResults = new ArrayList<Integer>(numIterations);
        for (int i = 0; i < numIterations; i++) {
            directResults.add(i);
        }
        assertArrayEquals(parResults.toArray(), directResults.toArray());
    }
}
