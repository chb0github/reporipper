# RepoRipper
a cli tool in groovy for managing various scms


Commands currently supported:
-
 - `cp ` example: `scm cp -format "{it -> it.collect{ it.name }}" st:MYPROJ/* bb:MY_OTHER_PROJ`the symbols: `st` and 
 `bb` 
 are derived from the `.scm.json` file where credentials are also stored; these can be overriden on the CLI as such: 
 
 - `scm cp -user cbongiorno -pass mypass -url https://restapi.myrepo.com  cp ..`

The `-format` option allows you to entirely remap the results. By default the output will be formatted json
Configuration:
-
Host, username and password are maintained in [.scm.json](.scm.json) - which is assumed to be in the current working 
directory; The file is not encrypted.


 
[![My Stack Overflow Profile](https://stackexchange.com/users/flair/673865.png)](http://stackexchange.com/users/673865)