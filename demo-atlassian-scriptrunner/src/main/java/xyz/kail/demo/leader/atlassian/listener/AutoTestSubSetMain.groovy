package xyz.kail.demo.leader.atlassian.listener

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.customfields.option.Option
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder

/**
 * 【创建指定子任务时】，自动设置父任务的 Auto Test 字段内容
 *
 * Three ways to update an issue in Jira Java Api
 * https://community.atlassian.com/t5/Agile-articles/Three-ways-to-update-an-issue-in-Jira-Java-Api/ba-p/736585
 */
// region 代码提示
Issue issue = ComponentAccessor.issueManager.getIssueObject("ARCH-1")
//Issue issue = event.issue
// endregion

// 只针对子任务
if (!issue.subTask) {
    return
}

// 子任务关键字任务关键字
def subSummary = issue.summary
if (!subSummary.contains("【设计自动化用例】") && !subSummary.contains("【自动化测试】")) {
    return
}

// 获取自定义字段
CustomField autoTestField = ComponentAccessor.customFieldManager.getCustomFieldObjectByName("Auto Test")
if (null == autoTestField) {
    return
}

// 获取 Parent 自定义字段的值
Issue parentIssue = issue.parentObject
def parentAutoTestValue = parentIssue.getCustomFieldValue(autoTestField)

// return parentAutoTestValue

List<Option> options = getOptions(parentIssue, autoTestField, ["Y", "N"])
if (options.size() != 2) {
    return
}

ModifiedValue modifiedValue = null
if (subSummary.contains("【设计自动化用例】")) {
    Option option = getOptions(parentIssue, autoTestField, ["N"]).get(0)
    modifiedValue = new ModifiedValue(parentAutoTestValue, option)
} else if (subSummary.contains("【自动化测试】")) {
    Option option = getOptions(parentIssue, autoTestField, ["Y"]).get(0)
    modifiedValue = new ModifiedValue(parentAutoTestValue, option)
}
if (null == modifiedValue) {
    return
}

// 更新 Parent 字段值
autoTestField.updateValue(null, parentIssue, modifiedValue, new DefaultIssueChangeHolder())

// 查找有 【测试任务/测试执行】 的子任务，并赋值
parentIssue.subTaskObjects.each { subTask ->
    if (subTask.summary.contains("【测试任务】") || subTask.summary.contains("【测试执行】")) {
        autoTestField.updateValue(null, subTask, modifiedValue, new DefaultIssueChangeHolder())
    }
}

/**
 *
 */
static List<Option> getOptions(Issue issue, CustomField customField, List<String> optionList) {
    def config = customField.getRelevantConfig(issue)
    def options = ComponentAccessor.getOptionsManager().getOptions(config)
    return options.findAll { it.value in optionList }
}