package org.bongiorno.reporipper.cmds


/**
 * @author cbongiorno on 1/4/19.
 */
abstract class AbstractCommand<T> implements Command<T> {

    String getName() {
        this.class.simpleName.toLowerCase()
    }
    @Override
    String toString() {
        getName()
    }
}
