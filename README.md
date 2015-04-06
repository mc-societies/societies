![logo](https://raw.githubusercontent.com/mc-societies/societies/master/logo.png) [![Crowdin](https://d322cqt584bo4o.cloudfront.net/societies/localized.png)](https://crowdin.com/project/societies)

## Motivation

[![Join the chat at https://gitter.im/mc-societies/societies](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/mc-societies/societies?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Societies is a revived version of the original [SimpleClans](http://dev.bukkit.org/bukkit-plugins/simpleclans/) and is targeted to support a wide range of minecraft servers.
At the moment Societies only supports Bukkit, but as new server implementations come up support will be added. (Sponge support is confirmed)

“The ability to simplify means to eliminate the unnecessary so that the necessary may speak.”
*—Hans Hofmann*

SimpleClans was borne out of the need for a self-sustaining drop-and-go system that could be easy to picked up by new players and required minimal attention from server staff. The system has been running over at the SacredLabyrinth where it has been enjoyed and refined by its community. I present it now for public consumption, enjoy.
*—Phaed*

## Found a bug or have a feature request?
Add it to the bug [tracker](https://github.com/Catharos/Societies/issues).

## Developer area
### Getting started

To get started, just contact me over at [esper.net](http://esper.net/publicirc.php) @ #SimpleClans.

### Special libraries

The backend library for grouping is [clib-group](https://github.com/Catharos/clib-groups), which provides a simple api to group members to groups. For basic actions and helpers [clib-core](https://github.com/Catharos/clib-core) will be used. clib-core contains functionality like commands, futures or special collections.
The glue for all components is [google guice](https://code.google.com/p/google-guice/), a lightweight dependency injection framework.

### Contributing

Currently, me - p000ison (Max A.) - is the only person who's working on this project, but this will hopefully change after release. You can contact me over at irc.esper.net @ #SimpleClans

#### License

Societies is licenced under [WTFPL](http://www.wtfpl.net/)


### Compilation

We use maven to handle our dependencies.

* Install [Maven 3](http://maven.apache.org/download.html)
* Check out this repo and: `mvn clean install`
