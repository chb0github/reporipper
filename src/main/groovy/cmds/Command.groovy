package cmds

/**
 * @author cbongiorno on 10/29/18.
 */
enum Command {

    LS{
        @Override
        def execute(String[] args, Map<String, String> options, Set<String> switches, Map<String, ?> config) {
            return null
        }
    },
    CP{
        @Override
        def execute(String[] args, Map<String, String> options, Set<String> switches, Map<String, ?> config) {
            return null
        }
    }
    static Command get(String name){
        values().find {it.name() == name}
    }
    abstract def execute(String[] args, Map<String,String> options, Set<String> switches, Map<String,?> config)
}