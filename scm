#!/usr/bin/env groovy -cp ./src/main/groovy:./src/main/resources
import org.bongiorno.reporipper.cmds.Context
import org.bongiorno.reporipper.cmds.Command
import groovy.json.JsonSlurper

import static java.lang.System.err
import static java.lang.System.exit


def config = new JsonSlurper().parse(new File('.scm.json'))

def cmd = args[0]
args = args.drop(1)

def opts = (args - args.findAll { it =~ '--.*' }).split { it.startsWith('-') }.transpose().collectEntries()
args = args - opts.keySet() - opts.values()
def chosenSwitches = args.findAll { it =~ '--.*' } as Set
args = args - chosenSwitches


Command command = ServiceLoader.load(Command.class).find { it.name == cmd }
if (command) {
    def args = binding.variables.remove('args') as String[]
    def format = opts.remove('-format')?.with { format -> evaluate(format as String) } ?: { it.toJson() }
    function = command.&execute >> format >> this.&println
    function(new Context(args, opts, chosenSwitches, config))

} else {
    err.println(System.properties['sun.java.command'])
    err.println("Unknown command '${cmd}'")
    err.println("Possible commands are: ${ServiceLoader.load(Command.class).join(',')}")
    exit(-1)
}