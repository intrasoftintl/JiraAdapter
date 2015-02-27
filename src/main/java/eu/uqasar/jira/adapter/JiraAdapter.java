package eu.uqasar.jira.adapter;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import eu.uqasar.adapter.SystemAdapter;
import eu.uqasar.adapter.exception.uQasarException;
import eu.uqasar.adapter.model.BindedSystem;
import eu.uqasar.adapter.model.Measurement;
import eu.uqasar.adapter.model.User;
import eu.uqasar.adapter.model.uQasarMetric;
import eu.uqasar.adapter.query.QueryExpression;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: eleni
 * Date: 1/9/14
 * Time: 11:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class JiraAdapter implements SystemAdapter {




    public JiraAdapter() {
    }

    @Override
    public List<Measurement> query(BindedSystem bindedSystem, User user, QueryExpression queryExpression) throws uQasarException {
        URI uri = null;
        LinkedList<Measurement> measurements = new LinkedList<Measurement>();


        try {

            /* Connection to JIRA instance */
            uri = new URI(bindedSystem.getUri());
            JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
            JiraRestClient client = factory.createWithBasicHttpAuthentication(uri, user.getUsername(), user.getPassword());

            /* START -- Metrics implementation */
            if (queryExpression.getQuery().equalsIgnoreCase(uQasarMetric.PROJECTS_PER_SYSTEM_INSTANCE.name())){

                JSONArray measurementResultJSONArray = new JSONArray();

                Iterable<BasicProject> basicProjects = client.getProjectClient().getAllProjects().claim();

                for (BasicProject basicProject : basicProjects) {

                    JSONObject bp = new JSONObject();
                    bp.put("self",basicProject.getSelf());
                    bp.put("key",basicProject.getKey());
                    bp.put("name",basicProject.getName());
                    measurementResultJSONArray.put(bp);
                }

                measurements.add(new Measurement(uQasarMetric.PROJECTS_PER_SYSTEM_INSTANCE, measurementResultJSONArray.toString()));

            }   else if (queryExpression.getQuery().equalsIgnoreCase(uQasarMetric.ISSUES_PER_PROJECTS_PER_SYSTEM_INSTANCE.name())) {

                Promise<SearchResult> searchResultPromise = client.getSearchClient().searchJql(" ORDER BY project DESC");
                Iterable<Issue> issues =  searchResultPromise.claim().getIssues();
                measurements.add(new Measurement(uQasarMetric.ISSUES_PER_PROJECTS_PER_SYSTEM_INSTANCE,formatIssuesResult(issues)));

            }  else if (queryExpression.getQuery().contains(uQasarMetric.FIXED_ISSUES_PER_PROJECT.name())) {

                Promise<SearchResult> searchResultPromise = client.getSearchClient().searchJql("resolution = Fixed ORDER BY updatedDate DESC");
                Iterable<Issue> issues =  searchResultPromise.claim().getIssues();
                measurements.add(new Measurement(uQasarMetric.FIXED_ISSUES_PER_PROJECT, formatIssuesResult(issues)));

            }  else if (queryExpression.getQuery().contains(uQasarMetric.UNRESOLVED_ISSUES_PER_PROJECT.name())) {
                Promise<SearchResult> searchResultPromise = client.getSearchClient().searchJql("resolution = Unresolved ORDER BY updatedDate DESC");
                Iterable<Issue> issues =  searchResultPromise.claim().getIssues();
                measurements.add(new Measurement(uQasarMetric.UNRESOLVED_ISSUES_PER_PROJECT, formatIssuesResult(issues)));

            } else if (queryExpression.getQuery().contains(uQasarMetric.UNRESOLVED_BUG_ISSUES_PER_PROJECT.name())) {

                Promise<SearchResult> searchResultPromise = client.getSearchClient().searchJql("issuetype = Bug AND status = \"To Do\"");
                Iterable<Issue> issues =  searchResultPromise.claim().getIssues();

                measurements.add(new Measurement(uQasarMetric.UNRESOLVED_BUG_ISSUES_PER_PROJECT, formatIssuesResult(issues)));
            } else if (queryExpression.getQuery().contains(uQasarMetric.UNRESOLVED_TASK_ISSUES_PER_PROJECT.name())) {
                Promise<SearchResult> searchResultPromise = client.getSearchClient().searchJql("issuetype = Task AND status = \"To Do\"");
                Iterable<Issue> issues =  searchResultPromise.claim().getIssues();
                measurements.add(new Measurement(uQasarMetric.UNRESOLVED_TASK_ISSUES_PER_PROJECT, formatIssuesResult(issues)));
            } else {
            throw new uQasarException(uQasarException.UQasarExceptionType.UQASAR_NOT_EXISTING_METRIC,queryExpression.getQuery());
            }

            // Close the JiraRestClient
            client.close();
            
            /* END -- Metrics implementation */


        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (URISyntaxException e) {
            throw new uQasarException(uQasarException.UQasarExceptionType.BINDING_SYSTEM_BAD_URI_SYNTAX,bindedSystem,e.getCause());
        }  catch (RuntimeException e){
            throw new uQasarException(uQasarException.UQasarExceptionType.BINDING_SYSTEM_CONNECTION_REFUSED,bindedSystem,e.getCause());
        } catch (IOException e) {
			e.printStackTrace();
		}
        return measurements;


    }

    @Override
    public List<Measurement> query(String bindedSystemURL, String credentials, String queryExpression) throws uQasarException {
        List<Measurement> measurements = null;

        BindedSystem bindedSystem = new BindedSystem();
        bindedSystem.setUri(bindedSystemURL);
        User user = new User();

        String[] creds = credentials.split(":");

        user.setUsername(creds[0]);
        user.setPassword(creds[1]);

        JiraQueryExpresion jiraQueryExpresion = new JiraQueryExpresion(queryExpression);

        JiraAdapter jiraAdapter = new JiraAdapter();

        measurements = jiraAdapter.query(bindedSystem,user,jiraQueryExpresion);


        return measurements;
    }

    public void printMeasurements(List<Measurement> measurements){
        String newLine = System.getProperty("line.separator");
        for (Measurement measurement : measurements) {
            System.out.println("----------TEST metric: "+measurement.getMetric()+" ----------"+newLine);
            System.out.println(measurement.getMeasurement()+newLine+newLine);
            System.out.println();

        }
    }

    public String formatIssuesResult( Iterable<Issue> issues) throws JSONException {

        JSONArray measurementResultJSONArray = new JSONArray();

        for (BasicIssue issue : issues) {
            JSONObject i = new JSONObject();
            i.put("self", issue.getSelf());
            i.put("key", issue.getKey());
            measurementResultJSONArray.put(i);
        }

        return  measurementResultJSONArray.toString();
    }



    //in order to invoke main from outside jar
    //mvn exec:java -Dexec.mainClass="eu.uqasar.jira.adapter.JiraAdapter" -Dexec.args="http://95.211.223.9:8084 soaptester:soaptester ISSUES_PER_PROJECTS_PER_SYSTEM_INSTANCE"

    public static void main(String[] args) {
        List<Measurement> measurements;
        String newLine = System.getProperty("line.separator");
        BindedSystem bindedSystem = new BindedSystem();
        bindedSystem.setUri(args[0]);
        User user = new User();
        String[] credentials = args[1].split(":");
        user.setUsername(credentials[0]);
        user.setPassword(credentials[1]);

        JiraQueryExpresion jiraQueryExpresion = new JiraQueryExpresion(args[2]);

        try {
        JiraAdapter jiraAdapter = new JiraAdapter();

            measurements = jiraAdapter.query(bindedSystem,user,jiraQueryExpresion);
            jiraAdapter.printMeasurements(measurements);


        } catch (uQasarException e) {
            e.printStackTrace();
        }
    }

}
