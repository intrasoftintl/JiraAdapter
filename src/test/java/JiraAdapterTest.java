import eu.uqasar.adapter.exception.uQasarException;
import eu.uqasar.adapter.model.*;
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


    // Test uqasar db connection
    // Test   metrics : { RESOURCES_PER_BINDING , ISSUES_PER_RESOURCE_PER_BINDING }
    @Test
    public void queryTest(){
        List<Measurement> measurements;
        JiraAdapter jiraAdapter = new JiraAdapter();
        List<BindedSystem> bindedSystems = null;
        try {
            bindedSystems = jiraAdapter.getBindedSystems();
        } catch (uQasarException e) {
            e.printStackTrace();
        }


            for (BindedSystem bindedSystem : bindedSystems) {

                try{
                    for (uQasarMetric metric  :uQasarMetric.values()) {
                        System.out.println("----------TEST metric: "+metric+" ----------");
                        JiraQueryExpresion jiraQueryExpresion = new JiraQueryExpresion(metric.name());
                        measurements = jiraAdapter.query(bindedSystem, bindedSystem.getCredentials(), jiraQueryExpresion);
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

        JiraAdapter jiraAdapter = new JiraAdapter();


        List<BindedSystem> bindedSystems = null;
        try {
            bindedSystems = jiraAdapter.getBindedSystems();
        } catch (uQasarException e) {
            e.printStackTrace();
        }

        JiraQueryExpresion jiraQueryExpresion = new JiraQueryExpresion("ERRONEUS METRIC");

        for (BindedSystem bindedSystem : bindedSystems) {

            try{
                Measurement m = jiraAdapter.query(bindedSystem, bindedSystem.getCredentials(), jiraQueryExpresion).get(0);
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
        JiraAdapter jiraAdapter = new JiraAdapter();

        try {
            BindedSystem bindedSystem = jiraAdapter.addSystemBindingInformation(new BindingInformation("http://testinstance"));
            System.out.println(bindedSystem.getBindingInformation().getURI());
        } catch (uQasarException e) {
            e.printStackTrace();
        }
        */
    }


    // Add new System to uQasarBinding database
    @Test
    public void addSystemBindingCredentialsTest(){
       /*
        JiraAdapter jiraAdapter = new JiraAdapter();

        try {
            int id_user = jiraAdapter.addSystemBindingCredentials(new Credentials("manos","sifakis"),4);
            System.out.println("id_user : "+id_user);
        } catch (uQasarException e) {
            e.printStackTrace();
        }
        */
    }


    public void printMeasurements(List<Measurement> measurements){

        for (Measurement measurement : measurements) {
            System.out.println(measurement.getMetric());
            System.out.println(measurement.getMeasurement());

        }
    }

}
