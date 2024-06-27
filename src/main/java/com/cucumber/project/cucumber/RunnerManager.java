package com.cucumber.project.cucumber;

import cucumber.runtime.model.CucumberFeature;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brian on 2/8/17.
 * The RunnerManager gets the appropriate feature files for the CucumberRunner
 */
public class RunnerManager {
    public static int THREAD_NUM = 0;
    public static final String DEFAULT_MAX_THREADS = "4";

    private int max_threads;
    static String className = "CucumberRunner";
    private Class clazz;
    private int threadNum;
    private ArrayList<String> features;

    public RunnerManager(Class clazz){
        //clazz is CucumberRunner1, etc.
        this.clazz = clazz;
      //todo pull number of threads from system property or config file
        this.threadNum = getRunnerNumber();
        THREAD_NUM = this.threadNum;
        this.features = new ArrayList<>();
    }

    /**
     * Splits the list of CucumberFeatures based on thread number.
     * @param features the total list of cucumber features to run.
     * @return the subset of feature files for each thread.
     */
    public List<CucumberFeature> splitFeatures(List<CucumberFeature> features) {
        List<CucumberFeature> splitFeatures = new ArrayList<CucumberFeature>();

        //if the threads are 0 we are not multithreading and will return the original results
        if(this.threadNum == 0){
            return features;
        }
        else{

            int count = features.size(); //12

            float size = (float)count / (float)max_threads;  // 1 / 3 = .333

            float index1 = ((float) (threadNum-1)) / (float) max_threads; // 0
            float index2 = (float) threadNum / (float) max_threads;  // .333

            int key1 = (int)(index1 * count); // 0
            int key2 = (int)(index2 * count); // 3

            for(int i=0;i<count;i++){
                if(i >= key1 && i < key2){
                    splitFeatures.add(features.get(i));
                }
            }

        }

        return splitFeatures;
    }

    /**
     * Gets the thread number based upon the class name.
     * @return the runner number associated with the class name (i.e. 1 for CucumberRunner1).
     */
    public int getRunnerNumber(){
        String pack = clazz.getPackage().getName();
        String name = clazz.getName().replace(pack,"").replace(".","");
        String num = name.replace(className, "");
        if(num.isEmpty()){
            return 0;
        }else{
            int number = Integer.valueOf(num);
            return number;
        }
    }

    /**
     * Get the thread number of this thread.
     * @return the thread number of this thread or 0 if not running in parallel.
     */
    public static int getThreadNum() {
        return THREAD_NUM;
    }
}
