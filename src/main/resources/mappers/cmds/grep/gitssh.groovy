package mappers.cmds.grep

{payload -> (payload*.file.links.self.href*.split('/').collect{ String.format('git@bitbucket.org:%6$s/%7$s.git',it) } as TreeSet).join('\n')}