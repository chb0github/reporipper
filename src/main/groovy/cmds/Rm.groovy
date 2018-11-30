#!/usr/bin/env groovy
package cmds

import scms.Scm
import scms.Project

def execute(Context ctx) {
    def pattern = '([a-zA-Z]+):?([a-zA-Z]+|\\.\\*)?/?([a-zA-Z]+|\\.\\*)?'

    def (scmName, prjPattern, repoPattern) = ctx.args[0].with { it =~ pattern }?.with { it[0][1..3] }
    Scm scm = toscm(scmName)
    Set<Project> projects = scm.getProjects().findAll{ it.key =~ prjPattern }

    projects.collect{
        scm.delProject(it.key)
    }.inject{a,b -> a && b}
}
this.&execute

