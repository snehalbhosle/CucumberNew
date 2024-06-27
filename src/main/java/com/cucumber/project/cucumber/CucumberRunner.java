package com.cucumber.project.cucumber;

import com.github.mkolisnyk.cucumber.runner.ExtendedCucumberOptions;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * This hooks Cucumber into the JUnit Runner.
 * This class should be extended multiple times, once per simultaneous thread.
 * The runners must be named CucumberRunner1, CucumberRunner2, etc.
 * Each will execute an appropriate percentage of the total feature files.
 */
@RunWith(CustomCucumber.class)
@ExtendedCucumberOptions(
)
@CucumberOptions(
        plugin = {
                "json:reports/json/report.json",
                "html:reports/html",},
        features = {"src/test/resources/features"},
        glue = {"com.cucumber.project.steps"},
        tags = "~@ignore"
)

public abstract class CucumberRunner {
}

