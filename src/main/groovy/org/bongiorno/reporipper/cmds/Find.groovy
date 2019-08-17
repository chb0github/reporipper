package org.bongiorno.reporipper.cmds

import org.bongiorno.reporipper.scms.Scm

class Find extends AbstractCommand<Collection<Map<String,Object>>> {

    @Override
    Collection<Map<String,Object>> execute(Context ctx) {
        def chosenScm = ctx.args && ctx.args[0] ? ctx.args[0] : null
        if(!chosenScm)
            throw new IllegalArgumentException("No SCM chosen")

        def scms = ctx.config.scm.keySet()
        if(!chosenScm in scms)
            throw new IllegalArgumentException("${chosenScm} invalid. Must be one of ${scms}")


        if(!(ctx.args.length > 1 && ctx.args[1]))
            throw new IllegalArgumentException("No search supplied or search expression is invalid")

        def filter = new GroovyShell(this.class.classLoader).evaluate(ctx.args[1] as String)

        Scm.getScm(chosenScm,ctx).repos.findAll(filter)
    }

    @Override
    String[] getArgs() {
        ['filter']
    }

    @Override
    String getDescription() {
        'find all repos matching the given filter'
    }
}
