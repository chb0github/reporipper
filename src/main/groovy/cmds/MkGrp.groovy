package cmds

import scms.Scm

class MkGrp implements Command {
    def execute(Context ctx) {
        def (scmName,group) = ctx.args[0].with{ it =~ "(${ctx.config.scm.keySet().join('|')}):[a-zA-Z0-9]+" }?.with{
            it[0][1..2] }

        if(!group)
            throw new IllegalArgumentException('No group supplied or not alphanumeric')

        Scm scm = scmName ?  Scm.getScm(scmName,ctx) : null
        if(!scm)
            throw new IllegalArgumentException("Unknown scm. must be ${ctx.config.scm.keyset().join('|')}")


        scm.createGroup(frmPrjName)
    }

}



