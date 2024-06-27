package com.cucumber.project.base.ui.base;


import java.sql.Driver;

/**
 * Created by rkhanna on 1/30/17.
 */
public class BasePageObject {
    private Driver driver;

    /**
     * Construct Base Page Object with driver instance
     *
     * @param driver
     */
    protected BasePageObject(Driver driver) {
        this.driver = driver;
    }

    /**
     * Get instance of driver
     *
     * @return Driver
     */
    protected Driver getDriver() {
        return this.driver;
    }

    /**
     * enum for different wait options namely VISIBLE,PRESENT,CLICKABLE and NOTREQUIRED
     */

}
