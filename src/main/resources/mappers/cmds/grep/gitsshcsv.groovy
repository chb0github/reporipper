package mappers.cmds.grep

import static java.lang.String.format
// yeah, it's hardcoded to BB for now. I know.
{payload ->
  ['term', 'repos'].join(',') + '\n' + payload.collect{
    [it.term, (it.result?.file?.links?.self?.href*.split('/').collect{ format('git@bitbucket.org:%6$s/%7$s.git',it) } as TreeSet).join(' ')].join(',')
  }.join('\n')
}