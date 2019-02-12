package org.bongiorno.reporipper.cmds
import org.bongiorno.reporipper.scms.Scm
import org.bongiorno.reporipper.scms.Project


class Rm extends AbstractCommand<Map<Object,Object>> {

    @Override
    Map<Object,Object> execute(Context ctx) {
        Class.forName(Scm.class.name)
        def pattern = '([a-zA-Z]+):?([a-zA-Z]+|\\.\\*)?/?([a-zA-Z]+|\\.\\*)?'

        def (scmName, prjPattern, repoPattern) = ctx.args[0].with { it =~ pattern }?.with { it[0][1..3] }
        Scm scm = Scm.getScm(scmName,ctx)

        Set<Project> projects = scm.getProject(prjPattern)?.with{ [it] as Set } ?: scm.getProjects().findAll{ it.key =~ prjPattern }

        projects.collect{ scm.delProject(it.key) }
    }


}
