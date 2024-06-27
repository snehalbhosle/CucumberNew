package com.cucumber.project.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * Created by NDHODDA on 12/17/2016.
 */
public class ConfigReader {

    private String baseUri;
    private String username;
    private String password;
    private String rn_username;
    private String rn_password;
    private String facility;
    private String authToken;
    private Properties props = null;
    private final HashMap<String, String> config = new HashMap();

    /**
     * Instantiate ConfigReader object with default props and load all properties into an HashMap
     */
    public ConfigReader() {
        loadPropertiesToMap();
        getSystemUserName();
        getSystemPassWord();
        getSystemRNUserName();
        getSystemRNPassWord();
        getSystemFacilityId();
        if (props != null) {
            this.baseUri = props.getProperty("baseUri");
            this.authToken = props.getProperty("authToken");
            if (username == null || username.isEmpty())
                this.username = props.getProperty("username");
            if (password == null || password.isEmpty())
                this.password = props.getProperty("password");
            if (rn_username == null || rn_username.isEmpty())
                this.rn_username = props.getProperty("rn_username");
            if (rn_password == null || rn_password.isEmpty())
                this.rn_password = props.getProperty("rn_password");
            if (facility == null || facility.isEmpty())
                this.facility = props.getProperty("facilityID");
        }
    }

    /**
     * Passing Property file name from System property and loading all properties into hashmap
     */
    public void loadPropertiesToMap() {
        String file = System.getProperty("beacon.config");
        loadProperties(file);
    }

    /**
     * Getting UserName from System property
     */
    public void getSystemUserName() {
        username = System.getProperty("username");
    }

    /**
     * Getting PassWord from System property
     */
    public void getSystemPassWord() {
        password = System.getProperty("password");
    }

    /**
     * Getting RN UserName from System property
     */
    public void getSystemRNUserName() {
        rn_username = System.getProperty("rn_username");
    }

    /**
     * Getting RN PassWord from System property
     */
    public void getSystemRNPassWord() {
        rn_password = System.getProperty("rn_password");
    }

    /**
     * Getting Facility Id from System property
     */
    public void getSystemFacilityId() {
        facility = System.getProperty("facilityID");
     }

    /**
     * Passing Property file name from System property and loading all properties into hashmap
     *
     * @param file the file path for the properties file.
     */
    public void loadProperties(String file) {
        if (file == null || file.isEmpty()) {
            file = "config.properties";
        }
        Properties props = readPropertiesFile(file);
        if (props != null) {
            for (Map.Entry<Object, Object> x : props.entrySet()) {
                config.put((String) x.getKey(), (String) x.getValue());
            }
        }
    }

    /**
     * Gets the base uri from the property file.
     *
     * @return the base uri from the property file.
     */
    public String getBaseUri() {
        return baseUri;
    }

    /**
     * Sets the base uri after the properties are loaded from the file. Note this will not change the actual property
     * file.
     *
     * @param baseUri the base uri to set for the properties.
     */
    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }

    /**
     * Gets the username from the property file.
     *
     * @return the username from the property file.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username after the properties are loaded from the file. Note this will not change the actual property
     * file.
     *
     * @param username the username to set for the properties.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password from the properties.
     *
     * @return the password in the properties.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password after the properties are loaded from the file. Note this will not change the actual property
     * file.
     *
     * @param password the password to set for the properties.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the auth token from the properties.
     *
     * @return the auth token.
     */
    public String getAuthToken() {
        return authToken;
    }

    /**
     * Sets the auth token after the properties are loaded from the file. Note this will not change the actual property
     * file.
     *
     * @param authToken the auth token to set for the properties.
     */
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }


    /**
     * Gets the config reader in the form of a string.
     *
     * @return the config reader properties contained in a string.
     */
    @Override
    public String toString() {
        return "ConfigReader{" +
                "baseUri='" + baseUri + '\'' +
                ", facility='" + facility + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", rn_username='" + rn_username + '\'' +
                ", rn_password='" + rn_password + '\'' +
                ", authToken='" + authToken + '\'' +
                '}';
    }

    /**
     * Reads the properties from a file and returns the properties associated.
     *
     * @param fileName the file name for the file to be read.
     * @return the properties that were loaded from the file provided.
     */
    public Properties readPropertiesFile(String fileName) {
        try (FileInputStream is = new FileInputStream(
                new File(System.getProperty("user.dir") + File.separator + fileName))) {
            if (is != null) {
                props = new Properties();
                props.load(is);
            }

        } catch (IOException ex) {
            System.out.println("Oops... File is missing, Please check it: " + System.getProperty("user.dir") + File.separator + fileName);
            ex.printStackTrace();
        }
        return props;
    }

    /**
     * Get the value in the ConfigReader object that is associated with the provided key.
     *
     * @param key the key to the value that wants to be retrieved.
     * @return the value associated with this key.
     */
    public String getProperty(String key) {
        String value;
        try {
            value = config.get(key);
        } catch (Exception exc) {
            exc.printStackTrace();
            throw new RuntimeException("Request to the ConfigReader object was not successful.");
        }
        return value;
    }

    /**
     * Get the value in the ConfigReader object that is associated with the provided key or the default.
     *
     * @param key          the key to the value that wants to be retrieved.
     * @param defaultValue the default value that will be set if no value in the property file.
     * @return the value associated with this key.
     */
    public String getProperty(String key, String defaultValue) {
        String value;
        try {
            value = config.get(key);
        } catch (Exception exc) {
            exc.printStackTrace();
            throw new RuntimeException("Request to the ConfigReader object was not successful.");
        }

        return value;
    }

    /**
     * Set Property value in hashmap with given key.
     *
     * @param key   the key that will be used later to retrieve the value provided.
     * @param value the value that will associated with the key provided.
     */
    public void setProperty(String key, String value) {
        config.put(key, value);
    }

}
