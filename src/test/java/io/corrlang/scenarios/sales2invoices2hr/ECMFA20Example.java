package io.corrlang.scenarios.sales2invoices2hr;

import io.corrlang.SystemTest;
import io.corrlang.plugins.PlottingTechSpace;
import org.junit.Test;

import java.util.Properties;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

/**
 * Tests the functionality on the old scenario used in the ECMFA20 paper
 * comprising SALES, INVOICES and EMPLOYEES
 */
public class ECMFA20Example extends SystemTest {


    @Override
    protected String getWorkdir() {
        return "sales2invoices2hr";
    }

    /**
     * Step 1: No element commonalities defined, goal is printing an overview picture
     */
    @Test
    public void testStep1() throws Throwable {
        runGoal("step1.corr", "Draw");
        assertTrue(getReportFacade().getErrors().isEmpty());
        assertBinaryFileContentAsExpected("vizz1.png", "vizz.png"); // TODO it would be better to somehow fetch the content of the Plotter and compare that!!
    }


    @Test
    public void testStep2() throws Throwable {
        Properties properties = new Properties();
        properties.put(PlottingTechSpace.PLOTTING_CONFIG_ARG_DRAW_SERVICES, "false");
        runGoal("step1.corr", "Draw", false,  properties);
        assertTrue(getReportFacade().getErrors().isEmpty());
        assertBinaryFileContentAsExpected("vizz2.png", "vizz.png");
    }

    @Test
    public void testStep3() throws Throwable {
        runGoal("step3.corr", "Schema");
        assertTrue(getReportFacade().getErrors().isEmpty());
        assertFalse(getReportFacade().getInfos().isEmpty());
        assertTextFileContentAsExpected("schema1.graphql", "schema.graphql");
    }


    @Test
    public void testStep4() throws Throwable {
        runGoal("step4.corr", "Federation", true, new Properties());
        waitForServerStartup();
        String request = "{\n" +
                "  \"query\" : \"query {\\n  clients {\\n   name\\n  }\\n}\"" +
                "} ";
        testHTTPRequest("http://127.0.0.1:8081/graphql/", request, 200, "{\"data\":{\"clients\":[{\"name\":\"Ban Geyton\"},{\"name\":\"Ferrell Leethem\"}]}}");
        String request2 = "{\n" +
                "  \"query\" : \"query {\\n  customers {\\n   name\\n  }\\n}\"" +
                "} ";
        testHTTPRequest("http://127.0.0.1:8081/graphql/", request2, 200, "{\"data\":{\"customers\":[{\"name\":\"Ban Geyton\"},{\"name\":\"Ferrell Leethem\"},{\"name\":\"Selinda Streader\"}]}}");
        String request3 = "{\n" +
                "  \"query\" : \"query {\\n  employees { firstname lastname worksAt { name } } }\"" +
                "} ";
        testHTTPRequest("http://127.0.0.1:8081/graphql/", request3, 200, "{\"data\":{\"employees\":[{\"firstname\":\"Tabbi\",\"lastname\":\"Witt\",\"name\"{\"name\":\"Sales\"}},{\"firstname\":\"Venus\",\"lastname\":\"Ferenczy\",\"name\"{\"name\":\"Sales\"}},{\"firstname\":\"Gregorio\",\"lastname\":\"Phythian\",\"name\"{\"name\":\"IT\"}},{\"firstname\":\"Beverlie\",\"lastname\":\"Pim\",\"name\"{\"name\":\"IT\"}},{\"firstname\":\"Ferrell\",\"lastname\":\"Leethem\",\"name\"{\"name\":\"HR\"}}]}}");

        stopServer();
    }

    @Test
    public void testStep5() throws Throwable {
        runGoal("step5.corr", "Schema");
        assertTrue(getReportFacade().getErrors().isEmpty());
        assertTextFileContentAsExpected("schema2.graphql", "schema.graphql");
    }

