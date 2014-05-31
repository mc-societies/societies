## Motivation

Societies is a revived version of the original [SimpleClans](http://dev.bukkit.org/bukkit-plugins/simpleclans/) and is targeted to support a wide range of minecraft servers.
At the moment Societies only supports Bukkit, but as new server implementations come up support will be added.

## Special libraries

The backend library for grouping is [clib-group](https://github.com/Catharos/clib-groups), which provides a simple api to group members to groups. For basic actions and helpers [clib-core](https://github.com/Catharos/clib-core) will be used. clib-core contains functionality like commands, futures or special collections.
The glue for all components is [google guice](https://code.google.com/p/google-guice/), a lightweight dependency injection framework.

## Compilation

We use maven to handle our dependencies.

* Install [Maven 3](http://maven.apache.org/download.html)
* Check out this repo and: `mvn clean install`
