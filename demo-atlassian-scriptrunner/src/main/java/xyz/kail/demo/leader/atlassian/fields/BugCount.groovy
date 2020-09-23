package xyz.kail.demo.leader.atlassian.fields

/**
 * Bug 数统计
 */

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.link.LinkCollection

// region 代码提示
// Issue issue = ComponentAccessor.issueManager.getIssueObject("ARCH-1")
// endregion

def bugProjectKeys = []
def bugCount = 0D

LinkCollection linkCollection = ComponentAccessor.issueLinkManager.getLinkCollectionOverrideSecurity(issue)
for (Issue linkIssue : linkCollection.allIssues) {
    def linkProject = linkIssue.projectObject
    if (!bugProjectKeys.isEmpty() && bugProjectKeys.contains(linkProject.key) || linkProject.name.contains("【QA】") ) {
        bugCount++
    }
}
return bugCount

