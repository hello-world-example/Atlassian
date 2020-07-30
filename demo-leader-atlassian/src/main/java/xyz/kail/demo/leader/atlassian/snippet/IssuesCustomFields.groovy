package xyz.kail.demo.leader.atlassian.snippet

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.fields.CustomField

/**
 * 自定义字段信息
 */

Issue issue = ComponentAccessor.issueManager.getIssueObject("ARCH-1")
CustomFieldManager customFieldManager = ComponentAccessor.customFieldManager;

List<CustomField> customFieldList = customFieldManager.getCustomFieldObjects(issue)

StringBuilder result = new StringBuilder();
for (CustomField customField : customFieldList) {
    result
            .append(customField.id).append(",")
            .append(customField.idAsLong).append(",")
            .append(customField.name).append(",")
            .append(customField.fieldName).append(",")
            .append(customField.description).append(",")
            .append(customField.customFieldType.name).append(",")
            .append("<br>")
}
return result.toString()



