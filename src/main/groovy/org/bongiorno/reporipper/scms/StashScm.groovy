package org.bongiorno.reporipper.scms

import groovy.transform.Memoized

import static java.util.concurrent.CompletableFuture.supplyAsync
import groovy.transform.InheritConstructors
/**
 * @author cbongiorno on 9/28/18.
 */
@InheritConstructors
class StashScm extends AbstractScm {

    StashScm(){}

    @Override
    @Memoized
    Set<Project> getProjects() {

        def result = [] as LinkedHashSet
        def page = 0
        for (def body = [isLastPage: false]; !body.isLastPage; page = body.nextPageStart) {
            def tmp = new URL("$url/projects?start=$page").withCreds(this.user, this.pass).json().get()

            body = tmp.body
            if(tmp.code in (200..299))
                result += body.values.collect{  new Project(name:it.name,key:it.key,description:it.description) } as Set
        }
        result
    }

    @Override
    boolean delProject(String prjName) {
        def results = getRepos(prjName).collect{ repo ->
            supplyAsync{
                new URL("$url/projects/$prjName/repos/${repo.key}").withCreds(this.user, this.pass).json().delete()
            }
        }.collect{ it.join() }.split { it.code in (200..299) }.with{ pass,fail ->
            [
                    pass: pass.collectEntries { [project: it.url] },
                    fail: fail.collectEntries{ [
                            project : it.url,
                            status: it.code
                    ]}
            ]
        }
        def prjDeleted = new URL("$url/projects/$prjName").withCreds(this.user,this.pass).json().delete()
        [
                project: prjDeleted.url,
                *:results
        ]
    }

    @Override
    Repository createRepo(String prjKey, String repoName, String description) {
        throw new UnsupportedOperationException()
    }

    def getPermissions(String project) {
        def users = supplyAsync{
            new URL("$url/projects/$project/permissions/users?limit=50")
                    .withCreds(this.user, this.pass).json().get().with{ it.body }
        }
        def groups = supplyAsync{
            new URL("$url/projects/$project/permissions/groups?limit=50")
                    .withCreds(this.user, this.pass).json().get().with{ it.body }
        }
        [
                users: users.join().values*.user,
                groups: groups.join().values.collect{
                    [
                            name: it.group.name,
                            permission: it.permission,
                    ]
                }
        ]

    }

    @Override
    Repository getRepo(String project, String name) {
        new URL("$url/projects/${project}/repos/$name").withCreds(this.user, this.pass).json().get().with{
            it.code in (200..299) ? new Repository(name:it.body.slug,key:it.body.key,description:it.body.description) : null
        }
    }

    @Override
    @Memoized
    Set<Repository> getRepos(String stashPrj) {

        Set<Repository> result = [] as LinkedHashSet
        def page = 0
        for (def body = [isLastPage: false]; !body.isLastPage; page = body.nextPageStart) {
            def tmp = new URL("$url/projects/${stashPrj}/repos?start=${page ?: 0}").withCreds(this.user, this.pass)
                    .json().get()

            body = tmp.body
            if(tmp.code in (200..299)) {
                result += body.values.collect {
                    new Repository(name: it.name, key: it.slug, clone: it.links.clone.collectEntries {
                        [(it.name): it.href]
                    })
                }
            }
        }
        result
    }

    @Override
    InputStream getProjectAvatar(String prjname) {
        new URL("$url/projects/${prjname}/avatar.png").withCreds(this.user, this.pass).binary().get().with{ it.body}
    }


    @Override
    @Memoized
    Project getProject(String name) {
        new URL("$url/projects/$name").withCreds(user, pass).json().get().with {
            code in (200..299) ? new Project(name:it.body.name,key:it.body.key,description:it.body.description) : null
        }
    }

    @Override
    String toString() {
        'stash'
    }
}



