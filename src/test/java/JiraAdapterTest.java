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

    // Test uqasar db connection
    // Test   metrics : { RESOURCES_PER_BINDING , ISSUES_PER_RESOURCE_PER_BINDING }
    @Test
    public void queryTest(){
        List<Measurement> measurements;

        try{
            for (uQasarMetric metric  :uQasarMetric.values()) {

                measurements = jiraAdapter.query(bindedSystemURL, credentials, metric.name());
                printMeasurements(measurements);
            }
        }catch (uQasarException e){
            e.printStackTrace();
        }
    }


    // Try to pass a non existing metric
    @Test
    public void queryTest_erroneus_metric(){

    try{
        Measurement m = jiraAdapter.query(bindedSystemURL, credentials, "ERRONEUS METRIC").get(0);
    }catch (uQasarException e){
        e.printStackTrace();
        assertTrue(true);
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
