package cmds
/**
 * @author cbongiorno on 10/29/18.
 */
trait Command {


    abstract def execute(Context ctx)

    String getName() {
        this.class.simpleName.toLowerCase()
    }
}