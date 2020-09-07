package com.adaptavist

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.plugin.osgi.util.OsgiHeaderUtil
import com.atlassian.sal.api.message.Message
import com.atlassian.sal.api.upgrade.PluginUpgradeTask
import org.osgi.framework.Bundle
import org.osgi.framework.FrameworkUtil

import static com.atlassian.jira.config.properties.APKeys.JIRA_BASEURL

class SetBaseUrlStartupTask implements PluginUpgradeTask {
    @Override
    int getBuildNumber() {
        1
    }

    @Override
    String getShortDescription() {
        "Ensure Jira's base url matches the JVM System Property"
    }

    @Override
    Collection<Message> doUpgrade() throws Exception {
        def applicationProperties = ComponentAccessor.applicationProperties

        def currentBaseUrl = applicationProperties.getString(JIRA_BASEURL)
        def jvmSystemPropertyBaseUrl = System.getProperty("baseurl")

        if (currentBaseUrl != jvmSystemPropertyBaseUrl) {
            applicationProperties.setString(JIRA_BASEURL, jvmSystemPropertyBaseUrl)
        }

        null
    }

    @Override
    String getPluginKey() {
        Bundle bundle = FrameworkUtil.getBundle(SetBaseUrlStartupTask)
        OsgiHeaderUtil.getPluginKey(bundle)
    }
}
