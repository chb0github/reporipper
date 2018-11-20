package scms

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper


class CacheScm implements Scm {

    private Scm real

    @Override
    Set<Repository> getRepos(String repo) {
        cache(repo,real.&getRepos)
    }

    @Override
    Set<Project> getProjects() {
        cache('projects',real.&getProjects)
    }

    @Override
    Repository getRepo(String project, String name) {
        cache("${project}.${name}", {real.getRepo(project,name)})
    }

    private def cache(String key, Closure f){
        def cache = new File("/tmp/${real.class.simpleName}.cache.$key.json")
        if (cache.exists()) {
            return new JsonSlurper().parse(cache)
        }
        f(key)?.tap { json ->

            new FileWriter("/tmp/${this.real.class.simpleName}.cache.$key.json").tap {
                write(new JsonBuilder(json).toPrettyString())
                flush()
                close()
            }
        }

    }

    @Override
    Set<String> getGroups() {
        cache('getGroups', real.&getGroups)
    }

    @Override
    def addGroupToRepo(String groupSlug, String reposlug, String privilage) {
        real.addGroupToRepo(groupSlug,reposlug,privilage)
    }

    @Override
    Project createProject(String name, String key, String description, InputStream avatar) {
        throw new UnsupportedOperationException()
    }

    @Override
    Repository createRepo(String prjKey, String repoName, String description) {
        real.createRepo(prjKey,repoName,description)
    }

    @Override
    InputStream getProjectAvatar(String prjname) {
        cache("${prjname}.avatar", {real.getProjectAvatar(prjname)})
    }

    @Override
    Project getProject(String name) {
        cache(name,real.&getProject)
    }
}
