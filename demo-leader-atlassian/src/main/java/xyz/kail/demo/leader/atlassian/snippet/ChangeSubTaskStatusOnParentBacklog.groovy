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
 * 还原项目状态【用测测试】
 */

IssueType typeObject = issue.getIssueTypeObject()

// 是主任务 (不是子任务)
if (!typeObject.subTask) {
    // 并且有项目看板标签
    if (issue.labels.collect { it.label }.contains("项目看板")) {
        Collection<Issue> issues = issue.subTaskObjects
        for (Issue sub in issues) {
            // 还原 (61) 子任务
            changeIssueStatus(sub, [61], "kaibin.yang")
        }
    }
}

static def changeIssueStatus(Issue issue, List<Integer> flowStep, String userKey) {

    WorkflowTransitionUtil workflowTransitionUtil = (WorkflowTransitionUtil) JiraUtils.loadComponent(WorkflowTransitionUtilImpl.class)

    IssueImpl subIssueImpl = (IssueImpl) issue

    for (Integer action : flowStep) {
        // 指定任务
        workflowTransitionUtil.setIssue(subIssueImpl)
        // 指定操作人
        workflowTransitionUtil.setUserkey(userKey)
        // 指定 转换(ID)
        workflowTransitionUtil.setAction(action)

        // 校验和执行
        workflowTransitionUtil.validate()
        workflowTransitionUtil.progress()
    }
}





