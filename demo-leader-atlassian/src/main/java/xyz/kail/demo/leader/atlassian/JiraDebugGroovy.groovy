package xyz.kail.demo.leader.atlassian

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser

ApplicationUser user = ComponentAccessor.jiraAuthenticationContext.user
return user.displayName

