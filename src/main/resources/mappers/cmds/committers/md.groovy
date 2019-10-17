package mappers.cmds.committers


{ payload ->
    def columns = ['repo','url','owner name', 'owner email']
    def header = "|${columns.join('|')}|"
    def bar = "|${columns.collect{ it.replaceAll('[a-z ]','-')}.join('|')}.join('|')}|"

    def lines = payload.collect { line ->
        [
            line.repo.replaceAll('(.*)\\.git','$1'),
            line.uri.replaceAll('git@(.*):(.*)', 'https://$1/$2'),
            line.stats.max{ it.lcd }.with{ [it.name, it.email]},
        ].flatten().join('|')
    }.join('\n')
    [header,bar,lines].join('\n')
}