package xyz.kail.demo.leader.atlassian.snippet

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.status.Status
import com.atlassian.jira.issue.status.category.StatusCategory
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl
import com.opensymphony.workflow.loader.ActionDescriptor

/**
 * 修改任务状态（通过工作流）
 */

Issue issue = ComponentAccessor.issueManager.getIssueObject("ARCH-1")

//Status statusObject = issue.getStatusObject()
//StatusCategory statusCategory = statusObject.getStatusCategory();
//
//
//return "$statusObject.id, $statusObject.name, $statusObject.description, <br>" +
//        " $statusObject.sequence, $statusObject.nameTranslation, $statusObject.descTranslation, <br>" +
//        " $statusCategory.id, $statusCategory.key, $statusCategory.name, $statusCategory.translatedName, <br>" +
//        " $issue.workflowId, <br>"


MutableIssue mutableIssue = (MutableIssue) issue

WorkflowTransitionUtil workflowTransitionUtil = (WorkflowTransitionUtil) JiraUtils.loadComponent(WorkflowTransitionUtilImpl.class)

// 指定任务
workflowTransitionUtil.setIssue(mutableIssue)
// 指定操作人
workflowTransitionUtil.setUserkey("kaibin.yang")
// 指定 转换(ID)
workflowTransitionUtil.setAction(44)

// 修改后的状态
ActionDescriptor actionDescriptor = workflowTransitionUtil.getActionDescriptor()
return actionDescriptor


// 校验和执行
workflowTransitionUtil.validate()
workflowTransitionUtil.progress()


