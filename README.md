# RepoRipper
a cli tool in groovy for managing various scms

Example:
- 
 - `scm cp -format "{it -> it.toJson()}" st:MYPROJ/* bb:MY_OTHER_PROJ`

the symbols: `st` and `bb` are derived from the `.scm.json` file where credentials are also stored
These can be overriden on the CLI. 
 
 - `scm cp -user cbongiorno -pass mypass -url https://restapi.myrepo.com -format "{it -> it.toJson()}" cp ..`
Current support for stash (AKA Bitbucket on prem) and Bitbucket cloud


 
