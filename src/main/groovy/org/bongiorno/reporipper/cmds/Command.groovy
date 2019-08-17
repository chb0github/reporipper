package org.bongiorno.reporipper.cmds


interface Command<T> {

    T execute(Context ctx)

    String[] getArgs()

    String getDescription()
}