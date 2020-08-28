package xyz.kail.demo.leader.atlassian.listener

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.issue.IssueEvent
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import org.ofbiz.core.entity.GenericValue

/**
 * 【当主任务更新的时候】【测试任务】子任务 Auto Test 自动 设置为 父任务的 Auto Test 字段内容
 */
// region 代码提示
IssueEvent event = null
// Issue issue = ComponentAccessor.issueManager.getIssueObject("ARCH-1")
// endregion

GenericValue changeLog = event.getChangeLog()
if (null == changeLog) return


Issue issue = event.issue
// 只针对主任务
if (issue.subTask) return

// 获取变动的字段
List<GenericValue> childChangeItem = changeLog.getRelated("ChildChangeItem")
if (null == childChangeItem || childChangeItem.isEmpty()) return

// Auto Test 字段是否变动
GenericValue change = childChangeItem.find { it -> it.get("field") == "Auto Test" }
if (null == change) return

// 自定义字段是否存在
CustomField autoTestField = ComponentAccessor.customFieldManager.getCustomFieldObjectByName("Auto Test")
if (null == autoTestField) return

// 查找有 【测试任务】 的子任务，并赋值
issue.subTaskObjects.each { subTask ->
    if (subTask.summary.contains("【测试任务】")) {
        // 获取 父任务的字段值
        def parentTestValue = issue.getCustomFieldValue(autoTestField)
        def subTestValue = subTask.getCustomFieldValue(autoTestField)

        // 更新 Parent 字段值
        def modifiedValue = new ModifiedValue(subTestValue, parentTestValue)
        autoTestField.updateValue(null, subTask, modifiedValue, new DefaultIssueChangeHolder())
    }
}
