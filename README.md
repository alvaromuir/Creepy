# Creepy
v1.0

Creepy is a mult-threaded webpage scanner that searches for OneTrust and GTM tag installations.


#### Usage:
Compiled with maven. Run `$mvn clean package`

Then can be run as:  
a single url  with the `-u` or `--url` flag
`$ java -jar creepy-1.0-SNAPSHOT-standalone.jar -u http://us.coke-cola.com/`

a list of comma-separated urls with the `-l` or `--list` flag
`$ java -jar creepy-1.0-SNAPSHOT-standalone.jar -l "https://us.coca-cola.com, https://www.sprite.com, https://www.minutemaid.com/"`

a file  of comma-separated urls with the `-f` or `--file` flag  

`$java -jar creepy-1.0-SNAPSHOT-standalone.jar -f <path/to/my/file>`


see help `-?` or `--help` and other options below

``` 
creepy parses a url for KO MDS Global required tags.
usage: creepy <parameters>
Parameters
 -?,--help            prints help information
 -f,--file <arg>      file of urls (one per line) to scan
 -l,--list <arg>      list urls (comma separated) to scan
 -o,--timeout <arg>   timeout in seconds, default 3
 -t,--limit <arg>     limit of threads to use, default 10
 -u,--url <arg>       url to scan
README - https://bitbucket.coke.com/projects/KOMDS/repos/creepy/browse
```

Alvaro Muir, KO MDS Global Digital Analytics