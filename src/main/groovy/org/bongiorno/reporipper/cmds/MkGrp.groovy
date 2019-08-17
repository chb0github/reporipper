package org.bongiorno.reporipper.cmds

import org.bongiorno.reporipper.scms.Scm

class MkGrp extends AbstractCommand<Map<Object,Object>> {
    @Override
    Map<Object,Object> execute(Context ctx) {
        def (scmName,group) = ctx.args[0].with{ it =~ "(${ctx.config.scm.keySet().join('|')}):[a-zA-Z0-9]+" }?.with{ it[0][1..2] }

        if(!group)
            throw new IllegalArgumentException('No group supplied or not alphanumeric')

        Scm scm = scmName ?  Scm.getScm(scmName) : null
        if(!scm)
            throw new IllegalArgumentException("Unknown scm. must be ${ctx.config.scm.keyset().join('|')}")

        throw new UnsupportedOperationException()
//        scm.createGroup(frmPrjName)
    }

    @Override
    String[] getArgs() {
        ['group']
    }

    @Override
    String getDescription() {
        'create a new user group'
    }
}



