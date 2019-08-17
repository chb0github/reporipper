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
    def format = opts.remove('-format')?.with { mapper ->
        mapper[0] == '{' ? evaluate(mapper as String) : this.class.getResource("mappers/cmds/${command.name}/${mapper}.groovy")?.with {evaluate(it.file as File)}
    } ?: { it.toJson() }
    function = command.&execute >> format >> this.&println
    function(new Context(args, opts, chosenSwitches, config))

} else {
    err.println(System.properties['sun.java.command'])
    err.println("Unknown command '${cmd}'")
    err.println("Possible cmds are: ${ServiceLoader.load(Command.class).join('\n')}")
    exit(-1)
}
/*
def cli = new CliBuilder(usage:'ant [options] [targets]', header:'Options:')
cli.help('print this message')
cli.format(
        args:1,
        argName:'lambda',
        '''
        may be of the forms: someFilter (uses a built in mapper) \'file:lambda.groovy\\'  or, may be a direct lambda expression as \\'{it ->  it' +
        '.someField}\'
''')
cli.filter(args:1, argName: 'filter','''
        may be of the forms: someFilter (uses a built in mapper) \'file:lambda.groovy\\'  or, may be a direct lambda expression as \\'{it ->  it' +
        '.someField}\'
''')
ServiceLoader.load(Command.class).each {
    cli.option(
            [args:it.args.length, argName:it.args.join(' '), longOpt: it.name],
            it.class,
            it.description
    )
}
def options = cli.parse(args)
cli.usage()
 */