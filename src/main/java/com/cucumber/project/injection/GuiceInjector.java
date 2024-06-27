package com.cucumber.project.injection;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import io.cucumber.guice.CucumberModules;
import io.cucumber.guice.InjectorSource;


/**
 * Guice Injector that creates a specific injector that details at what time
 * in the runtime process to inject, the default scope for classes, and the
 * custom dependency module the injections are based on.
 */
public class GuiceInjector implements InjectorSource {
    /**
     * Guice Injector that specifies the Guice module to use when
     * injecting variables and what scope to use as a default (Scenario Scoped).
     * NOTE: This class will be called at Runtime and found by Guice through the
     * "guice.injector-source" system variable
     */
    @Override
    public Injector getInjector() {
        return Guice.createInjector(Stage.PRODUCTION, CucumberModules.createScenarioModule(), new DependencyModule());
    }
}
