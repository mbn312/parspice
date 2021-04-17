package parspiceTest.sender;

import org.junit.jupiter.api.TestInstance;
import parspiceTest.ParSPICEInstance;
import parspice.sender.BooleanArraySender;
import parspice.worker.OWorker;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestBooleanArraySender extends OWorker<boolean[]> {
    ArrayList<boolean[]> parResults;
    int numIterations = 10;

    public TestBooleanArraySender() {
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
                    new TestBooleanArraySender(),
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
