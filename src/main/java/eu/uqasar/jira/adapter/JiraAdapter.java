package eu.uqasar.jira.adapter;


import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.JiraRestClientFactory;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.BasicProject;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import eu.uqasar.adapter.SystemAdapter;
import eu.uqasar.adapter.dataAccessLayer.dbConnector;
import eu.uqasar.adapter.exception.uQasarException;
import eu.uqasar.adapter.model.*;
import eu.uqasar.adapter.query.QueryExpression;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.ResultSet;
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
    public BindedSystem addSystemBindingInformation(BindingInformation bindingInformation) throws uQasarException {
        return null;
    }

    @Override
    public List<BindedSystem> getBindedSystems() throws uQasarException {
        LinkedList<BindedSystem> bindedSystems = new LinkedList<BindedSystem>();

        ResultSet rs;

        dbConnector dbHandler = new dbConnector();
        try {

            dbHandler.dbOpen();
            rs = dbHandler.dbQuery("SELECT * FROM System, User WHERE User.id_system = System.id_system AND id_type =1;");
            if (rs != null) {
                while (rs.next()) {

               BindedSystem b = new BindedSystem(rs.getString("id_system"));
                b.setBindingInformation(new BindingInformation(rs.getString("uri")));
                b.setCredentials(new Credentials(rs.getString("username"),rs.getString("password")));
                bindedSystems.add(b);
                System.out.println(b.getId()+b.getBindingInformation().getURI()+b.getCredentials().getPassword());

                }
            }
            rs.close();
            dbHandler.dbClose();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return bindedSystems;
    }

    @Override
    public List<Measurement> query(BindedSystem bindedSystem, Credentials credentials, QueryExpression queryExpression) throws uQasarException {
        URI uri = null;
        String measurementJSONResult;
        LinkedList<Measurement> measurements = new LinkedList<Measurement>();
        try {
            uri = new URI(bindedSystem.getBindingInformation().getURI());
        } catch (URISyntaxException e) {
           throw new uQasarException("URISyntaxException" + e.toString());
        }

        JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
        JiraRestClient client = factory.createWithBasicHttpAuthentication(uri, bindedSystem.getCredentials().getUsername(), bindedSystem.getCredentials().getPassword());

        if (queryExpression.getQuery().equalsIgnoreCase(uQasarMetric.RESOURCES_PER_BINDING.name())){

             measurementJSONResult =   client.getProjectClient().getAllProjects().claim().toString();
             measurements.add(new Measurement(uQasarMetric.RESOURCES_PER_BINDING,measurementJSONResult));
             return measurements;

        }  else if (queryExpression.getQuery().equalsIgnoreCase(uQasarMetric.ISSUES_PER_RESOURCE_PER_BINDING.name())) {

                Iterable<BasicProject> basicProjects  =   client.getProjectClient().getAllProjects().claim();
                for (BasicProject basicProject : basicProjects) {
                    System.out.println(basicProject.getName());

                   Promise<SearchResult> searchResultPromise = client.getSearchClient().searchJql("project = "+basicProject.getName());
                   Iterable<BasicIssue> issues =  searchResultPromise.claim().getIssues();
                   measurementJSONResult = issues.toString();
                   measurements.add(new Measurement(uQasarMetric.ISSUES_PER_RESOURCE_PER_BINDING,measurementJSONResult));
                }

            return measurements;
        } else if (queryExpression.getQuery().equalsIgnoreCase(uQasarMetric.USERS_PER_BINDING.name())) {

            return null;

        }else{

           throw new uQasarException("metric is not defined");

        }


    }

}
