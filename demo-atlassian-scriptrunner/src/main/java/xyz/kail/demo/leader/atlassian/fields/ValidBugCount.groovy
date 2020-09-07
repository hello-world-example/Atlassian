package xyz.kail.demo.leader.atlassian.fields


import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.link.LinkCollection
import com.atlassian.jira.security.groups.GroupManager

/**
 * 有效 Bug 数统计
 */
// region 代码提示
Issue issue = ComponentAccessor.issueManager.getIssueObject("ARCH-1")
// endregion 有效


// 非法的组
Collection<String> illegalGroups = ["jira-software-users"]
// 非法的 User
Set<String> illegalUserNames = [] as HashSet<String>

// 经办人是指定的组，判断为 非有效 Bug
GroupManager groupManager = ComponentAccessor.groupManager
illegalGroups.each { it ->
    groupManager.getUsersInGroup(it).each { user -> illegalUserNames.add(user.name) }
}
// return illegalUserNames

// Bug 项目 Key
def bugProjectKeys = []
def bugCount = 0D

LinkCollection linkCollection = ComponentAccessor.issueLinkManager.getLinkCollectionOverrideSecurity(issue)
for (Issue linkIssue : linkCollection.allIssues) {
    // Bug 如果分配给测试人员，认为是无效 Bug
    if (illegalUserNames.contains(linkIssue.assigneeUser.name)) {
        continue
    }
    // 链接 对应的项目
    def linkProject = linkIssue.projectObject
    if (!bugProjectKeys.isEmpty() && bugProjectKeys.contains(linkProject.key) || linkProject.name.contains("【QA】")) {
        bugCount++
    }
}
return bugCount

