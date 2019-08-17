package org.bongiorno.reporipper.cmds
@Grab(group = 'org.eclipse.jgit', module = 'org.eclipse.jgit', version = '5.1.3.201810200350-r')

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.TextProgressMonitor
import org.eclipse.jgit.transport.URIish
import org.bongiorno.reporipper.scms.Repository
import org.bongiorno.reporipper.scms.Scm


import static java.util.concurrent.CompletableFuture.supplyAsync

class Cp extends AbstractCommand<Map<String,Object>> {

    @Override
    Map<String,Object> execute(Context ctx) {
        def pattern = '([a-zA-Z]+):?([a-zA-Z]+|\\.\\*)?/?([a-zA-Z]+|\\*)?'


        def (frmScmName, frmPrjName, fromRepoName) = ctx.args[0].with { it =~ pattern }?.with { it[0][1..3] }
        def (toScmName, toPrjName, toRepoName) = ctx.args[1].with { it =~ pattern }?.with { it[0][1..3] }


        Scm frmScm = Scm.getScm(frmScmName,ctx)
        Scm toScm = Scm.getScm(toScmName,ctx)

        toRepoName = toRepoName ?: fromRepoName
        toPrjName = toPrjName ?: frmPrjName


        def frmPrj = frmScm.getProject(frmPrjName)
        if (!frmPrj) {
            throw new RuntimeException('Source project not found')
        }
        def avatar = frmScm.getProjectAvatar(frmPrj.key)

        def destPrj = toScm.getProject(toPrjName) ?: toScm.createProject(toPrjName, toPrjName, frmPrj.description, avatar)
        Set<Repository> frmRepos = frmScm.getRepos(frmPrj.key)

        if(!(frmPrjName in toScm.getGroups())){
            toScm.createGroup(frmPrjName)
        }
        Set<Repository> remotes = toScm.getRepos(destPrj.key)
        def remoteKeys = remotes*.key as Set

        Set<Repository> toRepos  = frmRepos - frmRepos.findAll{ it.key in  remoteKeys }

        println("Number of missing repos:${toRepos.size()}")


        toRepos.collect { srcRepo ->
            supplyAsync{
                def destRepo = toScm.createRepo(frmPrj.key, srcRepo.key, srcRepo.description)
                def groupStatus = toScm.addGroupToRepo(frmPrjName, destRepo.key, 'admin')
                // This structure needs to be encapsulated so that it can be portable
                String fromRef = srcRepo.clone.ssh
                String toRef = destRepo.clone.ssh
                System.err.println("copying ${fromRef} -> ${toRef}")

                def copyStatus = copyRepo(fromRef, toRef)


                [
                        from  : [
                                name: srcRepo.key,
                                uri : fromRef
                        ],
                        to    : [
                                name: destRepo.key,
                                uri : toRef
                        ],
                        message: copyStatus.message
                ]
            }
        }.collect{it.get()}.split{ !it.message }.with{ pass, fail -> [
                good:pass,
                bad:fail
        ]}

    }


    private def copyRepo(String srcUri, String destUri) {
        boolean success = false
        def tmpDir = File.createTempDir()
        def message = null

        try {
            Git git = Git.cloneRepository()
                    .setURI(srcUri)
                    .setRemote('origin')
                    .setBare(true)
                    .setDirectory(tmpDir)
                    .setNoCheckout(false)
                    .setCloneAllBranches(true).call()

            git.remoteAdd().tap {
                setName('other')
                setUri(new URIish(destUri))
            }.call()

            git.push().setRemote('other').setPushTags().setPushAll().setProgressMonitor(new TextProgressMonitor()).call()
            success = true
        }
        catch (Exception rte) {
            success = false
            message = rte.message
        }
        finally {
            tmpDir.deleteDir()

        }
        return [
                message: message,
                success: success
        ]
    }

    @Override
    String[] getArgs() {
        ['from scm:repo', 'to scm:repo']
    }

    @Override
    String getDescription() {
        'copies from the source scm:repo to the "to" scm:repo. This includes branches, tags and avatars (if possible)'
    }
}



