package org.bongiorno.reporipper.cmds

import org.bongiorno.reporipper.scms.Scm
/**
 * @author cbongiorno on 12/6/18.
 */
class Ls extends AbstractCommand<Map<Object,Object>> {

    def withRepos(def scm, def prj) {
        scm.getRepos(prj)
    }


    @Override
    Map<Object,Object> execute(Context ctx) {
        def (scmName,project) = (ctx.args[0] =~ "(${ctx.config.scm.keySet().join('|')}):([A-Za-z]+|\\*)")
                .with { matches() ? it[0][1..2] : []}
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
    String[] getArgs() {
        ['map','filter']
    }

    @Override
    String getDescription() {
        'lists all the repos in a given project'
    }
}


