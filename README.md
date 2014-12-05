![build](https://api.travis-ci.org/Catharos/societies.png) [![Crowdin](https://d322cqt584bo4o.cloudfront.net/societies/localized.png)](https://crowdin.com/project/societies)

## Motivation

Societies is a revived version of the original [SimpleClans](http://dev.bukkit.org/bukkit-plugins/simpleclans/) and is targeted to support a wide range of minecraft servers.
At the moment Societies only supports Bukkit, but as new server implementations come up support will be added. (Sponge support is confirmed)

“The ability to simplify means to eliminate the unnecessary so that the necessary may speak.”
*—Hans Hofmann*

SimpleClans was borne out of the need for a self-sustaining drop-and-go system that could be easy to picked up by new players and required minimal attention from server staff. The system has been running over at the SacredLabyrinth where it has been enjoyed and refined by its community. I present it now for public consumption, enjoy.
*—Phaed*


## Installation

If you're familiar with [Bukkit](http://bukkit.org/), you're probably also familiar with the installation of plugins.
Look [here](http://wiki.bukkit.org/Installing_Plugins) for some more information.

The latest build can be downloaded [here](http://build.frederik-schmitt.de/).


## Mechanics

### Listing
You can view all the societies using the **/society list** command.

### Rosters
The **/society roster** command shows you the members of your society.
Here you can see your society's members ranks, status, and when they have been seen last.


### Vitals
The **/society vitals** command shows you the vitals of all online members of your society.
Here you can see their health, what armor and weapons they are carrying and of what materials,
and how many hearts all of their food contains.
This comes in handy during battle to know who you need to help and who can bring help to you.


### Coordinates
The **/society coords** command shows you the distance and coordinates of all online members of you society.
 With this you will be able to know who to call for help during a raid/battle.


### Lookups
You can look up your own or another players info using **/society lookup** command.
This gives you all the data concerning the player, his kills and deaths, what society he belongs to,
the date when he joined the society, his status in the society, when he was last seen, and how many days he has been inactive for.


### Profiles
With the **/society profile** command you can view details about any society.
It shows you the society leaders, how many members are currently online, allies, rivalries, the date founded,
and how many days it has been inactive for.


### Tags
When a society is created, you must give it a name and a tag. The tag will be used as the unique identifier for you society
and will represent your society
This tag can have color codes which can be modified later.

### Alliances and Rivalries
Any society can send an request to start an alliance with any other society with **/society allies add**.
If the request is accepted of the second society, the alliance is formed.
The alliance can be broken of either society at any time with **/society allies** remove, no one needs to accept the removal of an alliance.

Society rivalries can be started by any society at any time, no request is needed,
rivalries are automatically formed once a society decides it wants one by using **/society rivals add**.
To break a society rivalry on the other hand, you need the acceptance of the other society,
you must use **/society rival remove** to send the other society a request, once one of their leaders accept the rivalry is broken.

You can view a list of all societies and their allies with the **/society alliances** command,
or their rivals with the **/society rivalries** command.


### Homes
Societies can set a home location with **/society home set**. Once the home base is set it cannot be changed.
This is to prevent players setting home bases in the middle of battlefields, enemy camps etc.
If you're a moderator of a server you can change it with the **/society home set [tag]** command.

Once a home is set any member is able to **/society home** at any time to teleport back to their home base.
Societies can also regroup by executing **/society home regroup**, which will teleport all society members to the home base.
This is useful for when your home base is being raided, or when you want to coordinate an event or raid.

Before a player is teleported he must wait a pre-configured amount of seconds on the same block.
This is to prevent them from running away from fights. If they move before the warm-up time is expired,
then the teleport is canceled. Warm-up timer for teleport can be configured with the **teleport.delay** config setting.


### Ranks
Ranks are basically sub-groups within societies. There are also a few pre-defined ranks:

- Leader
- Member

Those ranks have specific rights in a society. This means you have to claim the rank of a member before you can access
various information.
You can assign specific permission to a rank.

## Permissions

- vitals
- coords
- home
- home-set
- home-regroup
- alliances
- rivalries
- roster


### Inviting Members
A society can invite members into the society with **/society invite**.
These members are first added with the untrusted status.
These members will not be able to view society vitals, coords, or stats.
This is to prevent players form joining societies just to spy on other societies by writing down coordinates of bases
or viewing vitals during battles.
 Once a player has gained the society's trust he can be upgraded to trusted status with **/society trust**.


### Economy Support
You will need Vault for economy plugin support.
You can download it form [here](http://dev.bukkit.org/server-mods/vault/).


### Permissions
Works with all common permissions managers.


### Translations
Societies can be completely localized by contributing to our [crowdin.com project](https://crowdin.com/project/societies).

### Persistence
Societies supports two storage engines:

- MySQL offers you the possibility to support for example BungeeCord by disabling caching completely.
- A integrated engine is quiet fast and is perfectly for you if you're owning a small server (less than a few hundred players) or you just want to test Societies.


### Found a bug or have a feature request?
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
