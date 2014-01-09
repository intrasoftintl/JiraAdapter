import eu.uqasar.adapter.exception.uQasarException;
import eu.uqasar.adapter.model.BindedSystem;
import eu.uqasar.adapter.model.Measurement;
import eu.uqasar.jira.adapter.JiraAdapter;
import eu.uqasar.jira.adapter.JiraQueryExpresion;
import org.junit.Test;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: eleni
 * Date: 1/9/14
 * Time: 6:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class JiraAdapterTest {

    @Test
    public void queryTest(){

        JiraAdapter jiraAdapter = new JiraAdapter();

        try{

            List<BindedSystem> bindedSystems = jiraAdapter.getBindedSystems();

            JiraQueryExpresion jiraQueryExpresion = new JiraQueryExpresion("RESOURCES_PER_BINDING");

            for (BindedSystem bindedSystem : bindedSystems) {
                System.out.println(bindedSystem.getBindingInformation().getURI());
                System.out.println("**********************");
                Measurement m = jiraAdapter.query(bindedSystem, bindedSystem.getCredentials(), jiraQueryExpresion).get(0);
                System.out.println(m.getMetric());
                System.out.println(m.getMeasurement());
                System.out.println("**********************");



                JiraQueryExpresion ISSUES_PER_RESOURCE_PER_BINDING_Expresion = new JiraQueryExpresion("ISSUES_PER_RESOURCE_PER_BINDING");
                List<Measurement> measurements = jiraAdapter.query(bindedSystem, bindedSystem.getCredentials(), ISSUES_PER_RESOURCE_PER_BINDING_Expresion);

                for (Measurement measurement : measurements) {
                    System.out.println("-------------------------------------------");
                    System.out.println(measurement.getMetric());
                    System.out.println(measurement.getMeasurement());
                    System.out.println("-------------------------------------------");
                }
            }


        }catch (uQasarException e){
            e.printStackTrace();
        }


    }

}
