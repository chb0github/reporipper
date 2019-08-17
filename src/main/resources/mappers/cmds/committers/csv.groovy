package mappers.cmds.committers


{ payload ->
    def header = ['repo','url','owner name', 'owner email'].join(',')
    def lines = payload.collect { line ->
        [
            line.repo.replaceAll('(.*)\\.git','$1'),
            line.uri.replaceAll('git@(.*):(.*)', 'https://$1/$2'),
            line.stats.max{ it.lcd }.with{ [it.name, it.email]},
        ].flatten().join(',')
    }.join('\n')
    [header,lines].join('\n')
}