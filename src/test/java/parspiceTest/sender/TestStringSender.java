package parspiceTest.sender;

import org.junit.jupiter.api.TestInstance;
import parspice.job.OJob;
import parspiceTest.ParSPICEInstance;
import parspice.sender.StringSender;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestStringSender extends OJob<String> {
    ArrayList<String> parResults;
    int numTestTasks = 10;

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
            parResults = (new TestStringSender())
                    .init(2, numTestTasks)
                    .run(ParSPICEInstance.par);
        });
    }

    @Test
    public void testCorrectness() {
        List<String> directResults = new ArrayList<String>(numTestTasks);
        for (int i = 0; i < numTestTasks; i++) {
            directResults.add("Test");
        }
        assertArrayEquals(parResults.toArray(), directResults.toArray());
    }
}
