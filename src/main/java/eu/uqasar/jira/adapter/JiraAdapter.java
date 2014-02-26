package eu.uqasar.jira.adapter;


import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.JiraRestClientFactory;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.BasicProject;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import eu.uqasar.adapter.SystemAdapter;
import eu.uqasar.adapter.exception.uQasarException;
import eu.uqasar.adapter.model.BindedSystem;
import eu.uqasar.adapter.model.User;
import eu.uqasar.adapter.model.uQasarMetric;
import eu.uqasar.adapter.query.QueryExpression;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

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
    public JSONArray query(BindedSystem bindedSystem, User user, QueryExpression queryExpression) throws uQasarException {
        URI uri = null;
        JSONArray jsonArrayresult;
        JSONArray measurements = new JSONArray();

        try {

            /* Connection to JIRA instance */
            uri = new URI(bindedSystem.getUri());
            JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
            JiraRestClient client = factory.createWithBasicHttpAuthentication(uri, user.getUsername(), user.getPassword());

            /* START -- Metrics implementation */
            if (queryExpression.getQuery().equalsIgnoreCase(uQasarMetric.PROJECTS_PER_SYSTEM_INSTANCE.name())){

                jsonArrayresult  = new JSONArray((Collection) client.getProjectClient().getAllProjects().claim());

                JSONObject jsonObject = new JSONObject();
                jsonObject.put(uQasarMetric.PROJECTS_PER_SYSTEM_INSTANCE.name(), jsonArrayresult);
                measurements.put(jsonObject);


                //measurements.add(new Measurement(uQasarMetric.PROJECTS_PER_SYSTEM_INSTANCE, measurementJSONResult));

            }
            else if (queryExpression.getQuery().equalsIgnoreCase(uQasarMetric.ISSUES_PER_PROJECTS_PER_SYSTEM_INSTANCE.name())) {
                JSONObject jsonObject = new JSONObject();
                Iterable<BasicProject> basicProjects  =   client.getProjectClient().getAllProjects().claim();
                for (BasicProject basicProject : basicProjects) {
                    Promise<SearchResult> searchResultPromise = client.getSearchClient().searchJql("project = "+basicProject.getName());
                    Iterable<BasicIssue> issues =  searchResultPromise.claim().getIssues();
                    jsonArrayresult = new JSONArray((Collection) issues);
                    jsonObject.put(basicProject.getName(), jsonArrayresult);

                    //measurementJSONResult = issues.toString();
                    //measurements.add(new Measurement(uQasarMetric.ISSUES_PER_PROJECTS_PER_SYSTEM_INSTANCE,measurementJSONResult));
                }
                JSONObject supersetJsonObject = new JSONObject();
                supersetJsonObject.put(uQasarMetric.ISSUES_PER_PROJECTS_PER_SYSTEM_INSTANCE.name(), jsonObject);

                measurements.put(supersetJsonObject);

            }
            else if (queryExpression.getQuery().contains(uQasarMetric.FIXED_ISSUES_PER_PROJECT.name())) {
                Promise<SearchResult> searchResultPromise = client.getSearchClient().searchJql("resolution = Fixed ORDER BY updatedDate DESC");
                Iterable<BasicIssue> issues =  searchResultPromise.claim().getIssues();
                jsonArrayresult = new JSONArray((Collection) issues);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(uQasarMetric.FIXED_ISSUES_PER_PROJECT.name(), jsonArrayresult);
                measurements.put(jsonObject);
                //measurementJSONResult = issues.toString();
                //measurements.add(new Measurement(uQasarMetric.FIXED_ISSUES_PER_PROJECT,measurementJSONResult));

            }
            else if (queryExpression.getQuery().contains(uQasarMetric.UNRESOLVED_ISSUES_PER_PROJECT.name())) {
                Promise<SearchResult> searchResultPromise = client.getSearchClient().searchJql("resolution = Unresolved ORDER BY updatedDate DESC");
                Iterable<BasicIssue> issues =  searchResultPromise.claim().getIssues();

                jsonArrayresult = new JSONArray((Collection) issues);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(uQasarMetric.UNRESOLVED_ISSUES_PER_PROJECT.name(), jsonArrayresult);
                measurements.put(jsonObject);

                //measurementJSONResult = issues.toString();
                //measurements.add(new Measurement(uQasarMetric.UNRESOLVED_ISSUES_PER_PROJECT,measurementJSONResult));

            }
            else if (queryExpression.getQuery().contains(uQasarMetric.UNRESOLVED_BUG_ISSUES_PER_PROJECT.name())) {

                Promise<SearchResult> searchResultPromise = client.getSearchClient().searchJql("issuetype = Bug AND status = \"To Do\"");
                Iterable<BasicIssue> issues =  searchResultPromise.claim().getIssues();

                jsonArrayresult = new JSONArray((Collection) issues);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(uQasarMetric.UNRESOLVED_BUG_ISSUES_PER_PROJECT.name(), jsonArrayresult);
                measurements.put(jsonObject);
            }
            else if (queryExpression.getQuery().contains(uQasarMetric.UNRESOLVED_TASK_ISSUES_PER_PROJECT.name())) {
                Promise<SearchResult> searchResultPromise = client.getSearchClient().searchJql("issuetype = Task AND status = \"To Do\"");
                Iterable<BasicIssue> issues =  searchResultPromise.claim().getIssues();
                jsonArrayresult = new JSONArray((Collection) issues);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(uQasarMetric.UNRESOLVED_TASK_ISSUES_PER_PROJECT.name(), jsonArrayresult);
                measurements.put(jsonObject);            }
            else
            {
            throw new uQasarException(uQasarException.UQasarExceptionType.UQASAR_NOT_EXISTING_METRIC,queryExpression.getQuery());
            }


            /* END -- Metrics implementation */


        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (URISyntaxException e) {
            throw new uQasarException(uQasarException.UQasarExceptionType.BINDING_SYSTEM_BAD_URI_SYNTAX,bindedSystem,e.getCause());
        }  catch (RuntimeException e){
            throw new uQasarException(uQasarException.UQasarExceptionType.BINDING_SYSTEM_CONNECTION_REFUSED,bindedSystem,e.getCause());
        }
        return measurements;


    }

    @Override
    public JSONArray query(String bindedSystemURL, String credentials, String queryExpression) throws uQasarException {
        JSONArray measurements = null;

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



    //in order to invoke main from outside jar
    //mvn exec:java -Dexec.mainClass="eu.uqasar.jira.adapter.JiraAdapter" -Dexec.args="http://95.211.223.9:8084 soaptester:soaptester ISSUES_PER_PROJECTS_PER_SYSTEM_INSTANCE"

    public static void main(String[] args) {
        JSONArray measurements;
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
            System.out.println("------------------------------------------"+newLine);
            System.out.println(measurements);

        } catch (uQasarException e) {
            e.printStackTrace();
        }
    }

}
