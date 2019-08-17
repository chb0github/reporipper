package org.bongiorno.reporipper.cmds
import org.bongiorno.reporipper.scms.*

class MkRepo extends AbstractCommand<Map<Object,Object>> {
    Map<Object,Object> execute(Context ctx) {
        def (scmName,project,repo,description) = ctx.args[0]
                .with{ it =~ "(${ctx.config.scm.keySet().join('|')}):[a-zA-Z0-9-]+/[[a-zA-Z0-9-]+" }?.with{ it[0][1..4] }
        Scm scm = toscm(scmName)

        if(!project)
            throw new IllegalArgumentException('No project supplied or not alphanumeric')

        if(!repo)
            throw new IllegalArgumentException('No repo supplied or not alphanumeric')

        scm.createRepo(project,repo,description ?: '')
    }

    @Override
    String[] getArgs() {
        ['project','repo','description']
    }

    @Override
    String getDescription() {
        'creates a new repo under the given project'
    }
}



