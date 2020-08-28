package xyz.kail.demo.leader.atlassian.fields

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.fields.CustomField

/**
 * 子任务 @AutoTest 自动回显父任务的 Auto Test 字段内容
 */
// region 代码提示
Issue issue = ComponentAccessor.issueManager.getIssueObject("ARCH-1")
// endregion

// 只针对子任务
if (!issue.subTask) {
    return
}

if (!issue.summary.contains("【测试任务】")) {
    return
}

CustomField autoTestField = ComponentAccessor.customFieldManager.getCustomFieldObjectByName("Auto Test")
if (null == autoTestField) {
    return
}

// 获取 父任务的字段值
def autoTestValue = issue.parentObject.getCustomFieldValue(autoTestField)
if (null == autoTestValue) {
    return
}

return autoTestValue.toString()

