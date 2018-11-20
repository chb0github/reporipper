package scms
/**
 * @author cbongiorno on 9/28/18.
 */
trait Scm {

    String user
    String pass
    String url

    boolean projectExists(String prjName){
        new URL("$url/projects/${stashPrj}").exists()
    }

    boolean repoExists(String prjName,String repoName){
        throw new UnsupportedOperationException()
    }

    abstract InputStream getProjectAvatar(String prjname)
    Set<String> getGroups(){
        throw new UnsupportedOperationException()
    }

    def addGroupToRepo(String groupSlug, String reposlug, String privilage){
        throw new UnsupportedOperationException()

    }

    def createGroup(String groupName) {
         throw new UnsupportedOperationException()
    }

    abstract Set<Repository> getRepos(String project)
    abstract Repository getRepo(String project, String name)

    abstract Set<Project> getProjects()

    abstract Project getProject(String name)

    Project createProject(String name, String key, String description, InputStream avatar) {
        throw new UnsupportedOperationException()
    }


    abstract Repository createRepo(String prjKey, String repoName, String description)

    public static Scm getInstance(String name, String user, String pass, String url) {
        switch (name) {
            case 'bb': return new BBScm(user:user,pass:pass,url:url) as Scm
            case 'stash': return new StashScm(user:user,pass:pass,url:url) as Scm
        }

    }
}
