package xyz.kail.demo.leader.atlassian.listener

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder

import java.sql.Timestamp

/**
 * 【任务 Done 的时候自动设置 "Bug解决时间" 】
 *
 * Three ways to update an issue in Jira Java Api
 * https://community.atlassian.com/t5/Agile-articles/Three-ways-to-update-an-issue-in-Jira-Java-Api/ba-p/736585
 */
// region 代码提示
Issue issue = ComponentAccessor.issueManager.getIssueObject("ARCH-1")
//Issue issue = event.issue
// endregion

// 子任务不统计，只针对主任务
if (issue.subTask) return

// 只针对匹配 【线上缺陷管理】与【QA】的项目
String projectName = issue.projectObject.name
if (!projectName.contains("【线上缺陷管理】") && !projectName.contains("【QA】")) return

// 当时状态不是是 关闭、done 的时候，直接返回
def endStatus = ["关闭", "done", "已修复"] as Set
def statusText = issue.statusObject.name.toLowerCase()
if (!endStatus.contains(statusText)) return

// 获取自定义字段（Bug解决时间）
CustomField autoTestField = ComponentAccessor.customFieldManager.getCustomFieldObjectByName("Bug解决时间")
if (null == autoTestField) return

// 获取自定义字段的值
def bugTimeValue = issue.getCustomFieldValue(autoTestField)
if (null != bugTimeValue) return

// 如果当前字段没有值，设置为当前时间
autoTestField.updateValue(null, issue, new ModifiedValue(null, new Timestamp(System.currentTimeMillis())), new DefaultIssueChangeHolder())
