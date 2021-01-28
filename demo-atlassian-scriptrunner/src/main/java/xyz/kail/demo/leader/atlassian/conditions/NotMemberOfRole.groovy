package xyz.kail.demo.leader.atlassian.conditions

import com.atlassian.crowd.embedded.api.User
import com.atlassian.jira.component.ComponentAccessor

/**
 * 当前用户非 '角色名称' 时可以操作
 */
User currentUser = ComponentAccessor.jiraAuthenticationContext.user.directoryUser
return !ComponentAccessor.groupManager.isUserInGroup(currentUser.name, "天天拍车测试部")
