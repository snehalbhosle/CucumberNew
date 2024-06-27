package com.cucumber.project.steps;

import com.cucumber.project.data.ContextObject;
import com.cucumber.project.ui.MyStepPageObject;
import com.google.inject.Inject;
import io.cucumber.guice.ScenarioScoped;
import io.cucumber.java.en.Given;

import java.sql.SQLException;

@ScenarioScoped
public class MyStepdefs {
    private ContextObject contextObject;
    private MyStepPageObject myStepPageObject;

    @Inject
    public MyStepdefs(ContextObject contextObject, MyStepPageObject myStepPageObject) {
        this.contextObject = contextObject;
        this.myStepPageObject = myStepPageObject;
    }

    @Given("I am on the Google search page")
    public void iAmOnTheGoogleSearchPage() throws SQLException {
        this.myStepPageObject.iAmOnTheGoogleSearchPage();
    }
}
