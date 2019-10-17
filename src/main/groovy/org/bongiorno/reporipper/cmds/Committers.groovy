package org.bongiorno.reporipper.cmds

import org.bongiorno.reporipper.scms.Repository
import org.bongiorno.reporipper.scms.Scm
@Grab(group = 'org.eclipse.jgit', module = 'org.eclipse.jgit', version = '5.1.3.201810200350-r')
@Grab(group = 'org.slf4j', module = 'slf4j-jdk14', version = '1.7.28')

import org.eclipse.jgit.api.Git

import java.util.stream.Collectors

import static java.util.stream.Collectors.toSet

class Committers extends AbstractCommand<Set<Map<String, Object>>> {

  @Override
  Set<Map<String, Object>> execute(Context ctx) {
    def filter = {true } // make room for a real filter
    Scm scm = Scm.getScm(ctx.args[0])
    Map<Boolean, List<Repository>> repos = scm.args[1..-1]
        .collect { it.startsWith('file:') ? new File(new URL(it).file).readLines() : [it] }
        .flatten().parallelStream().map { scm.getRepo(it as String) }.collect(Collectors.partitioningBy(Objects.&nonNull))

    repos[Boolean.TRUE].parallelStream().map { repo ->
      Git.cloneRepository()
          .setURI(repo.clone.ssh)
          .setRemote('origin')
          .setBare(true)
          .setDirectory(File.createTempDir())
          .setNoCheckout(false)
          .setCloneAllBranches(true).call()
    }.map { git ->
      [
          *: git.remoteList().call()[0].with {
            [
                repo: it.uris[0].path.split('/')[1],
                uri : it.uris.toString()[1..-2]
            ]
          },
          localDir: git.repo.directory,
          stats   : git.log().all().call().findAll(filter).groupBy { it.authorIdent }.collect { author, revs ->
            [
                name : author.name,
                email: author.emailAddress,
                lcd  : revs.max { it.commitTime }.with { ofEpochSecond(it.commitTime) },
                count: revs.size()
            ]
          } as List<Map<String, Object>>

      ]
    }.peek { it.localDir.deleteDir() }.peek { it.remove('localDir') }.collect(toSet())
  }

  @Override
  String[] getArgs() {
    ['repo1', 'file:repolist.txt', '- (stdin)']
  }

  @Override
  String getDescription() {
    'returns a report of committer details for every repo supplied'
  }
}



