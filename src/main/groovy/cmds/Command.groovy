package cmds
/**
 * @author cbongiorno on 10/29/18.
 */
interface Command<T> {

    T execute(Context ctx)

}