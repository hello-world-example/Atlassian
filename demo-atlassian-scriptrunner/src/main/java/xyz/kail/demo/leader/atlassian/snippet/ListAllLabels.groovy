package xyz.kail.demo.leader.atlassian.snippet

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.label.Label
import com.atlassian.jira.project.Project
import com.atlassian.jira.project.ProjectManager

/**
 * 遍历 所有项目 的 所有issue， 获取 Label 字段数据
 *
 * @kail
 */

ProjectManager projectManager = ComponentAccessor.projectManager
IssueManager issueManager = ComponentAccessor.issueManager


// 项目列表
//Project project = ComponentAccessor.projectManager.getProjectObjByKey("ARCH")
//List<Project> projects = ComponentAccessor.projectManager.getProjectObjects();
//return projects

Set<String> labels = new HashSet<>()
List<Project> projects = projectManager.getProjectObjects();
for (Project project : projects) {
    //Project project = ComponentAccessor.projectManager.getProjectObjByKey("ARCH")
    Collection<Long> issueIds = issueManager.getIssueIdsForProject(project.id)
    for (Long issueId : issueIds) {
        MutableIssue issueObject = issueManager.getIssueObject(issueId)
        for (Label label : issueObject.labels) {
            labels.add(label.label)
        }
    }
}

return labels

// issue 个数
//return ComponentAccessor.issueManager.issueCount

// labels 信息
//Field lablesField = ComponentAccessor.fieldManager.getField("labels")
//return "$lablesField.id, $lablesField.name, $lablesField.nameKey, "
