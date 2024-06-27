package com.cucumber.project.cucumber;


import com.cucumber.project.data.ContextObject;
import com.cucumber.project.injection.DependencyModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import io.cucumber.guice.ScenarioScoped;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;


/**
 * Created by csears on 1/30/17.
 * <p>
 * This file will hold all the hooks for the beacon framework and anyone using this framework.
 * Caution because any generic @Before or @After hook will run for everyone utilizing the framework.
 */
@ScenarioScoped
public class GenericHooks {

    private ContextObject contextObject;
    private WebDriver driver;
    @Inject
    public GenericHooks(ContextObject contextObject) {
        this.contextObject = contextObject;
    }


    /**
     * This hook runs before and after each scenario in the Cucumber run.
     *
     * @param scenario The Cucumber scenario that is currently being run.
     */
    //Lowering the priority of execution to 1, in order for it to run first
    @Before(order = 1)
    public void addScenario(Scenario scenario) {
        contextObject.set(Scenario.class.toString(), scenario);
    }

    @After(value = "@Web or @web or @Device or @device")
    public void afterUIScenario(Scenario scenario) {
        Injector injector = Guice.createInjector(new DependencyModule());
        driver = injector.getInstance(Key.get(WebDriver.class));
          if (scenario.isFailed()) {
                try {
                 scenario.attach(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES), "image/png", scenario.getName());
                } catch (Exception e) {
                 e.printStackTrace();
                }
          }
          driver.quit();
    }

}