    @Test
    public void testStep6() throws Throwable {
        runGoal("step6.corr", "Schema");
        assertTrue(getReportFacade().getErrors().isEmpty());
        assertTextFileContentAsExpected("schema2.graphql", "schema.graphql");
    }

    @Test
    public void testStep7() throws Throwable {
        runGoal("step7.corr", "Fed", true, new Properties());
        waitForServerStartup();
        String request = "{\n" +
                "  \"query\" : \"query { partners { id name firstname lastname purchases { id } invoices { id } worksAt { name } } }\"" +
                "} ";
        testHTTPRequest("http://127.0.0.1:8081/graphql/", request, 200, "{\"data\":{\"partners\":[{\"id\":\"1\",\"name\":\"Ban Geyton\",\"firstname\":null,\"lastname\":null,\"purchases\":[{\"id\":\"1\"},{\"id\":\"2\"}],\"invoices\":[],\"worksAt\":null},{\"id\":\"2\",\"name\":\"Ferrell Leethem\",\"firstname\":null,\"lastname\":null,\"purchases\":[{\"id\":\"3\"},{\"id\":\"4\"},{\"id\":\"5\"}],\"invoices\":[],\"worksAt\":null},{\"id\":\"3\",\"name\":\"Selinda Streader\",\"firstname\":null,\"lastname\":null,\"purchases\":[{\"id\":\"6\"}],\"invoices\":[],\"worksAt\":null},{\"id\":\"1\",\"name\":\"Ban Geyton\",\"firstname\":null,\"lastname\":null,\"purchases\":[],\"invoices\":[{\"id\":\"1\"},{\"id\":\"2\"}],\"worksAt\":null},{\"id\":\"2\",\"name\":\"Ferrell Leethem\",\"firstname\":null,\"lastname\":null,\"purchases\":[],\"invoices\":[{\"id\":\"3\"}],\"worksAt\":null},{\"id\":\"1\",\"name\":null,\"firstname\":\"Tabbi\",\"lastname\":\"Witt\",\"purchases\":[],\"invoices\":[],\"worksAt\":{\"name\":\"Sales\"}},{\"id\":\"2\",\"name\":null,\"firstname\":\"Venus\",\"lastname\":\"Ferenczy\",\"purchases\":[],\"invoices\":[],\"worksAt\":{\"name\":\"Sales\"}},{\"id\":\"3\",\"name\":null,\"firstname\":\"Gregorio\",\"lastname\":\"Phythian\",\"purchases\":[],\"invoices\":[],\"worksAt\":{\"name\":\"IT\"}},{\"id\":\"4\",\"name\":null,\"firstname\":\"Beverlie\",\"lastname\":\"Pim\",\"purchases\":[],\"invoices\":[],\"worksAt\":{\"name\":\"IT\"}},{\"id\":\"5\",\"name\":null,\"firstname\":\"Ferrell\",\"lastname\":\"Leethem\",\"purchases\":[],\"invoices\":[],\"worksAt\":{\"name\":\"HR\"}}]}}");
        stopServer();
    }

