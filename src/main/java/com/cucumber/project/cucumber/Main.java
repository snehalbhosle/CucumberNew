package com.cucumber.project.cucumber;


import io.cucumber.core.options.CommandlineOptionsParser;
import io.cucumber.core.options.CucumberProperties;
import io.cucumber.core.options.CucumberPropertiesParser;
import io.cucumber.core.options.RuntimeOptions;
import io.cucumber.core.runtime.Runtime;

import java.io.IOException;
import java.util.Optional;

/**
 * Starting point for the single feature file/scenario execution
 * For generating reports, add the following line in the Edit Configurations
 */
public class Main {

    private static boolean isFeatureFileExecution = false;

    public static void main(String[] argv) throws Throwable {
        byte exitstatus = run(argv, Thread.currentThread().getContextClassLoader());
        System.exit(exitstatus);
    }

    /**
     * Launches the Cucumber-JVM command line.
     *
     * @param argv        runtime options. See details in the {@code cucumber.api.cli.Usage.txt} resource.
     * @param classLoader classloader used to load the runtime
     * @return 0 if execution was successful, 1 if it was not (test failures)
     * @throws IOException if resources couldn't be loaded during the run.
     */
    public static byte run(String[] argv, ClassLoader classLoader) throws IOException {
        isFeatureFileExecution = true;

        RuntimeOptions propertiesFileOptions = new CucumberPropertiesParser()
                .parse(CucumberProperties.fromPropertiesFile())
                .build();

        RuntimeOptions environmentOptions = new CucumberPropertiesParser()
                .parse(CucumberProperties.fromEnvironment())
                .build(propertiesFileOptions);

        RuntimeOptions systemOptions = new CucumberPropertiesParser()
                .parse(CucumberProperties.fromSystemProperties())
                .build(environmentOptions);

        CommandlineOptionsParser commandlineOptionsParser = new CommandlineOptionsParser(System.out);
        RuntimeOptions runtimeOptions = commandlineOptionsParser
                .parse(argv)
                .addDefaultGlueIfAbsent()
                .addDefaultFeaturePathIfAbsent()
                .addDefaultSummaryPrinterIfNotDisabled()
                .enablePublishPlugin()
                .build(systemOptions);

        Optional<Byte> exitStatus = commandlineOptionsParser.exitStatus();
        if (exitStatus.isPresent()) {
            return exitStatus.get();
        }

        final Runtime runtime = Runtime.builder()
                .withRuntimeOptions(runtimeOptions)
                .withClassLoader(() -> classLoader)
                .build();

        runtime.run();


        return runtime.exitStatus();
    }

    static boolean isFeatureFileExecution() {
        return isFeatureFileExecution;
    }
}