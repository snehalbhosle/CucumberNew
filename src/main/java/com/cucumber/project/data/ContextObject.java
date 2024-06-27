package com.cucumber.project.data;


import java.util.HashMap;

/**
 * Created by csears on 11/22/16.
 * <p>
 * The object that holds the variables instances that will be used across step definitions.
 */
public class ContextObject<E> {
    private final HashMap<String, E> context = new HashMap();

    public ContextObject() {
        set((E) new ConfigReader());
    }


    /**
     * Creates an object in the context object to be retrieved in the future.
     *
     * @param key   the key that will be used to locate this object.
     * @param value the value to be saved with this given key.
     */
    public void set(String key, E value) {
        context.put(key, value);
    }

    /**
     * Stores an object in the context object to be retrieved in the future.
     * Uses the classname as a key, meaning only 1 instance of each class may be stored
     *
     * @param value the value to be saved with this given key.
     */
    public void set(E value) {
        String name = value.getClass().toString();
        set(name, value);
    }


    /**
     * Get the value in the context object that is associated with the provided key.
     *
     * @param key the key to the value that wants to be retrieved.
     * @return the value associated with this key.
     */
    public E get(String key) {
        E value;

        try {
            value = (E) context.get(key);
        } catch (Exception exc) {
            exc.printStackTrace();
            throw new RuntimeException("Request to the context object was not successful.");
        }

        if (value == null) {
            throw new RuntimeException("No value set for the given key: " + key + " in the context object: " + this.toString() + " " + context.toString());
        }

        return (E) value;
    }


    /**
     * Get the item using the classname as a key
     *
     * @param klass the class to get aka LoginResponse.class
     * @return the value associated with this key.
     */
    public E get(Class klass) {
        String key = klass.toString();
        return (E) get(key);
    }

    /**
     * Remove and get the value associated with the given key in the context object.
     *
     * @param key the key to the value that wants to be removed.
     * @return void
     */
    public E remove(String key) {
        E value;

        try {
            value = context.remove(key);
        } catch (Exception exc) {
            exc.printStackTrace();
            throw new RuntimeException("Request to remove in the context object was not successful.");
        }

        if (value == null) {
            throw new RuntimeException("Requested key: " + key + " to be removed, not found in the data set");
        }

        return value;
    }

    /**
     * Removes all possible references to objects that were set in the context object.
     */
    public void tearDown() {
        //remove all set key value pairs in the context object
        context.entrySet().removeIf(e -> true);
    }

    public ConfigReader getConfig() {
        return (ConfigReader) this.get(ConfigReader.class);
    }



}
