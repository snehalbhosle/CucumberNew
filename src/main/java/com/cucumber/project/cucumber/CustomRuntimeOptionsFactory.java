package com.cucumber.project.cucumber;


import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.io.MultiLoader;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.junit.CucumberOptions;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * This class is the RuntimeOptionsFactory copied and pasted with one additional function: addGlueFromPackages() which
 * searches all packages in the classpath.
 */
public class CustomRuntimeOptionsFactory {
    private final Class clazz;
    private boolean featuresSpecified = false;
    private boolean glueSpecified = true;
    private boolean pluginSpecified = false;
    private RunnerManager runnerManager;


    public CustomRuntimeOptionsFactory(Class clazz) {
        this.clazz = clazz;
        this.runnerManager = new RunnerManager(clazz);
    }

    // Added this function to search for packages to add to the glue
    public List<String> addGlueFromPackages(List<String> args) {
        try{
            for(Package p : Package.getPackages()) {
                if (isCucumberPackage(p)){
                    String name = "classpath:" + p.getName().replace(".","/");
                    if(!args.contains(name) && (!args.contains(p.getName()))){
                        args.add("--glue");
                        args.add(p.getName());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return args;
    }

    private boolean isCucumberPackage(Package p) throws IOException, ClassNotFoundException {
        Class[] classes = getClasses(p.getName());
        for(Class c : classes){
            Method[] methods =  c.getMethods();
            for (Method method : methods)
            {
                Annotation[] annotations = method.getAnnotations();
                if (method.isAnnotationPresent(Given.class) || method.isAnnotationPresent(When.class) || method.isAnnotationPresent(Then.class)
                        || method.isAnnotationPresent(Before.class) || method.isAnnotationPresent(After.class))
                {
                   return true;
                }
            }
        }
        return false;
    }

    private Class[] getClasses(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    private List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }


    public RuntimeOptions create(CucumberOptions options) throws IOException {
        List<String> args = buildArgsFromOptions(options);
        return new RuntimeOptions(args);
    }

    public RuntimeOptions create() throws IOException {
        List<String> args = buildArgsFromOptions();
        return new RuntimeOptions(args);
    }

    private List<String> buildArgsFromOptions(CucumberOptions options) throws IOException {
        List<String> args = new ArrayList<String>();
        if (options != null) {
            addDryRun(options, args);
            addMonochrome(options, args);
            addTags(options, args);
            addPlugins(options, args);
//            addStrict(options, args);
            addName(options, args);
            addSnippets(options, args);
            addGlue(options, args);
            addFeatures(options, args);
        }
        addDefaultFeaturePathIfNoFeaturePathIsSpecified(args, clazz);
        addDefaultGlueIfNoGlueIsSpecified(args, clazz);
        addNullFormatIfNoPluginIsSpecified(args);
        return args;
    }

    private List<String> buildArgsFromOptions() throws IOException {
        List<String> args = new ArrayList<String>();

        for (Class classWithOptions = clazz; hasSuperClass(classWithOptions); classWithOptions = classWithOptions.getSuperclass()) {
            CucumberOptions options = getOptions(classWithOptions);
            if (options != null) {
                addDryRun(options, args);
                addMonochrome(options, args);
                addTags(options, args);
                addPlugins(options, args);
//                addStrict(options, args);
                addName(options, args);
                addSnippets(options, args);
                addGlue(options, args);
                addFeatures(options, args);
//                addJunitOptions(options, args);
            }
        }
        addDefaultFeaturePathIfNoFeaturePathIsSpecified(args, clazz);
        addDefaultGlueIfNoGlueIsSpecified(args, clazz);
        addNullFormatIfNoPluginIsSpecified(args);

        //System.out.println(args);
        return args;
    }

    private void addName(CucumberOptions options, List<String> args) {
        for (String name : options.name()) {
            args.add("--name");
            args.add(name);
        }
    }

    private void addSnippets(CucumberOptions options, List<String> args) {
        args.add("--snippets");
        args.add(options.snippets().toString());
    }

    private void addDryRun(CucumberOptions options, List<String> args) {
        if (options.dryRun()) {
            args.add("--dry-run");
        }
    }

    private void addMonochrome(CucumberOptions options, List<String> args) {
        if (options.monochrome() || runningInEnvironmentWithoutAnsiSupport()) {
            args.add("--monochrome");
        }
    }

    private void addTags(CucumberOptions options, List<String> args) {
        for (String tags : options.tags().split(",")) {
            args.add("--tags");
            args.add(tags);
        }
    }

    private void addPlugins(CucumberOptions options, List<String> args) throws IOException {
        for (String plugin : options.plugin()) {
            args.add("--plugin");
            args.add(plugin);
            pluginSpecified = true;
        }
    }

    private void addNullFormatIfNoPluginIsSpecified(List<String> args) {
        if (!pluginSpecified) {
            args.add("--plugin");
            args.add("null");
        }
    }

    private void addFeatures(CucumberOptions options, List<String> args) {
        if (options != null && options.features().length != 0) {
            Collections.addAll(args, options.features());
            featuresSpecified = true;
        }
    }

    private void addDefaultFeaturePathIfNoFeaturePathIsSpecified(List<String> args, Class clazz) {
        if (!featuresSpecified) {
            args.add(MultiLoader.CLASSPATH_SCHEME + packagePath(clazz));
        }
    }

    private void addGlue(CucumberOptions options, List<String> args) {
        for (String glue : options.glue()) {
            args.add("--glue");
            args.add(glue);
            glueSpecified = true;
        }
        args = addGlueFromPackages(args);
    }

    private void addDefaultGlueIfNoGlueIsSpecified(List<String> args, Class clazz) {
        if (!glueSpecified) {
            args.add("--glue");
            args.add(MultiLoader.CLASSPATH_SCHEME + packagePath(clazz));
        }
    }


//    private void addStrict(CucumberOptions options, List<String> args) {
//        if (options.strict()) {
//            args.add("--strict");
//        }
//    }

//    private void addJunitOptions(CucumberOptions options, List<String> args) {
//        for (String junitOption : options.junit()) {
//            args.add("--junit," + junitOption);
//        }
//    }

    static String packagePath(Class clazz) {
        return packagePath(packageName(clazz.getName()));
    }

    static String packagePath(String packageName) {
        return packageName.replace('.', '/');
    }

    static String packageName(String className) {
        return className.substring(0, Math.max(0, className.lastIndexOf(".")));
    }

    private boolean runningInEnvironmentWithoutAnsiSupport() {
        boolean intelliJidea = System.getProperty("idea.launcher.bin.path") != null;
        // TODO: What does Eclipse use?
        return intelliJidea;
    }

    private boolean hasSuperClass(Class classWithOptions) {
        return classWithOptions != Object.class;
    }

    private CucumberOptions getOptions(Class<?> clazz) {
        return clazz.getAnnotation(CucumberOptions.class);
    }
}
