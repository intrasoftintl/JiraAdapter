import eu.uqasar.adapter.exception.uQasarException;
import eu.uqasar.adapter.model.BindedSystem;
import eu.uqasar.adapter.model.Measurement;
import eu.uqasar.adapter.model.uQasarMetric;
import eu.uqasar.jira.adapter.JiraAdapter;
import eu.uqasar.jira.adapter.JiraQueryExpresion;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: eleni
 * Date: 1/9/14
 * Time: 6:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class JiraAdapterTest {

    JiraAdapter jiraAdapter = new JiraAdapter();
    String newLine = System.getProperty("line.separator");
    // Test uqasar db connection
    // Test   metrics : { RESOURCES_PER_BINDING , ISSUES_PER_RESOURCE_PER_BINDING }
    @Test
    public void queryTest(){
        List<Measurement> measurements;
        List<BindedSystem> bindedSystems = null;
        try {
            bindedSystems = jiraAdapter.getBindedSystems();
        } catch (uQasarException e) {
            e.printStackTrace();
        }


            for (BindedSystem bindedSystem : bindedSystems) {

                try{
                    for (uQasarMetric metric  :uQasarMetric.values()) {
                        JiraQueryExpresion jiraQueryExpresion = new JiraQueryExpresion(metric.name());
                        measurements = jiraAdapter.query(bindedSystem, bindedSystem.getUser(), jiraQueryExpresion);
                        printMeasurements(measurements);
                    }
                }catch (uQasarException e){
                    e.printStackTrace();
                }
            }
    }


    // Try to pass a non existing metric
    @Test
    public void queryTest_erroneus_metric(){

        List<BindedSystem> bindedSystems = null;
        try {
            bindedSystems = jiraAdapter.getBindedSystems();
        } catch (uQasarException e) {
            e.printStackTrace();
        }

        JiraQueryExpresion jiraQueryExpresion = new JiraQueryExpresion("ERRONEUS METRIC");

        for (BindedSystem bindedSystem : bindedSystems) {

            try{
                Measurement m = jiraAdapter.query(bindedSystem, bindedSystem.getUser(), jiraQueryExpresion).get(0);
            }catch (uQasarException e){
                e.printStackTrace();
                assertTrue(true);
            }
        }
    }

    // Add new System to uQasarBinding database
    @Test
    public void addSystemBindingInformationTest(){
        /*
        try {
            BindedSystem bindedSystem = jiraAdapter.addSystemBindingInformation(new BindedSystem("http://testinstance",1),new User("eleni","ego"));
            System.out.println(bindedSystem.getUri());
        } catch (uQasarException e) {
            e.printStackTrace();
        }
        */

    }

    // Add new System to uQasarBinding database
    @Test
    public void getBindedSystemsTest(){

        try {
            List<BindedSystem> bindedSystems = jiraAdapter.getBindedSystems();
            System.out.println("----------TEST GET BindedSystems----------"+newLine);
            for (BindedSystem bindedSystem : bindedSystems) {

                System.out.println("Binded System base url : "+ bindedSystem.getUri() + "Username : " + bindedSystem.getUser().getUsername());
            }
        } catch (uQasarException e) {
            e.printStackTrace();
        }

    }


    public void printMeasurements(List<Measurement> measurements){

        for (Measurement measurement : measurements) {
            System.out.println("----------TEST metric: "+measurement.getMetric()+" ----------"+newLine);
            System.out.println(measurement.getMeasurement()+newLine+newLine);
            System.out.println();

        }
    }

}
