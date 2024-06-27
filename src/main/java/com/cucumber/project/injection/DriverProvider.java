package com.cucumber.project.injection;

import com.google.inject.Provider;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;


/**
 * Created by rkhanna on 1/17/17.
 */
public class DriverProvider implements Provider<WebDriver> {


    Object contextObject;

    @Override
    public WebDriver get() {

       return new ChromeDriver();
    }
}
