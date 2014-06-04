import eu.uqasar.adapter.exception.uQasarException;
import eu.uqasar.adapter.model.Measurement;
import eu.uqasar.adapter.model.uQasarMetric;
import eu.uqasar.jira.adapter.JiraAdapter;
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
    String bindedSystemURL = "http://95.211.223.9:8084";
    String credentials = "soaptester:soaptester";

    /*
     Test uqasar db connection
     Test   metrics :
    {
      PROJECTS_PER_SYSTEM_INSTANCE ,
      ISSUES_PER_PROJECTS_PER_SYSTEM_INSTANCE,
      FIXED_ISSUES_PER_PROJECT,
      UNRESOLVED_ISSUES_PER_PROJECT,
      UNRESOLVED_BUG_ISSUES_PER_PROJECT,
      UNRESOLVED_TASK_ISSUES_PER_PROJECT
    }
    */

    @Test
    public void queryTestAllMetrics(){
        List<Measurement> measurements = null;


            for (uQasarMetric metric  :uQasarMetric.values()) {

                try{
                measurements = jiraAdapter.query(bindedSystemURL, credentials, metric.name());
                    jiraAdapter.printMeasurements(measurements);
                }catch (uQasarException e){
                    System.out.println(e.toString());
                }

            }


    }


    @Test
    public void queryTestPROJECTS_PER_SYSTEM_INSTANCE(){
        List<Measurement> measurements = null;
            try{
                measurements = jiraAdapter.query(bindedSystemURL, credentials, "PROJECTS_PER_SYSTEM_INSTANCE");
                jiraAdapter.printMeasurements(measurements);
            }catch (uQasarException e){
                System.out.println(e.toString());
            }
    }


    @Test
    public void queryTestISSUES_PER_PROJECTS_PER_SYSTEM_INSTANCE(){
        List<Measurement> measurements = null;
        try{
            measurements = jiraAdapter.query(bindedSystemURL, credentials, "ISSUES_PER_PROJECTS_PER_SYSTEM_INSTANCE");
            jiraAdapter.printMeasurements(measurements);
        }catch (uQasarException e){
            System.out.println(e.toString());
        }
    }

    @Test
    public void queryTestFIXED_ISSUES_PER_PROJECT(){
        List<Measurement> measurements = null;
        try{
            measurements = jiraAdapter.query(bindedSystemURL, credentials, "FIXED_ISSUES_PER_PROJECT");
            jiraAdapter.printMeasurements(measurements);
        }catch (uQasarException e){
            System.out.println(e.toString());
        }
    }

    @Test
    public void queryTestUNRESOLVED_ISSUES_PER_PROJECT(){
        List<Measurement> measurements = null;
        try{
            measurements = jiraAdapter.query(bindedSystemURL, credentials, "UNRESOLVED_ISSUES_PER_PROJECT");
            jiraAdapter.printMeasurements(measurements);
        }catch (uQasarException e){
            System.out.println(e.toString());
        }
    }

    @Test
    public void queryTestUNRESOLVED_BUG_ISSUES_PER_PROJECT(){
        List<Measurement> measurements = null;
        try{
            measurements = jiraAdapter.query(bindedSystemURL, credentials, "UNRESOLVED_BUG_ISSUES_PER_PROJECT");
            jiraAdapter.printMeasurements(measurements);
        }catch (uQasarException e){
            System.out.println(e.toString());
        }
    }

    @Test
    public void queryTestUNRESOLVED_TASK_ISSUES_PER_PROJECT(){
        List<Measurement> measurements = null;
        try{
            measurements = jiraAdapter.query(bindedSystemURL, credentials, "UNRESOLVED_TASK_ISSUES_PER_PROJECT");
            jiraAdapter.printMeasurements(measurements);
        }catch (uQasarException e){
            System.out.println(e.toString());
        }
    }








    // Try to pass a non existing metric
    @Test
    public void queryTest_erroneus_metric(){

    try{
        List<Measurement> measurements = jiraAdapter.query(bindedSystemURL, credentials, "ERRONEUS METRIC");
    }catch (uQasarException e){
        e.printStackTrace();
        assertTrue(true);
    }

    }

}
