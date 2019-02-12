package org.bongiorno.reporipper.scms
/**
 * @author cbongiorno on 9/28/18.
 */
interface Scm {

    static def getScm = { name,ctx ->
            String user = ctx.config.scm[name]?.user ?: ctx.options['-user'] ?: System.console().readLine('username > ')
            String pass = ctx.config.scm[name]?.pass ?: ctx.options['-pass'] ?: System.console().readPassword('password > ')
            URL url = (ctx.config.scm[name].url ?: ctx.options['-url']).with { u -> new URL(u as String) }
            ServiceLoader.load(Scm.class).find { it.toString() == name }?.tap {
                it.user = user
                it.pass = pass
                it.url = url.toString()
//                it.builder().user(user).pass(pass).url(url.toString()).build()
            }.with {new ImmutableScm(it)}


    }

    boolean repoExists(String prjName, String repoName)

    Set<String> getGroups()

    def addGroupToRepo(String groupSlug, String reposlug, String privilage)

    Project createProject(String name, String key, String description, InputStream avatar)

    def createGroup(String groupName)

    InputStream getProjectAvatar(String prjname)

    Set<Repository> getRepos(String project)

    Set<Repository> getRepos()

    def search(String query)

    Repository getRepo(String project, String name)

    Set<Project> getProjects()

    Project getProject(String name)

    boolean delProject(String prjName)


    Repository createRepo(String prjKey, String repoName, String description)

    public String getName()
}
