package com.cucumber.project.common;


import com.cucumber.project.data.ContextObject;

/**
 * Contains any utility functions that will be useful for the users of the framework.
 * <p>
 */
public class Utility {



    /**
     * @param configProperty - Property which is to be queried from the local config file
     * @return - A string that represents the configuration property.
     */
    public static String getLocalConfigProperty(String configProperty) {
        return new ContextObject<>().getConfig().getProperty(configProperty);
    }


}
