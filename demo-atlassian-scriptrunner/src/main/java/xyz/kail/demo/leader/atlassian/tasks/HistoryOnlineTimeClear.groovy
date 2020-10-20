package xyz.kail.demo.leader.atlassian.tasks


import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.web.bean.PagerFilter

/**
 * How to get issue object from search results? : https://community.atlassian.com/t5/Jira-questions/How-to-get-issue-object-from-search-results/qaq-p/1355620
 *
 * 历史数据跑批
 *
 * - 自动更新"需求移交时间"，取当前【需求移交】子任务的最晚完成时间，不管父任务的 ”需求移交时间“ 是否已经填过，都进行更新
 * - 自动更新"预计完成时间"，取当前【上线】   子任务的最晚完成时间，父任务的 ”预计完成时间“ 如果已经有值，不进行更新
 *
 * @see xyz.kail.demo.leader.atlassian.ChangeOnSubTaskCreate*
 */

ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getUser()

String jql = 'issuetype in standardIssueTypes() AND createdDate > 2020-01-01'

SearchService searchService = ComponentAccessor.getComponent(SearchService.class)
SearchService.ParseResult parseResult = searchService.parseQuery(user.directoryUser, jql)

if (parseResult.isValid()) {
    def searchResult = searchService.search(user.directoryUser, parseResult.getQuery(), PagerFilter.getUnlimitedFilter())
    searchResult.issues.collect {
        if (!it.subTaskObjects.isEmpty()) {
            changeOnSubTaskCreate(it, "上线")
            changeOnSubTaskCreate(it, "需求移交")
            return it.key
        }
        return ""
    }.join(",")
}

static def changeOnSubTaskCreate(Issue parentIssue, String summary) {

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