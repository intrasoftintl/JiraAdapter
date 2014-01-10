JiraAdapter
===========
JiraAdapter is a RESTful java Client that implements the uQasarAdapter interface overrinding the following methods

addSystemBindingInformation : add a new Jira instance at uQuasar Quality Model

getBindedSystems :  retrieve all Jira binded Instances that are registered at uQasarBinding database

query : invokes a specific query to a specified bindedSystem using the credentials of a specific user and returns a list of measurements depending on the metric the query contains

------------------------------------------------------------------------

JiraAdapter implements all the predefined metrics that are proposed by uQasarAdapter:

For the time being these metrrics are:

RESOURCES_PER_BINDING (retrieve all the projects of every JIRA binded system of the uQasar database)

ISSUES_PER_RESOURCE_PER_BINDING (retrieve all the issues for each project of every JIRA binded system of the uQasar database)

SEARCH_ISSUES (retrieve all the issues of every JIRA binded system that satisfy any jql expression ex. issuetype = Bug AND status = "To Do")

----------------------------------------------------------------------

Furthermore JiraAdapter throws the proposed uQuasarExceptionTypes

 UQASAR_DB_CONNECTION_REFUSED (when the is a problem with uQuasar database connection)
 
 BINDING_SYSTEM_CONNECTION_REFUSED (when a binding system refuses the connection to the JiraAdapter)
 
 BINDING_SYSTEM_BAD_URI_SYNTAX, (when the binding system base url is mal formed)
 
 UQASAR_NOT_EXISTING_METRIC (when the queried metric is not an uQuasarMetric)
 
 ---------------------------------------------------------------------
 
All JiraAdapter methods are tested via junit tests
