package parspiceTest.sender;

import org.junit.jupiter.api.TestInstance;
import parspiceTest.ParSPICEInstance;
import parspice.sender.StringSender;
import parspice.worker.OWorker;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestStringSender extends OWorker<String> {
    ArrayList<String> parResults;
    int numIterations = 10;

    public TestStringSender() {
        super(new StringSender());
    }

    @Override
    public String task(int i) throws Exception {
        return "Test";
    }

    @Test
    @BeforeAll
    public void testRun() {
        assertDoesNotThrow(() -> {
            parResults = ParSPICEInstance.par.run(
                    (new TestStringSender()).job().numTasks(numIterations),
                    2
            ).getOutputs();
        });
    }

    @Test
    public void testCorrectness() {
        List<String> directResults = new ArrayList<String>(numIterations);
        for (int i = 0; i < numIterations; i++) {
            directResults.add("Test");
        }
        assertArrayEquals(parResults.toArray(), directResults.toArray());
    }
}
