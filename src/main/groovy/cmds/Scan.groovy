package cmds

import scms.Scm
import scms.Project

class Scan extends AbstractCommand<Map<Object,Object>> {

    @Override
    Map<Object,Object> execute(Context ctx) {
        def chosenScm = ctx.args && ctx.args[0]
        if(!chosenScm)
            throw new IllegalArgumentException("No SCM chosen")

        def scms = ctx.config.scm.keySet()
        if(!chosenScm in scms)
            throw new IllegalArgumentException("${chosenScm} invalid. Must be one of ${scms}")

        def lambda = ctx.args[1].find()?.with{
            try {
                evalutate(ctx.args[1])
            }
            catch(Exception e){}
        }
        if(!lambda)
            throw new IllegalArgumentException("No search supplied or ")


        Scm scm = Scm.getScm(chosenScm,ctx)

        scm.repos.find(lambda)
    }


}
