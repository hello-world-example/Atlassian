package xyz.kail.demo.leader.atlassian;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.bc.project.component.ProjectComponentManager;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueImpl;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.status.SimpleStatus;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserUtil;
import com.opensymphony.workflow.InvalidInputException;

import java.util.Collection;

/**
 * Custom Workflow Functions : https://scriptrunner.adaptavist.com/latest/jira/custom-workflow-functions.html
 * Post Functions Examples： https://scriptrunner.adaptavist.com/latest/jira/recipes/workflow/postfunctions.html
 * -- Set Issue Attributes： https://scriptrunner.adaptavist.com/latest/jira/recipes/workflow/postfunctions/set-issue-attributes.html
 */
public class Main {

    public static void main(String[] args) {
        Issue issue = null;
        IssueImpl issueImpl = null;

        Project projectObject = issue.getProjectObject();
        Collection<ProjectComponent> projectComponents = projectObject.getProjectComponents();
        for (ProjectComponent projectComponent : projectComponents) {
            projectComponent.getName();
            projectComponent.getDescription();
            projectComponent.getComponentLead().getUsername();
        }


        // 是否是子任务
        boolean isSubTask = issue.isSubTask();
        // 父任务
        Issue parentIssue = issue.getParentObject();
        // 子任务
        Collection<Issue> subTasks = parentIssue.getSubTaskObjects();
        for (Issue subTask : subTasks) {
            String summary = subTask.getSummary();
            Status statusObject = subTask.getStatusObject();
            SimpleStatus simpleStatus = statusObject.getSimpleStatus();

            String statusId = simpleStatus.getId();
            String statusName = simpleStatus.getName();

        }

        ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getUser();
        String displayName = user.getDisplayName();
        String name = user.getName();

        IssueService issueService = ComponentAccessor.getIssueService();
        IssueInputParameters issueInputParameters = issueService.newIssueInputParameters();
        issueInputParameters.setComment("");
        issueInputParameters.setResolutionId("1");

        InvalidInputException invalidInputException = new InvalidInputException("name", "error message");


        ProjectComponentManager projectComponentManager = ComponentAccessor.getProjectComponentManager();
        CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
        UserUtil userUtil = ComponentAccessor.getUserUtil();


    }

}
