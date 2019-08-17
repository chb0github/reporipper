package org.bongiorno.reporipper.scms

import groovy.json.JsonSlurper

/**
 * @author cbongiorno on 9/28/18.
 */
interface Scm {
    static Map<String,Object> config = new JsonSlurper().parse((this.getResource('.scm.json')?.file as File) ?: new File('.scm.json'))
    static def getScm = { name ->

            ServiceLoader.load(Scm.class).find { it.toString() == name }?.tap {
                it.user = config.scm[name]?.user
                it.pass = config.scm[name]?.pass
                it.url = config.scm[name].url.with { u -> new URL(u as String) }.toString()
//                it.builder().user(user).pass(pass).url(url.toString()).build()
            }?.with {new ImmutableScm(it)}
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

    Repository getRepo(String name)

    Set<Project> getProjects()

    Project getProject(String name)

    boolean delProject(String prjName)


    Repository createRepo(String prjKey, String repoName, String description)

    public String getName()
}
