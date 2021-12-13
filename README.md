# MC-Log4J-Patcher  
The goal of this project is to provide Minecraft players, and server owners, peace of mind in regards to the recently discovered Log4J exploit (CVE-2021-44228).  
Currently this project looks for any Log4J format strings, not just `jndi`, and either replaces them or stops them from being logged entirely depending on your [configuration](#config).

I would much appreciate any help from others, whether it be by contributing or by suggesting features, platforms, etc. via an [issue](https://github.com/Koupah/MC-Log4j-Patcher/issues/new).

## Supported Platforms  
 - [Spigot/Bukkit](https://getbukkit.org/download/spigot) (Tested Spigot 1.8.9, 1.12.2)
 - [Bungeecord](https://github.com/SpigotMC/BungeeCord) (Tested Waterfall)


## Planned Platforms
 - Velocity
 - Sponge
 - Forge (I know it should be fixed, but people keep asking me to make a mod anyways)
 - Vanilla/All Clients via Java Agent

## Config
 - Customizable messages
 - Toggles for all listeners, individual for each platform

*Specifics need to be filled in*
