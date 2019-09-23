package org.bongiorno.reporipper.cmds


import org.bongiorno.reporipper.scms.Scm

import java.util.stream.Stream

import static java.util.stream.Collectors.toList

class Grep extends AbstractCommand<List<Map<String, Object>>> {

  @Override
  List<LinkedHashMap<String, ?>> execute(Context ctx) {
    def chosenScm = ctx.args && ctx.args[0] ? ctx.args[0] : null
    if (!chosenScm)
      throw new IllegalArgumentException("No SCM chosen")

    def scms = ctx.config.scm.keySet()
    if (!chosenScm in scms)
      throw new IllegalArgumentException("${chosenScm} invalid. Must be one of ${scms}")


    def scm = Scm.getScm(chosenScm)
    if (!(ctx.args.length > 1 && ctx.args[1]))
      throw new IllegalArgumentException("No search supplied or search expression is invalid")


    ctx.args[1..-1].collect { it.startsWith('file:') ? new File(new URL(it).file).readLines() : [it] }.flatten().parallelStream().distinct().map {
      [
          term  : it,
          result: scm.search(it)
      ]
    }.collect(toList())
  }

  @Override
  String[] getArgs() {
    ['searchArgs', 'repo']
  }

  @Override
  String getDescription() {
    'searches for a given string in the repos supplied'
  }
}
