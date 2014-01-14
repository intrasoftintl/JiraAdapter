JiraAdapter
===========

JiraAdapter is a project that implements the uQasarAdapter interface overrinding the following methods:

	addSystemBindingInformation : adds a new Jira instance at uQuasar system

	getBindedSystems :  retrieves all Jira binded instances that are registered at uQasar system

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

    UQASAR_DB_CONNECTION_REFUSED (thrown when there is a problem with uQasar database connection)

    BINDING_SYSTEM_CONNECTION_REFUSED (thrown when a binding system refuses the connection to the third party Adapter)

    BINDING_SYSTEM_BAD_URI_SYNTAX, (thrown when the binding system base url is malformed)

    UQASAR_NOT_EXISTING_METRIC (thrown when the queried metric is not a proper uQasarMetric)

 
 ---------------------------------------------------------------------
 
All JiraAdapter methods are tested via junit tests
