JiraAdapter
===========

JiraAdapter is a project that implements the uQasarAdapter interface overrinding the following methods:

	query : invokes a specific query to a specified binded system-instance using the credentials of a specific user while in paraller returns a list of measurements

------------------------------------------------------------------------

JiraAdapter measures all the predefined metrics that are proposed by uQasarAdapter:

For the time being these metrrics are:

     
        PROJECTS_PER_SYSTEM_INSTANCE

        ISSUES_PER_PROJECTS_PER_SYSTEM_INSTANCE

        FIXED_ISSUES_PER_PROJECT

        UNRESOLVED_ISSUES_PER_PROJECT

        UNRESOLVED_BUG_ISSUES_PER_PROJECT

        UNRESOLVED_TASK_ISSUES_PER_PROJECT



----------------------------------------------------------------------

Furthermore JiraAdapter throws the proposed uQuasarExceptionTypes


    BINDING_SYSTEM_CONNECTION_REFUSED (thrown when a binding system refuses the connection to the third party Adapter)

    BINDING_SYSTEM_BAD_URI_SYNTAX, (thrown when the binding system base url is malformed)

    UQASAR_NOT_EXISTING_METRIC (thrown when the queried metric is not a proper uQasarMetric)

 
 ---------------------------------------------------------------------
 
All JiraAdapter methods are tested via junit tests

---------------------------------------------------------------------

JiraAdapter can be invoked as Java Library (JAR) from command line as 


	mvn exec:java -Dexec.mainClass="eu.uqasar.jira.adapter.JiraAdapter" -Dexec.args="http://95.211.223.9:8084 soaptester:soaptester ISSUES_PER_PROJECTS_PER_SYSTEM_INSTANCE"
	
OR

	java -cp JiraAdapter-1.0.jar eu.uqasar.jira.adapter.JiraAdapter http://95.211.223.9:8084 soaptester:soaptester ISSUES_PER_PROJECTS_PER_SYSTEM_INSTANCE

		
 
arg0 is the URL binding or the JiraInstallation
arg1 is the string concatenation of username:password
arg2 is the desired METRIC

--------------------------------------------------------------------

Jira Adapter can be invoked as a library from an external class e.g:public class jiraAdapterInvocation {

    public static void main(String[] args) throws uQasarException {
        String newLine = System.getProperty("line.separator");
        List<Measurement> measurements;

        String bindedSystemURL = "http://95.211.223.9:8084";
        String credentials = "soaptester:soaptester";


        JiraAdapter jiraAdapter = new JiraAdapter();

        for (uQasarMetric metric  :uQasarMetric.values()) {

            measurements = jiraAdapter.query(bindedSystemURL, credentials, metric.name());

            for (Measurement measurement : measurements) {
                System.out.println("----------TEST metric: "+measurement.getMetric()+" ----------"+newLine);
                System.out.println(measurement.getMeasurement()+newLine+newLine);
                System.out.println();

            }
   		}

  		}
	}

