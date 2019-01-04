package cmds

import scms.Scm
/**
 * @author cbongiorno on 12/6/18.
 */
class Ls implements Command {

    def withRepos(def scm, def prj) {
        scm.getRepos(prj)
    }


    @Override
    def execute(Context ctx) {
        def (scmName,project) = (ctx.args[0] =~ '(ss|bb):([A-Za-z]+|\\*)').with { matches() ? it[0][1..2] : []}
        Scm scm = Scm.getScm(scmName,ctx)
        if (!project || '*' == project) {
            return [
                    scm.getProjects().collect { prj ->
                        [
                                id   : prj.id,
                                name : prj.key,
                                href : prj.links.self.href[0],
                                repos: withRepos(scm, prj.key),
                        ]
                    }
            ]
        } else {
            return args.with { it as Set }.collect { arg ->
                scm.getProject(project).with { prj ->
                    [
                            id   : prj.id,
                            name : prj.key,
                            href : prj.links.self.href,
                            repos: withRepos(scm, prj.key),
                    ]
                }
            }
        }
    }

    @Override
    String toString() {
        'ls'
    }
}


