package com.cucumber.project.cucumber;

import com.cucumber.project.common.Utility;
import com.github.mkolisnyk.cucumber.runner.ExtendedCucumberOptions;
import com.github.mkolisnyk.cucumber.runner.ExtendedFeatureRunner;
import com.github.mkolisnyk.cucumber.runner.ReportRunner;
import com.github.mkolisnyk.cucumber.runner.RetryAcceptance;
import com.github.mkolisnyk.cucumber.runner.runtime.ExtendedRuntimeOptions;
import cucumber.runtime.ClassFinder;
import cucumber.runtime.ExtendedRuntime;
import cucumber.runtime.Runtime;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import cucumber.runtime.junit.Assertions;
import cucumber.runtime.junit.JUnitOptions;
import cucumber.runtime.junit.JUnitReporter;
import cucumber.runtime.model.CucumberFeature;
import io.cucumber.junit.CucumberOptions;
import org.apache.commons.lang.ArrayUtils;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CustomCucumber extends ParentRunner<ExtendedFeatureRunner> {
    private JUnitReporter jUnitReporter;
    private final List<ExtendedFeatureRunner> children = new ArrayList<ExtendedFeatureRunner>();
    private final Runtime runtime;
    private static RunnerManager runnerManager;
    private List<CucumberFeature> parallelFilteredCucFeatures;
    private final ExtendedRuntimeOptions[] extendedOptions;
    private Class clazz;
    private int retryCount = 0;
    private int threadsCount = 1;
    private boolean runPreDefined = true;

    public CustomCucumber(Class clazz) throws Exception {
        super(clazz);
        try {
            this.clazz = clazz;
            runnerManager = new RunnerManager(clazz);
            ClassLoader classLoader = clazz.getClassLoader();
            Assertions.assertNoCucumberAnnotatedMethods(clazz);

            CustomRuntimeOptionsFactory runtimeOptionsFactory = new CustomRuntimeOptionsFactory(clazz);
            RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();

            ResourceLoader resourceLoader = new MultiLoader(classLoader);

            runtime = createRuntime(resourceLoader, classLoader, runtimeOptions);
            extendedOptions = ExtendedRuntimeOptions.init(clazz);
            init(runtimeOptions, classLoader, resourceLoader);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public CustomCucumber(
            Class clazz, CucumberOptions baseOptions,
            ExtendedCucumberOptions[] extendedOptionsValue, boolean runPreDefinedValue) throws Exception {
        super(clazz);
        this.clazz = clazz;
        this.runPreDefined = runPreDefinedValue;
        ClassLoader classLoader = clazz.getClassLoader();
        Assertions.assertNoCucumberAnnotatedMethods(clazz);
        CustomRuntimeOptionsFactory runtimeOptionsFactory = new CustomRuntimeOptionsFactory(clazz);
        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create(baseOptions);

        ResourceLoader resourceLoader = new MultiLoader(classLoader);

        runtime = createRuntime(resourceLoader, classLoader, runtimeOptions);
        extendedOptions = ExtendedRuntimeOptions.init(extendedOptionsValue);
        init(runtimeOptions, classLoader, resourceLoader);
    }

//    private void init(RuntimeOptions runtimeOptions,
//                      ClassLoader classLoader, ResourceLoader resourceLoader) throws Exception
//    {
//
//        for (ExtendedRuntimeOptions option : extendedOptions) {
//            retryCount = Math.max(retryCount, option.getRetryCount());
//            threadsCount = Math.max(threadsCount, option.getThreadsCount());
//        }
//
//        final JUnitOptions junitOptions = new JUnitOptions(runtimeOptions.getJunitOptions());
//        List<CucumberFeature> cucumberFeatures = runtimeOptions.cucumberFeatures(resourceLoader);
//        parallelFilteredCucFeatures = runnerManager.splitFeatures(cucumberFeatures);;
//        System.out.println("RunnerNumber"+runnerManager.getRunnerNumber());
//        jUnitReporter = new JUnitReporter(
//                runtimeOptions.reporter(classLoader),
//                runtimeOptions.formatter(classLoader),
//                runtimeOptions.isStrict(),
//                junitOptions);
//        Method[] retryMethods = this.getPredefinedMethods(RetryAcceptance.class);
//        addChildren(parallelFilteredCucFeatures, retryMethods);
//    }

    protected Runtime createRuntime(ResourceLoader resourceLoader, ClassLoader classLoader,
                                    RuntimeOptions runtimeOptions) throws InitializationError, IOException {
        ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);
        return new ExtendedRuntime(resourceLoader, classFinder, classLoader, runtimeOptions);
    }

    @Override
    public List<ExtendedFeatureRunner> getChildren() {
        return children;
    }

    @Override
    protected Description describeChild(ExtendedFeatureRunner child) {
        return child.getDescription();
    }

    @Override
    protected void runChild(ExtendedFeatureRunner child, RunNotifier notifier) {
        if (!parallelFilteredCucFeatures.isEmpty()) {
            child.run(notifier);
        }
    }


    @Override
    public void run(RunNotifier notifier) {
        super.run(notifier);
        if (!parallelFilteredCucFeatures.isEmpty()) {
            System.out.println("The feature is completed on runner " + clazz.getName());
            jUnitReporter.done();
            jUnitReporter.close();
            runtime.printSummary();
            for (ExtendedRuntimeOptions extendedOption : extendedOptions) {
                ReportRunner.run(extendedOption);
            }
        }
    }


    private void init(RuntimeOptions runtimeOptions,
                      ClassLoader classLoader, ResourceLoader resourceLoader) throws Exception {
        try {
            for (ExtendedRuntimeOptions option : extendedOptions) {
                retryCount = Math.max(retryCount, Integer.parseInt(Utility.getLocalConfigProperty("retryCount")));
                threadsCount = Math.max(threadsCount, option.getThreadsCount());
            }
        } catch (Exception e) {
            for (ExtendedRuntimeOptions option : extendedOptions) {
                retryCount = Math.max(retryCount, retryCount);
                threadsCount = Math.max(threadsCount, option.getThreadsCount());
            }
        }

        final JUnitOptions junitOptions = new JUnitOptions(runtimeOptions.getJunitOptions());
        final List<CucumberFeature> cucumberFeatures = runtimeOptions.cucumberFeatures(resourceLoader);

        //cucumber features sorted by tags at this point (full list of runnable feature files passed in)
        parallelFilteredCucFeatures = runnerManager.splitFeatures(cucumberFeatures);

        jUnitReporter = new JUnitReporter(runtimeOptions.reporter(classLoader), runtimeOptions.formatter(classLoader), runtimeOptions.isStrict(), junitOptions);
        Method[] retryMethods = this.getPredefinedMethods(RetryAcceptance.class);
        addChildren(parallelFilteredCucFeatures, retryMethods);
    }

    private Method[] getPredefinedMethods(Class annotation) {
        if (!annotation.isAnnotation()) {
            return new Method[]{};
        }
        Method[] filteredMethodList = new Method[]{};
        Method[] methodList = this.clazz.getMethods();
        for (Method method : methodList) {
            Annotation[] annotations = method.getAnnotations();
            for (Annotation item : annotations) {
                if (item.annotationType().equals(annotation)) {
                    filteredMethodList = (Method[]) ArrayUtils.add(filteredMethodList, method);
                }
            }
        }
        return filteredMethodList;
    }

    private void runPredefinedMethods(Class annotation) throws Exception {
        if (!annotation.isAnnotation()) {
            return;
        }
        Method[] methodList = getPredefinedMethods(annotation);
        for (Method method : methodList) {
            method.invoke(null);
        }
    }

//    @Override
//    public void run(RunNotifier notifier) {
//        try {
//            if (this.runPreDefined) {
//                runPredefinedMethods(BeforeSuite.class);
//            }
//            runPredefinedMethods(BeforeSubSuite.class);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        super.run(notifier);
//        try {
//            if (this.runPreDefined) {
//                runPredefinedMethods(AfterSuite.class);
//            }
//            runPredefinedMethods(AfterSubSuite.class);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        runtime.printSummary();
//        jUnitReporter.done();
//        jUnitReporter.close();
//        for (ExtendedRuntimeOptions extendedOption : extendedOptions) {
//            ReportRunner.run(extendedOption);
//        }
//    }

    private void addChildren(List<CucumberFeature> cucumberFeatures, Method[] retryMethods) throws InitializationError {
        for (CucumberFeature cucumberFeature : cucumberFeatures) {
            children.add(
                    new ExtendedFeatureRunner(cucumberFeature, runtime,
                            jUnitReporter, this.retryCount, retryMethods));
        }
    }

    public static boolean isRetryApplicable(Throwable e, Method[] retryMethods) {
        if (retryMethods == null || retryMethods.length == 0) {
            return true;
        }
        for (Method method : retryMethods) {
            Class<?>[] types = method.getParameterTypes();
            if (types.length != 1 || !ArrayUtils.contains(types, Throwable.class)) {
                continue;
            }
            try {
                if (!(Boolean) method.invoke(null, e)) {
                    return false;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return true;
    }
}
