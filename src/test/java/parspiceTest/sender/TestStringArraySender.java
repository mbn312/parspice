package parspiceTest.sender;

import org.junit.jupiter.api.TestInstance;
import parspice.worker.OWorker;
import parspiceTest.ParSPICEInstance;
import parspice.sender.StringArraySender;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestStringArraySender extends OWorker<String[]> {
    ArrayList<String[]> parResults;
    int numTestTasks = 10;

    public TestStringArraySender() {
        super(new StringArraySender());
    }

    @Override
    public String[] task(int i) throws Exception {
        System.out.println(i);
        String[] results = {"Test","Correct"};
        return results;
    }

    @Test
    @BeforeAll
    public void testRun() {
        assertDoesNotThrow(() -> {
            parResults = (new TestStringArraySender())
                    .init(2, numTestTasks)
                    .run(ParSPICEInstance.par);
        });

    }

    @Test
    public void testCorrectness() {
        List<String[]> directResults = new ArrayList<String[]>(numTestTasks);
        for (int i = 0; i < numTestTasks; i++) {
            String[] x = {"Test","Correct"};
            directResults.add(x);
        }
        assertArrayEquals(parResults.toArray(), directResults.toArray());
    }
}
