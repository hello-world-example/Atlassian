package xyz.kail.demo.leader.atlassian.snippet

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.IssueImpl
import com.atlassian.jira.issue.issuetype.IssueType
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl

// region 代码提示
Issue issue = ComponentAccessor.issueManager.getIssueObject("ARCH-1")
// endregion


/**
 * 父项目关闭时，关闭所有子项目
 */

IssueType typeObject = issue.getIssueTypeObject()

// 是主任务 (不是子任务)
if (typeObject.subTask) {
    return
}
// 并且有项目看板标签
if (!issue.labels.collect { it.label }.contains("项目看板")) {
    return
}


WorkflowTransitionUtil workflowTransitionUtil = (WorkflowTransitionUtil) JiraUtils.loadComponent(WorkflowTransitionUtilImpl.class)

Collection<Issue> issues = issue.subTaskObjects
for (Issue sub in issues) {
    IssueImpl subIssueImpl = (IssueImpl) sub

    // 指定任务
    workflowTransitionUtil.setIssue(subIssueImpl)
    // 指定操作人
    workflowTransitionUtil.setUserkey("auto.robot")
    // 指定 转换(ID) 直接关闭 (81)
    workflowTransitionUtil.setAction(81)

    // 校验和执行
    workflowTransitionUtil.validate()
    workflowTransitionUtil.progress()
}





