package xyz.kail.demo.leader.atlassian

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

def millis = System.currentTimeMillis()
println(millis)

def jsonSlurper = new JsonSlurper()
def object = jsonSlurper.parseText('{ "name": "John Doe" } /* some comment */')

println(System.currentTimeMillis() - millis)

assert object instanceof Map
assert object.name == 'John Doe'