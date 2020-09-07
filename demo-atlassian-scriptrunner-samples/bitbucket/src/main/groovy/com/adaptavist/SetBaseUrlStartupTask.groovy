package com.adaptavist

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.atlassian.bitbucket.server.ApplicationPropertiesService
import com.atlassian.bitbucket.user.SecurityService
import com.atlassian.bitbucket.user.UserService
import com.atlassian.bitbucket.util.Operation
import com.atlassian.plugin.osgi.util.OsgiHeaderUtil
import com.atlassian.sal.api.upgrade.PluginUpgradeTask
import groovy.util.logging.Log4j
import org.osgi.framework.FrameworkUtil
import org.slf4j.LoggerFactory

@Log4j
class SetBaseUrlStartupTask implements PluginUpgradeTask {
    ApplicationPropertiesService applicationPropertiesService
    UserService userService
    SecurityService securityService

    SetBaseUrlStartupTask(ApplicationPropertiesService applicationPropertiesService,
                          UserService userService,
                          SecurityService securityService
    ) {
        this.applicationPropertiesService = applicationPropertiesService
        this.userService = userService
        this.securityService = securityService
    }

    @Override
    int getBuildNumber() {
        1
    }

    @Override
    String getShortDescription() {
        "Ensure Bitbucket's base url matches the JVM System Property. Set logging levels to DEBUG."
    }

    @Override
    String getPluginKey() {
        def bundle = FrameworkUtil.getBundle(SetBaseUrlStartupTask)
        OsgiHeaderUtil.getPluginKey(bundle)
    }

    Collection doUpgrade() {
        // Set logging levels to DEBUG
        ["com.onresolve", "com.example"].each {
            def logger = (Logger) LoggerFactory.getLogger(it)
            logger.setLevel(Level.DEBUG)
        }

        def user = userService.getUserByName('admin')
        if (!user) {
            log.warn("User name: admin doesn't exist.")
            [:]
        }

        // Set base URL
        securityService.impersonating(user, "Running script job as $user.slug").call(new Operation() {
            @Override
            Object perform() throws Throwable {
                def currentBaseUrl = applicationPropertiesService.baseUrl
                def jvmSystemPropertyBaseUrl = new URI(System.getProperty("baseurl"))
                if (currentBaseUrl != jvmSystemPropertyBaseUrl) {
                    applicationPropertiesService.baseURL = jvmSystemPropertyBaseUrl
                }
                null
            }
        })

        null
    }
}
