grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.plugin.location.'cn-bioportal-connector'  = '../CnBioPortalConnector'
grails.plugin.location.'cn-nif-connector'		 = '../CnNifConnector'

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility
    repositories {
        grailsCentral()
        mavenRepo "http://repository.codehaus.org/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
        // runtime 'mysql:mysql-connector-java:5.1.21'
    }

    plugins {
        build(":tomcat:$grailsVersion",
              ":release:2.2.0",
              ":rest-client-builder:1.0.3") {
            export = false
        }
			  
		runtime ":hibernate:$grailsVersion"
		compile ":rest:0.8"
    }
}
