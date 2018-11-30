package scms
import groovy.transform.MapConstructor
/**
 * @author cbongiorno on 11/29/18.
 */
@MapConstructor
abstract class AbstractScm implements Scm {

    final String user
    final String pass
    final String url

    boolean projectExists(String prjName){
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
