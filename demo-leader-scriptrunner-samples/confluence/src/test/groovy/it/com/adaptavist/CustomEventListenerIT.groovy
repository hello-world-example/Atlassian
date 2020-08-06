package it.com.adaptavist

import com.atlassian.confluence.spaces.SpaceManager
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal
import com.atlassian.sal.api.component.ComponentLocator
import com.onresolve.scriptrunner.canned.common.admin.SrSpecification
import spock.lang.Shared

class CustomEventListenerIT extends SrSpecification {

    @Shared
    SpaceManager spaceManager = ComponentLocator.getComponent(SpaceManager)

    def "space description updates upon space creation"() {
        setup:
        def spaceKey = "FOO"
        def initialDescription = "initial"
        def expectedDescription = "foo bar description"

        when:
        def space = spaceManager.createSpace(spaceKey, "MYSPACE", initialDescription, AuthenticatedUserThreadLocal.get())
        spaceManager.saveSpace(space)

        then:
        spaceManager.getSpace(spaceKey).description.bodyAsString == expectedDescription
    }
}
