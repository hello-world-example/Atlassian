package xyz.kail.demo.leader.atlassian

// region 代码提示
Issue issue = issue
// endregion

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl
import com.opensymphony.workflow.WorkflowContext


UserUtil userUtil = ComponentAccessor.userUtil

//
//
String currentUser = ((WorkflowContext) transientVars.get("context")).getCaller()


def workflowManager = ComponentAccessor.getWorkflowManager()

WorkflowTransitionUtil workflowTransitionUtil = (WorkflowTransitionUtil) JiraUtils.loadComponent(WorkflowTransitionUtilImpl.class)

MutableIssue parent = issue.getParentObject() as MutableIssue
String originalParentStatus = parent.getStatus().getSimpleStatus().getName()
def isDevBacklogStatus = originalParentStatus in ['Next Up Dev', 'Backlog Dev', 'Selected for Development']
if (isDevBacklogStatus) {
    workflowTransitionUtil.setIssue(parent)
    workflowTransitionUtil.setUserkey(currentUser)
    workflowTransitionUtil.setAction(171)
    if (workflowTransitionUtil.validate()) {
        workflowTransitionUtil.progress()
    }
}
