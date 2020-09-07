package xyz.kail.demo.leader.atlassian.fields

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.comments.Comment
import com.atlassian.jira.issue.fields.CustomField

import java.text.SimpleDateFormat

/**
 * 延期原因
 */
// region 代码提示
Issue issue = ComponentAccessor.issueManager.getIssueObject("ARCH-1")
// endregion 有效

//def join = "\n ===---==--=-=--==---=== \n"
def join = "<br> ===---==--=-=--==---=== <br>"


CustomField delayReasonField = ComponentAccessor.customFieldManager.getCustomFieldObjectByName("@延期原因")
if (null == delayReasonField) return

// 延期原因
String delayReasonText = issue.getCustomFieldValue(delayReasonField)
delayReasonText = null == delayReasonText ? "" : delayReasonText
// 每一个延期原因
String[] delayReasons = delayReasonText.split(join)

/**
 * id,time 前缀一样，说明是更新的，同一天的更新可以互相覆盖
 */
static def findUpdate(String[] delayReasons, String idAndUpdatedPrefix) {
    for (i in 0..<delayReasons.length) {
        if (delayReasons[i].startsWith(idAndUpdatedPrefix)) {
            return i
        }
    }
    return -1
}


List<Comment> comments = ComponentAccessor.commentManager.getComments(issue)

return comments
        .findAll { it -> it.body.contains("延期") }
        .collect { it ->
            def id = it.id
            def time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(it.updated)
            def name = it.updateAuthorFullName
            def body = it.body

            def delay = "$id,$time,$name,$body,$it.created"
            def index = findUpdate(delayReasons, "$id,$time")
            if (index >= 0) {
                delayReasons[index] = delay
                return null
            }
            return delay
        }
        .join(join)

