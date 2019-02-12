package org.bongiorno.reporipper.cmds


interface Command<T> {

    T execute(Context ctx)

}