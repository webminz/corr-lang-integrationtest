package io.corrlang.scenarios.patients2pregnancies;

import io.corrlang.SystemTest;
import org.junit.Test;

public class KarlEriksApplication extends SystemTest {

    private static final String CORRSPEC = "karlErikDemo.corrlang";


    @Test
    public void testKarlErikDemo() throws Throwable {
        runGoal(CORRSPEC, "GQLFederation");

    }

    @Override
    protected String getWorkdir() {
        return "patients2pregnancies";
    }
}
