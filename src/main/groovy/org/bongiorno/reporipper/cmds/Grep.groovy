package org.bongiorno.reporipper.cmds
import org.bongiorno.reporipper.scms.*
class Grep extends AbstractCommand<Object> {

    @Override
    def execute(Context ctx) {
        def chosenScm = ctx.args && ctx.args[0] ? ctx.args[0] : null
        if(!chosenScm)
            throw new IllegalArgumentException("No SCM chosen")

        def scms = ctx.config.scm.keySet()
        if(!chosenScm in scms)
            throw new IllegalArgumentException("${chosenScm} invalid. Must be one of ${scms}")



        if(!(ctx.args.length > 1 && ctx.args[1]))
            throw new IllegalArgumentException("No search supplied or search expression is invalid")


        Scm.getScm(chosenScm).search(ctx.args[1])


    }

    @Override
    String[] getArgs() {
        ['searchArgs','repo']
    }

    @Override
    String getDescription() {
        'searches for a given string in the repos supplied'
    }
}
