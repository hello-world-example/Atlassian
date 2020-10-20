package xyz.kail.demo.leader.atlassian.listener

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder

/**
 * 创建子任务的时候
 * - 自动更新"需求移交时间"，取当前【需求移交】子任务的最晚完成时间，不管父任务的 ”需求移交时间“ 是否已经填过，都进行更新
 * - 自动更新"预计完成时间"，取当前【上线】   子任务的最晚完成时间，父任务的 ”预计完成时间“ 如果已经有值，不进行更新
 */
// region 代码提示
//Issue issue = ComponentAccessor.issueManager.getIssueObject("COM-202")
// endregion

if (!issue.subTask) {
    return
}

// 父任务
Issue parentIssue = issue.parentObject
// 子任务主题
String summary = issue.getSummary()

/**
 * 自动更新"需求移交时间"，取当前【需求移交】子任务的最晚完成时间
 * 不管父任务的 ”需求移交时间“ 是否已经填过，都进行更新
 */
if (summary.contains("需求移交")) {

    // 获取自定义字段 子任务的（需求移交时间）
    CustomField customField = ComponentAccessor.customFieldManager.getCustomFieldObjectByName("预计完成日期")
    if (null == customField) return

    // 【需求移交】子任务的最晚"预计完成日期"
    def lastTime = lastTime(parentIssue, customField, "需求移交")
    if (null == lastTime) return

    // 获取自定义字段（需求移交时间）
    CustomField prdTimeCustomField = ComponentAccessor.customFieldManager.getCustomFieldObjectByName("需求移交时间")
    if (null == prdTimeCustomField) return

    // 不管父任务的 ”需求移交时间“ 是否已经填过，都进行更新
    prdTimeCustomField.updateValue(null, parentIssue, new ModifiedValue(null, lastTime), new DefaultIssueChangeHolder())
}

/**
 * 自动更新"预计完成时间"，取当前【上线】子任务的最晚完成时间
 * 父任务的 ”预计完成日期“ 如果已经有值，不进行更新
 */
if (summary.contains("上线")) {
    // 获取自定义字段（需求移交时间）
    CustomField customField = ComponentAccessor.customFieldManager.getCustomFieldObjectByName("预计完成日期")
    if (null == customField) return

    // 父任务的 ”预计完成日期“ 如果已经有值，不进行更新
    if (null != parentIssue.getCustomFieldValue(customField)) return "[有值不更新]"

    // 【上线】子任务的最晚完成时间
    def lastTime = lastTime(parentIssue, customField, "上线")
    if (null == lastTime) return

    customField.updateValue(null, parentIssue, new ModifiedValue(null, lastTime), new DefaultIssueChangeHolder())
}

/**
 * 获取 issue 子任务 匹配指定关键字 key 的 最大 CustomField
 */
static def lastTime(Issue issue, CustomField customField, String key) {
    if (issue.subTask) return null
    return issue.subTaskObjects
            .findAll { it -> it.summary.contains(key) }
            .collect { it -> it.getCustomFieldValue(customField) }
            .max()
}
