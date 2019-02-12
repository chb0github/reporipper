package org.bongiorno.reporipper.scms


import static java.util.concurrent.CompletableFuture.supplyAsync

/**
 * @author cbongiorno on 11/29/18.
 */
abstract class AbstractScm implements Scm {

    String user
    String pass
    String url

    final String name = this.class.simpleName.replaceAll('([A-Z][a-z]+)[A-Z][a-z]+','$1').toLowerCase()

    boolean projectExists(String prjName) {
        new URL("$url/projects/${stashPrj}").exists()
    }

    @Override
    boolean repoExists(String prjName, String repoName) {
        throw new UnsupportedOperationException()
    }

    @Override
    Set<String> getGroups() {
        throw new UnsupportedOperationException()
    }

    @Override
    def addGroupToRepo(String groupSlug, String reposlug, String privilage) {
        throw new UnsupportedOperationException()
    }

    @Override
    def search(String query) {
        throw new UnsupportedOperationException()
    }

    @Override
    Project createProject(String name, String key, String description, InputStream avatar) {
        throw new UnsupportedOperationException()
    }

    @Override
    def createGroup(String groupName) {
        throw new UnsupportedOperationException()
    }

    @Override
    InputStream getProjectAvatar(String prjname) {
        throw new UnsupportedOperationException()
    }

    @Override
    Set<Repository> getRepos(String project) {
        throw new UnsupportedOperationException()
    }

    @Override
    Set<Repository> getRepos() {
        this.projects.take(2).collect{ project ->
            supplyAsync{
                this.getRepos(project)
            }
        }.collect{ it.join() }.flatten()
    }

    @Override
    Repository getRepo(String project, String name) {
        throw new UnsupportedOperationException()
    }

    @Override
    Set<Project> getProjects() {
        throw new UnsupportedOperationException()
    }

    @Override
    Project getProject(String name) {
        throw new UnsupportedOperationException()
    }

    @Override
    boolean delProject(String prjName) {
        throw new UnsupportedOperationException()
    }

    @Override
    Repository createRepo(String prjKey, String repoName, String description) {
        throw new UnsupportedOperationException()
    }
}
