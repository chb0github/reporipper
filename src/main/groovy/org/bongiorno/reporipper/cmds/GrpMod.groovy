package org.bongiorno.reporipper.cmds


/**
 * @author cbongiorno on 12/6/18.
 */
class GrpMod extends AbstractCommand<Map<String,Object>> {

    @Override
    Map<String,Object> execute(Context ctx) {
        // modify a group
        throw new UnsupportedOperationException()
    }

    @Override
    String[] getArgs() {
        []
    }

    @Override
    String getDescription() {
        'modifies a user group. Currently not implemented'
    }
}
