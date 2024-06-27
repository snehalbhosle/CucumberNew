package com.cucumber.project.injection;

import com.cucumber.project.data.ContextObject;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.name.Names;
import org.openqa.selenium.WebDriver;

import static io.cucumber.guice.CucumberScopes.createScenarioScope;

/**
 * Created by csears on 11/14/16.
 * <p>
 * Guice module that binds object instances to inject and classes to given scopes.
 */
public class DependencyModule implements Module {
    /**
     * Sets up the bindings for the various Step Definitions and instance variables.
     *
     * @param binder The Guice binder that will be used for injecting variables and scope.
     */
    public void configure(Binder binder) {
        binder.bind(ContextObject.class).in(createScenarioScope());
        binder.bind(WebDriver.class).toProvider(DriverProvider.class).in(createScenarioScope());
        binder.bind(String.class).annotatedWith(Names.named("Default Excel File Path")).toInstance("src/test/resources/");
    }
}
