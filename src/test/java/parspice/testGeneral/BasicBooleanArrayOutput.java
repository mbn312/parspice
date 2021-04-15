package parspice.testGeneral;

import org.junit.jupiter.api.TestInstance;
import parspice.ParSPICEInstance;
import parspice.sender.BooleanArraySender;
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
public class BasicBooleanArrayOutput extends OWorker<boolean[]> {
    ArrayList<boolean[]> parResults;
    int numIterations = 10;

    public BasicBooleanArrayOutput() {
        super(new BooleanArraySender());
    }

    @Override
    public boolean[] task(int i) throws Exception {
        System.out.println(i);
        boolean[] re = {false,true};
        return re;
    }

    @Test
    @BeforeAll
    public void testRun() {
        assertDoesNotThrow(() -> {
            parResults = ParSPICEInstance.par.run(
                    new BasicBooleanArrayOutput(),
                    numIterations,
                    2
            );
        });

    }

    @Test
    public void testCorrectness() {
        List<boolean[]> directResults = new ArrayList<boolean[]>(numIterations);
        for (int i = 0; i < numIterations; i++) {
            boolean[] x = {false,true};
            directResults.add(x);
        }


        assertArrayEquals(parResults.toArray(), directResults.toArray());
    }
}
