package com.cucumber.project.ui;

import com.cucumber.project.base.ui.base.BasePageObject;
import com.cucumber.project.data.ContextObject;
import com.google.inject.Inject;

import java.sql.Driver;
import java.sql.SQLException;

public class MyStepPageObject extends BasePageObject {
    private ContextObject contextObject;

    @Inject
    public MyStepPageObject(Driver driver, ContextObject contextObject) {
        super(driver);
        this.contextObject = contextObject;
    }

    public void iAmOnTheGoogleSearchPage() throws SQLException {
        this.getDriver().acceptsURL("https://www.google.com");
    }
}
