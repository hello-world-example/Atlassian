package xyz.kail.demo.leader.atlassian.fields

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.comments.Comment

import java.text.SimpleDateFormat

/**
 * 延期原因
 */
// region 代码提示
Issue issue = ComponentAccessor.issueManager.getIssueObject("ARCH-1")
// endregion 有效

def join = "\n ===---==--=-=--==---=== \n"
//def join = "<br> ===---==--=-=--==---=== <br>"

List<Comment> comments = ComponentAccessor.commentManager.getComments(issue)

def delayReason = comments
        .findAll { it -> it.body.contains("延期") }
        .collect { it ->
            def id = it.id
            def time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(it.updated)
            def name = it.updateAuthorFullName
            def body = it.body
            return "$id,$time,$name,$body"
        }

if (delayReason.isEmpty()) {
    return null
}

return delayReason.join(join)

