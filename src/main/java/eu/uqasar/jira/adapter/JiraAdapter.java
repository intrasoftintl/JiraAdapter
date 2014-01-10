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
import java.sql.SQLException;
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

    dbConnector dbHandler;
    public JiraAdapter() {
          dbHandler = new dbConnector();
    }

    @Override
    public BindedSystem addSystemBindingInformation(BindingInformation bindingInformation) throws uQasarException {
         BindedSystem system = new BindedSystem();
           try {
                this.dbHandler.dbOpen();

                 int system_id = this.dbHandler.dbUpdate("INSERT INTO System (uri , id_type ) VALUES ('"+bindingInformation.getURI()+"', 1);");

                system.setId(String.valueOf(system_id));
                system.setBindingInformation(bindingInformation);

                this.dbHandler.dbClose();
           } catch (SQLException e) {
               throw new uQasarException (uQasarException.UQasarExceptionType.UQASAR_DB_CONNECTION_REFUSED);
           } catch (Exception e) {
               e.printStackTrace();
           }

            return system;

    }

    @Override
    public int addSystemBindingCredentials(Credentials credentials, int id_bindedSystem) throws uQasarException {
        int user_id=0;
        try {
            this.dbHandler.dbOpen();

            user_id = this.dbHandler.dbUpdate("INSERT INTO User( username ,password, id_system) VALUES ('"+credentials.getUsername()+"', '"+credentials.getPassword()+"', "+id_bindedSystem+");");

            this.dbHandler.dbClose();
        } catch (SQLException e) {
            throw new uQasarException (uQasarException.UQasarExceptionType.UQASAR_DB_CONNECTION_REFUSED);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user_id;
    }

    @Override
    public List<BindedSystem> getBindedSystems() throws uQasarException {
        LinkedList<BindedSystem> bindedSystems = new LinkedList<BindedSystem>();

        ResultSet rs;

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
        } catch (SQLException e) {
          throw new uQasarException (uQasarException.UQasarExceptionType.UQASAR_DB_CONNECTION_REFUSED);
        } catch (Exception e) {
            e.printStackTrace();
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
                } else if (queryExpression.getQuery().contains(uQasarMetric.SEARCH_ISSUES.name())) {

                    String[] stringParts = queryExpression.getQuery().split("SEARCH_ISSUES");
                    String jqlQuery = stringParts[1];

                    System.out.println("jqlQuery "+jqlQuery);

                        Promise<SearchResult> searchResultPromise = client.getSearchClient().searchJql(jqlQuery);
                        Iterable<BasicIssue> issues =  searchResultPromise.claim().getIssues();
                        measurementJSONResult = issues.toString();
                        measurements.add(new Measurement(uQasarMetric.SEARCH_ISSUES,measurementJSONResult));


                    return measurements;
                }else{

                   throw new uQasarException(uQasarException.UQasarExceptionType.UQASAR_NOT_EXISTING_METRIC,queryExpression.getQuery());

                }

        } catch (URISyntaxException e) {
            throw new uQasarException(uQasarException.UQasarExceptionType.BINDING_SYSTEM_BAD_URI_SYNTAX,bindedSystem,e.getCause());
        }  catch (RuntimeException e){
            throw new uQasarException(uQasarException.UQasarExceptionType.BINDING_SYSTEM_CONNECTION_REFUSED,bindedSystem,e.getCause());
        }


    }

}