    @Test
    public void testStep8() throws Throwable {
        runGoal("step8.corr", "Fed", true, new Properties());
        waitForServerStartup();
        String request = "{\n" +
                "  \"query\" : \"query { partners { id name firstname lastname purchases { id } invoices { id } worksAt { name } } }\"" +
                "} ";
        testHTTPRequest("http://127.0.0.1:8081/graphql/", request, 200, "{\"data\":{\"partners\":[{\"id\":\"1\",\"name\":\"Ban Geyton\",\"firstname\":null,\"lastname\":null,\"purchases\":[{\"id\":\"1\"},{\"id\":\"2\"}],\"invoices\":[{\"id\":\"1\"},{\"id\":\"2\"}],\"worksAt\":null},{\"id\":\"2\",\"name\":\"Ferrell Leethem\",\"firstname\":null,\"lastname\":null,\"purchases\":[{\"id\":\"3\"},{\"id\":\"4\"},{\"id\":\"5\"}],\"invoices\":[{\"id\":\"3\"}],\"worksAt\":null},{\"id\":\"3\",\"name\":\"Selinda Streader\",\"firstname\":null,\"lastname\":null,\"purchases\":[{\"id\":\"6\"}],\"invoices\":[],\"worksAt\":null},{\"id\":\"1\",\"name\":null,\"firstname\":\"Tabbi\",\"lastname\":\"Witt\",\"purchases\":[],\"invoices\":[],\"worksAt\":{\"name\":\"Sales\"}},{\"id\":\"2\",\"name\":null,\"firstname\":\"Venus\",\"lastname\":\"Ferenczy\",\"purchases\":[],\"invoices\":[],\"worksAt\":{\"name\":\"Sales\"}},{\"id\":\"3\",\"name\":null,\"firstname\":\"Gregorio\",\"lastname\":\"Phythian\",\"purchases\":[],\"invoices\":[],\"worksAt\":{\"name\":\"IT\"}},{\"id\":\"4\",\"name\":null,\"firstname\":\"Beverlie\",\"lastname\":\"Pim\",\"purchases\":[],\"invoices\":[],\"worksAt\":{\"name\":\"IT\"}},{\"id\":\"5\",\"name\":null,\"firstname\":\"Ferrell\",\"lastname\":\"Leethem\",\"purchases\":[],\"invoices\":[],\"worksAt\":{\"name\":\"HR\"}}]}}");
        stopServer();
    }

    @Test
    public void testStep9() throws Throwable {
        runGoal("step9.corr", "Fed", true, new Properties());
        waitForServerStartup();
        String request = "{\n" +
                "  \"query\" : \"query { partners { id name firstname lastname purchases { id } invoices { id } worksAt { name } } }\"" +
                "} ";
        testHTTPRequest("http://127.0.0.1:8081/graphql/", request, 200, "{\"data\":{\"partners\":[{\"id\":\"1\",\"name\":\"Ban Geyton\",\"firstname\":null,\"lastname\":null,\"purchases\":[{\"id\":\"1\"},{\"id\":\"2\"}],\"invoices\":[{\"id\":\"1\"},{\"id\":\"2\"}],\"worksAt\":null},{\"id\":\"5\",\"name\":\"Ferrell Leethem\",\"firstname\":\"Ferrell\",\"lastname\":\"Leethem\",\"purchases\":[{\"id\":\"3\"},{\"id\":\"4\"},{\"id\":\"5\"}],\"invoices\":[{\"id\":\"3\"}],\"worksAt\":{\"name\":\"HR\"}},{\"id\":\"3\",\"name\":\"Selinda Streader\",\"firstname\":null,\"lastname\":null,\"purchases\":[{\"id\":\"6\"}],\"invoices\":[],\"worksAt\":null},{\"id\":\"1\",\"name\":null,\"firstname\":\"Tabbi\",\"lastname\":\"Witt\",\"purchases\":[],\"invoices\":[],\"worksAt\":{\"name\":\"Sales\"}},{\"id\":\"2\",\"name\":null,\"firstname\":\"Venus\",\"lastname\":\"Ferenczy\",\"purchases\":[],\"invoices\":[],\"worksAt\":{\"name\":\"Sales\"}},{\"id\":\"3\",\"name\":null,\"firstname\":\"Gregorio\",\"lastname\":\"Phythian\",\"purchases\":[],\"invoices\":[],\"worksAt\":{\"name\":\"IT\"}},{\"id\":\"4\",\"name\":null,\"firstname\":\"Beverlie\",\"lastname\":\"Pim\",\"purchases\":[],\"invoices\":[],\"worksAt\":{\"name\":\"IT\"}}]}}");
        stopServer();
    }

}
