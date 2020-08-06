package com.adaptavist

import com.atlassian.confluence.setup.settings.SettingsManager
import com.atlassian.plugin.osgi.util.OsgiHeaderUtil
import com.atlassian.sal.api.component.ComponentLocator
import com.atlassian.sal.api.message.Message
import com.atlassian.sal.api.upgrade.PluginUpgradeTask
import org.osgi.framework.FrameworkUtil

class SetBaseUrlStartupTask implements PluginUpgradeTask {
    @Override
    int getBuildNumber() {
        1
    }

    @Override
    String getShortDescription() {
        "Ensure Confluence's base url matches the JVM System Property."
    }

    @Override
    Collection<Message> doUpgrade() throws Exception {
        def settingsManager = ComponentLocator.getComponent(SettingsManager)

        def currentBaseUrl = settingsManager.globalSettings.baseUrl
        def jvmSystemPropertyBaseUrl = System.getProperty("baseurl")

        if (currentBaseUrl != jvmSystemPropertyBaseUrl) {
            def globalSettings = settingsManager.globalSettings
            globalSettings.baseUrl = jvmSystemPropertyBaseUrl
            settingsManager.updateGlobalSettings(globalSettings)
        }

        null
    }

    @Override
    String getPluginKey() {
        def bundle = FrameworkUtil.getBundle(SetBaseUrlStartupTask)
        OsgiHeaderUtil.getPluginKey(bundle)
    }
}
