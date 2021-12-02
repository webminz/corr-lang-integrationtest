package io.corrlang;

import com.google.common.io.Files;
import no.hvl.past.TestBase;
import no.hvl.past.util.IOStreamUtils;
import no.hvl.past.util.Pair;
import org.apache.logging.log4j.core.util.IOUtils;
import org.junit.Before;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Properties;

import static org.junit.Assert.*;

public abstract class SystemTest extends TestBase {

    protected File workdir;

    protected SystemTestRun testRunner;

    protected SystemTestRun.TestReportFacade reportFacade;

    protected abstract String getWorkdir();

    protected String defaultActualFilesFolder = "actualFiles";

    protected String defaultExpectedFilesFolder = "comparisonFiles";

    public SystemTestRun.TestReportFacade getReportFacade() {
        return reportFacade;
    }

    @Before
    public void setUp() {
        File resourceFolderItem = getResourceFolderItem(getWorkdir());
        if (!resourceFolderItem.exists()) {
            resourceFolderItem.mkdirs();
        }
        workdir = resourceFolderItem;
    }

    protected void runGoal(String corrSpecFileName, String goalName) throws Throwable {
        runGoal(corrSpecFileName, goalName, false, new Properties());
    }

    protected void runGoal(String corrSpecFileName, String goalName, boolean async, Properties properties) throws Throwable {
        File file = new File(workdir, corrSpecFileName);
        Pair<SystemTestRun, SystemTestRun.TestReportFacade> r = SystemTestRun.create(workdir, getResourceFolderItem("corrlang.conf"), file, properties);
        testRunner = r.getFirst();
        reportFacade = r.getSecond();
        testRunner.testRun(goalName, true, async);
    }



    public void testHTTPRequest(String url, String message, int expectedResponseCode, String expectedContent) throws IOException {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.connect();
            IOStreamUtils.copyOver(IOStreamUtils.stringAsInputStream(message), connection.getOutputStream());
            connection.getOutputStream().close();
            int responseCode = connection.getResponseCode();
            assertEquals(expectedResponseCode, responseCode);

            if ((responseCode / 100) == 2) {
                String s = IOStreamUtils.readInputStreamAsString(connection.getInputStream());
                assertEquals(expectedContent, s);
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public void assertBinaryFileContentAsExpected(String expectedFileName, String actualFileName) {
        File folder = new File(workdir, defaultActualFilesFolder);
        File actual = new File(folder, actualFileName);
        assertTrue("Expected file does not exist", actual.exists());
        File expectedFolder = new File(workdir, defaultExpectedFilesFolder);
        File expected = new File(expectedFolder, expectedFileName);
        try {
            assertTrue("Expected file content does not match",Files.equal(actual, expected));
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void assertTextFileContentAsExpected(String expectedFileName, String actualFileName) {
        File folder = new File(workdir, defaultActualFilesFolder);
        File actual = new File(folder, actualFileName);
        assertTrue("Expected file does not exist", actual.exists());
        File expectedFolder = new File(workdir, defaultExpectedFilesFolder);
        File expected = new File(expectedFolder, expectedFileName);
        try (FileInputStream afis = new FileInputStream(actual);
             FileInputStream efils =  new FileInputStream(expected)) {
            assertEquals("File content mismatch: ", IOStreamUtils.readInputStreamAsString(efils), IOStreamUtils.readInputStreamAsString(afis));
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    protected void stopServer() {
        testRunner.stopServer();
    }

    protected void waitForServerStartup() throws InterruptedException {
        testRunner.getServerStartupWait().acquire();
    }

    protected void setDefaultActualFilesFolder(String defaultActualFilesFolder) {
        this.defaultActualFilesFolder = defaultActualFilesFolder;
    }

    protected void setDefaultExpectedFilesFolder(String defaultExpectedFilesFolder) {
        this.defaultExpectedFilesFolder = defaultExpectedFilesFolder;
    }
}
