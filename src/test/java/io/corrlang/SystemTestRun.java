package io.corrlang;

import io.corrlang.engine.parser.SyntacticalResult;
import io.corrlang.engine.reporting.ReportFacade;
import io.corrlang.engine.runner.AbstractRun;
import no.hvl.past.di.DependencyInjectionContainer;
import no.hvl.past.di.PropertyHolder;
import io.corrlang.plugins.ServerStarter;
import no.hvl.past.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Semaphore;

public class SystemTestRun extends AbstractRun {

    private final Semaphore serverStartupWait = new Semaphore(0);

    public SystemTestRun(ReportFacade reportFacade, Properties properties, List<String> corrSpecs) {
        super(reportFacade, properties, corrSpecs);
    }

    public static class TestReportFacade implements ReportFacade {

        private final StringBuilder infos = new StringBuilder();
        private final StringBuilder errors = new StringBuilder();
        private final List<Throwable> exceptions = new ArrayList<>();

        public String getInfos() {
            return infos.toString();
        }

        public String getErrors() {
            return errors.toString();
        }

        public List<Throwable> getExceptions() {
            return exceptions;
        }

        @Override
        public void reportError(String message) {
            System.err.println(message);
            errors.append(message);
        }

        @Override
        public void reportInfo(String message) {
            System.out.println(message);
            infos.append(message);
        }

        @Override
        public void reportException(Throwable cause) {
            cause.printStackTrace(System.err);
            errors.append(cause.getMessage());
            exceptions.add(cause);
        }

        @Override
        public void reportSyntaxError(String file, int line, int column, String message) {
            System.err.println("Syntax error: " + message + " as " + line + ":" + column);
            this.errors.append(file).append(";").append(line).append(";").append(column).append(";").append(message);
        }

    }


    public static Pair<SystemTestRun, TestReportFacade> create(File baseDir, File config, File corrSpec, Properties properties) {
        properties.setProperty(PropertyHolder.BASE_DIR, baseDir.getAbsolutePath());
        properties.setProperty(DependencyInjectionContainer.USE_CONFIG_COMMAND, config.getAbsolutePath());
        TestReportFacade testReportFacade = new TestReportFacade();
        SystemTestRun testRun = new SystemTestRun(testReportFacade, properties, Collections.singletonList(corrSpec.getAbsolutePath()));
        return new Pair<>(testRun, testReportFacade);
    }

    public void testRun(String goal, boolean silent, boolean async) throws Throwable {
        initialise(silent);
        Logger logger = LogManager.getLogger(SystemTest.class);
        // retrieving the current test name
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];

        logger.info("Running system test '"  +
                stackTraceElement.getClassName() + "::" + stackTraceElement.getMethodName() +
                "' on corrspec file '" +
                getCorrSpecs().get(0) + "' and goal '" +  goal +"'");

        ServerStarter serverStarter = getDiContainer().getBean(ServerStarter.class);
        serverStarter.setStartedHandler(() -> {
            logger.info("Server started!!!");
            serverStartupWait.release();
        });
        serverStarter.setStoppedHandler(() -> {
            logger.info("Server stopped");
        });

        if (async) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        executeGoal(goal, Void.class);
                    } catch (Throwable e) {
                        // TODO signal fail
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            executeGoal(goal, Void.class);
        }
    }

    public void stopServer() {
        ServerStarter serverStarter = getDiContainer().getBean(ServerStarter.class);
        serverStarter.stopServer();
    }

    Semaphore getServerStartupWait() {
        return serverStartupWait;
    }

    public SyntacticalResult getParseResult() {
        return getSyntacticalResult();
    }
}
