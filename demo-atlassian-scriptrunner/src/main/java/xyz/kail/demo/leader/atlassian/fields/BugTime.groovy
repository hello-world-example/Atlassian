package xyz.kail.demo.leader.atlassian.fields

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.fields.CustomField

import java.sql.Timestamp

/**
 * 计算 Bug 处理耗时
 *
 * Bug解决时间 - 创建时间 精确到小时 并 四舍五入
 */

// region 代码提示
Issue issue = ComponentAccessor.issueManager.getIssueObject("ARCH-1")
// endregion

// 子任务不统计，只针对主任务
if (issue.subTask) return

// 只针对匹配 【线上缺陷管理】与【QA】的项目
String projectName = issue.projectObject.name
if (!projectName.contains("【线上缺陷管理】") && !projectName.contains("【QA】")) return

// Bug解决时间 CustomField
CustomField bugEndTimeField = ComponentAccessor.customFieldManager.getCustomFieldObjectByName("Bug解决时间")
if (null == bugEndTimeField) return

// Bug解决时间 Value（如果未填写，返回 666666D）
def bugEndTime = issue.getCustomFieldValue(bugEndTimeField)
if (null == bugEndTime) return 666666D

// 时间差值
if (bugEndTime instanceof Timestamp) {
    return ((bugEndTime.getTime() - issue.created.getTime()) / 1000 / 60 / 60) as Double
}
return -999D


