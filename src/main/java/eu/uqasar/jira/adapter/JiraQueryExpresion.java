package eu.uqasar.jira.adapter;

import eu.uqasar.adapter.query.QueryExpression;

/**
 * Created with IntelliJ IDEA.
 * User: eleni
 * Date: 1/9/14
 * Time: 3:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class JiraQueryExpresion extends QueryExpression {

    String query;

    public JiraQueryExpresion(String query) {
        super(query);
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
