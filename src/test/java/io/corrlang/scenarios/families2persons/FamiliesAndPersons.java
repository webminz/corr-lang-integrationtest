package io.corrlang.scenarios.families2persons;

import io.corrlang.SystemTest;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class FamiliesAndPersons extends SystemTest {

    private static final String CORRSPEC_FILE = "familiesPersons.corr";

    @Test
    public void testRunHelpGoal() throws Throwable {
        runGoal(CORRSPEC_FILE, "HELP");
        String infos = reportFacade.getInfos();
        String expected = "aa";
        assertEquals(expected, infos);
    }

    @Test
    public void testRunVerify() throws Throwable {
        Properties properties = new Properties();
        properties.put("useConfig", "/Users/past/Documents/dev/bx/corrlang-performance/checkFileBased-execution/solutions/corrlang/corrlang.conf");
        runGoal(CORRSPEC_FILE, "Check", false, properties);
        // TODO check expected file
    }


    @Override
    protected String getWorkdir() {
        return "families2persons";
    }
}
