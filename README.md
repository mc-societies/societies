![build](https://raw.githubusercontent.com/Catharos/Societies/master/clan_crest.png)  ![build](https://api.travis-ci.org/Catharos/Societies.png) [![Crowdin](https://d322cqt584bo4o.cloudfront.net/societies/localized.png)](https://crowdin.com/project/societies)

## Motivation

Societies is a revived version of the original [Simplesocieties](http://dev.bukkit.org/bukkit-plugins/simplesocieties/) and is targeted to support a wide range of minecraft servers.
At the moment Societies only supports Bukkit, but as new server implementations come up support will be added. (Sponge support is confirmed)

“The ability to simplify means to eliminate the unnecessary so that the necessary may speak.”
—Hans Hofmann

Simplesocieties was borne out of the need for a self-sustaining drop-and-go system that could be easy to picked up by new players and required minimal attention from server staff. The system has been running over at the SacredLabyrinth where it has been enjoyed and refined by its community. I present it now for public consumption, enjoy.
—Phaed


## Installation

If you're familiar with [Bukkit](http://bukkit.org/), you're probably also familiar with the installation of plugins. Look [here](http://wiki.bukkit.org/Installing_Plugins) for some more information.

The latest build can be downloaded [here](http://build.frederik-schmitt.de/).


## Usage

### Society List
You can view all the societies on your server using the /society list command.

### Society Roster
The /society roster command shows you the members of your society. Here you can see your society's members ranks, status, and when the last time they were seen online was.


### Society Vitals
The /society vitals command shows you the vitals of all online members of your society. Here you can see their health, what armor and weapons they are carrying and of what materials, and how many hearts all of their food contains. This comes in handy during battle to know who you need to help and who can bring help to you.


### Society Coordinates
The /society coords command shows you the distance and coordinates of all online members of you society, sorted by distance. With this you will be able to know who to call for help during a raid/battle.


### Member Lookup
You can look up your own or another players info using /society lookup command. This gives you all the data concerning the player, his kills and deaths, what society he belongs to, the date when he joined the society, his status in the society, when he was last seen, and how many days he has been inactive for.


### Society Profile
With the /society profile command you can view details about any society. It shows you the society leaders, how many members are currently online, allies, rivalries, the date founded, and how many days it has been inactive for.


### Society Tags
When a society is created, you must give it a name and a tag. The tag will be used as the unique identifier for you society, will be used in the society commands, and will pretty much represent your society. This tag can have color codes which can be later modified. For example, If i wanted to create a society named "Knights of the Labyrinth" with a red and white tag named "kol", I would use the following create command:

/society create "Knights of the Labyrinth" &4K&Fo&4l
Later on if I wanted to modify this tag, for example I wanted to make it all red instead of red and white and all uppercase I would use the modtag command. Note: with the modtag command you can only change colors and case, but not the letters that make up the tag

/society tag &4KOL
The colored tag will prefix all society player's names on chat.


### Society Alliances and Rivalries
Any society leader can send an request to start an alliance with any other society with /society allies add. If the request is accepted by a leader of the second society, the alliance is formed. The alliance can be broken by any leader of either society at any time with /society allies remove, no one needs to accept the removal of an alliance.

society rivalries can be started by any society at any time, no request is needed, rivalries are automatically formed once a society leader decides he wants one by using /society rivals add. If someone has pissed you off and you want them as rivals, their permission is not needed. To break a society rivalry on the other hand, you need the acceptance of the other society, you must use /society rival remove to send the other society a request, once one of their leaders accept the rivalry is broken.

You can view a list of all societies and their allies with the /society alliances command, or their rivals with the /society rivalries command.


### Society Homes
Societies can set a home location with /society home set. The society must be verified and only the leader is able to use this command, and he is able to use it only once. Once the home base is set it cannot be changed. This is to prevent players setting home bases in the middle of battlefields, enemy camps etc. The only ones who can change it are mods with the /society home set [tag] command.

Once home is set any member is able to /society home at any time to teleport back to their home base. Leaders have permissions for a powerful command /society home regroup, which will teleport all society members to the home base. This is useful for when your home base is being raided, or when you want to coordinate an event or raid.

Before a player is teleported he must wait a pre-configured amount of seconds on the same block. This is to prevent them from running away from pvp fights. If they move before the warmup time is expired, then the teleport is canceled. Warmup timer for teleport can be configured with the teleport.delay config setting. If set to 0, the warmup is disabled.

Alternatively if you do not allow teleporting in your server (many survival servers don't), you can disable the "societies.member.home" and "simplesocieties.member.home-regroup" permissions from your players and enable the "teleport-home-on-spawn" config setting. This will turn society homes into society spawn points. Your server's societies will still be able to set homes, and their members will be able spawn in their society homes.


### Society Ranks


### Inviting Members
Creators of societies will become the first society leader. They can invite other members into the society with /society invite. These members are first added with the untrusted status. These members will not be able to view society vitals, coords, or stats. This is to prevent players form joining societies just to spy on other societies by writing down coordinates of bases or viewing vitals during battles. Once a player has gained the society's trust he can be upgraded to trusted status with /society trust.

### Economy Support
You will need Vault for economy plugin support. You can download it form here. Just drop it into your plugins folder. http://dev.bukkit.org/server-mods/vault/
#### Society Creation
You can charge your players for unverified society creation by using the economy.creation-price config setting. Turn it off by setting it to 0. With only this setting on, you still have control of society verification.
#### Society Verification
You can also add pay for verification with the economy.verification-price config setting. This will give control of society verification back to the players, it will give them the /society verify menu item, which will charge them for society verification.
You can have either or both of these systems in place at the same time.


### Permissions
Works with all common permissions managers.


### Translations
Societies can be completely localized by contributing to our [crowdin.com project](https://crowdin.com/project/societies).

### Persistence
Societies supports two storage engines.


#### MySQL
MySQL offers you the possibility to support for example BungeeCord by disabling caching completely.
#### Integrated NoSQL engine
This integrated engine is quiet fast and is perfectly for you if you're owning a small server (less than a few hundred players) or you just want to test Societies.


### Found a bug or have a feature request?
Add it to the bug tracker: https://github.com/Catharos/Societies/issues



## Developer area
### Getting started

To get started, just contact me over at [esper.net](http://esper.net/publicirc.php) @ #Simplesocieties.

### Special libraries

The backend library for grouping is [clib-group](https://github.com/Catharos/clib-groups), which provides a simple api to group members to groups. For basic actions and helpers [clib-core](https://github.com/Catharos/clib-core) will be used. clib-core contains functionality like commands, futures or special collections.
The glue for all components is [google guice](https://code.google.com/p/google-guice/), a lightweight dependency injection framework.

### Contributing

Currently, me - p000ison (Max A.) - is the only person who's working on this project, but this will hopefully change after release. You can contact me over at irc.esper.net @ #Simplesocieties

![wtfpl](http://www.wtfpl.net/wp-content/uploads/2012/12/logo-220x1601.png)


### Compilation

We use maven to handle our dependencies.

* Install [Maven 3](http://maven.apache.org/download.html)
* Check out this repo and: `mvn clean install`
