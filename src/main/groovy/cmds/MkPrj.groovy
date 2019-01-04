package cmds
import scms.Scm

class MkPrj implements Command {
    def execute(Context ctx) {
        def (scmName,key,name,description,avatar) = ctx.args[0]
                .with{ it =~ "(${ctx.config.scm.keySet().join('|')}):[a-zA-Z0-9-]+/[[a-zA-Z0-9-]+" }?.with{ it[0][1..4] }
        Scm scm = Scm.getScm(scmName,ctx)

        if(!project)
            throw new IllegalArgumentException('No project supplied or not alphanumeric')

        if(!repo)
            throw new IllegalArgumentException('No repo supplied or not alphanumeric')


        scm.createProject(key,name,description, null)
    }
}