package scms

import groovy.transform.Memoized

import static java.lang.String.format
import static java.util.concurrent.CompletableFuture.supplyAsync
import groovy.transform.InheritConstructors

/**
 * @author cbongiorno on 9/28/18.
 */
@InheritConstructors
class BBScm extends AbstractScm {

    BBScm(){}

    @Override
    @Memoized
    Set<Project> getProjects() {
        def meta = new URL("$url/2.0/teams/twengg/projects/?pagelen=0").withCreds(this.user, this.pass).json().get()
        def body = meta.body
        int pageCount = (body.size / 10) + Math.min((body.size % 10) as int, 1)
        if (!(meta.code in (200..299))) {
            throw new RuntimeException(meta.toString())
        }
        (1..pageCount).collect { page ->
            supplyAsync {
                new URL("$url/2.0/teams/twengg/projects/?page=$page&pagelen=10")
                        .withCreds(this.user, this.pass).json().get().with { it.body?.values }
            }
        }.collect { it.join() }.findAll().flatten().collect {
            new Project(name: it.name, key: it.key, description: it.description)
        } as Set
    }

    @Override
    Repository getRepo(String project, String repoName) {
        new URL("$url/2.0/repositories/twengg/$repoName").withCreds(this.user, this.pass).json().get().with {
            it.code in (200..299) ? it.body : null
        }?.with {
            new Repository(name: it.name, key: it.slug, description: it.description,
                    clone: it.links.clone.collectEntries { [(it.name): it.href] })
        }
    }

    @Override
    boolean repoExists(String prjName, String repoName) {
        new URL("$url/2.0/repositories/twengg/$repoName").withCreds(this.user, this.pass).exists()
    }

    @Override
    Set<Repository> getRepos() {
        getRepos(null)
    }


    @Override
    def search(String query) {
        def formatStr = '%s/2.0/teams/twengg/search/code?search_query=%s&page=%d&pagelen=%d'
        def url = format(formatStr,this.url,query,1,1)
        def meta = new URL(url).withCreds(this.user, this.pass).json().get()
        def body = meta.body
        if (!(meta.code in (200..299))) {
            throw new RuntimeException(meta.toString())
        }
        def pages = (body.size / 10 as int) + Math.min((body.size % 10) as int, 1)
        (1..pages).collect { page ->
            def u = format(formatStr,this.url,query,page,10)
            supplyAsync {
                new URL(u).withCreds(this.user, this.pass).json().get().with { it.body?.values }
            }

        }.collect { it.join() }.findAll().flatten() as Set
    }

    @Override
    @Memoized
    Set<Repository> getRepos(String prj) {
        def usePrj = prj ? format('q=project.key=%s',('"' + prj + '"').encodeURL()) : ''
        def url = format("%s/2.0/repositories/twengg?%s", url, usePrj )
        def meta = new URL(url).withCreds(this.user, this.pass).json().get()


        def body = meta.body
        if (!(meta.code in (200..299))) {
            throw new RuntimeException(meta.toString())
        }
        def pages = (body.size / 10 as int) + Math.min((body.size % 10) as int, 1)
        (1..pages).collect { page ->
            def frmt = '%s/2.0/repositories/twengg?page=%d&pagelen=10&%s'
            def u = format(frmt, url, page, usePrj)
            supplyAsync {
                new URL(u).withCreds(this.user, this.pass).json().get().with { it.body?.values }
            }

        }.collect { it.join() }.findAll().flatten().collect {
            new Repository(name: it.name, key: it.slug, description: it.description,
                    clone: it.links.clone.collectEntries { [(it.name): it.href] })
        } as Set
    }

    InputStream getProjectAvatar(String prjRef) {
        new URL("$url/2.0/${prjRef}/avatar.png").withCreds(this.user, this.pass).binary().get().body
    }

    @Override
    @Memoized
    Set<String> getGroups() {
        new URL("$url/1.0/groups/twengg").withCreds(user, pass).json().get().with {
            it.code in (200..299) ? it.body*.slug as Set : Collections.emptySet()
        }
    }

    @Override
    def addGroupToRepo(String groupSlug, String reposlug, String priv) {
        new URL("$url/1.0/group-privileges/twengg/$reposlug/twengg/${groupSlug.toLowerCase()}")
                .withCreds(user, pass).text().put(priv).with {
            it.code in (200..299) ? it.body : null
        }
    }


    @Override
    def createGroup(String groupName) {
        new URL("$url/1.0/groups/twengg").withCreds(user, pass).text().post("name=$groupName").with {
            it.code in (200..299) ? it.body : null
        }
    }

    @Override
    @Memoized
    Project getProject(String name) {
        new URL("$url/2.0/teams/twengg/projects/$name").withCreds(user, pass).json().get().with {
            it.code in (200..299) ? new Project(name: it.body.name, key: it.body.key, description: it.body.description) : null
        }
    }

    @Override
    Repository createRepo(String prjKey, String repoName, String description) {
        new URL("$url/2.0/repositories/twengg/$repoName/").withCreds(this.user, this.pass).json().put(
                [
                        scm        : "git",
                        project    : [
                                key: prjKey
                        ],
                        is_private : true,
                        description: description
                ]
        ).with {
            it.code in (200..299) ? it.body : null
        }?.with {
            new Repository(name: it.name, key: it.slug, description: it.description,
                    clone: it.links.clone.collectEntries { [(it.name): it.href] })
        }
    }

    @Override
    Project createProject(String name, String key, String description, InputStream avatar) {

        new URL("$url/2.0/teams/twengg/projects/").withCreds(this.user, this.pass).json().post(
                [
                        name       : name,
                        key        : key,
                        description: description,
                        links      : [
                                avatar: [
                                        href: "data:image/png;base64,${avatar.bytes.encodeBase64()}"
                                ]
                        ],
                        is_private : true
                ]
        ).with {
            it.code in (200..299) ? new Project(it.body.name, it.body.key,  it.body.description) : null
        }
    }

    @Override
    String toString() {
        'bb'
    }
}
