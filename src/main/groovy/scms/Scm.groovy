package scms
/**
 * @author cbongiorno on 9/28/18.
 */
interface Scm {



    boolean repoExists(String prjName,String repoName)

    Set<String> getGroups()

    def addGroupToRepo(String groupSlug, String reposlug, String privilage)

    Project createProject(String name, String key, String description, InputStream avatar)

    def createGroup(String groupName)
    InputStream getProjectAvatar(String prjname)

    Set<Repository> getRepos(String project)
    Repository getRepo(String project, String name)

    Set<Project> getProjects()

    Project getProject(String name)

    boolean delProject(String prjName)


    Repository createRepo(String prjKey, String repoName, String description)

//    public static Scm getInstance(String name, String user, String pass, String url) {
//        switch (name) {
//            case 'bb': return new BBScm(user:user,pass:pass,url:url) as Scm
//            case 'stash': return new StashScm(user:user,pass:pass,url:url) as Scm
//        }
//
//    }
}
