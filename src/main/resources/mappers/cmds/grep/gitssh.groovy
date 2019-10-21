package mappers.cmds.grep

import groovy.json.JsonBuilder

import static java.lang.String.format
// yeah, it's hardcoded to BB for now. I know.
{payload ->
  new JsonBuilder(payload.collect{ [
      term: (it.term),
      repos: (it.result?.file?.links?.self?.href*.split('/').collect{ format('git@bitbucket.org:%6$s/%7$s.git',it) } as TreeSet)
  ] }).toPrettyString()
}