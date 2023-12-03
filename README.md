# ![Image Missing](assets/Chest-Banner.png)

This plugin was created for the BlockGame MMO server (IP: mc.blockgame.info). 
It allows containers to serve instanced loot to players. Meaning, that each player sees a personal version of the loot.

### Prerequisites
- MySQL DB
### Features
- Supported Containers
  - Single Chest
  - Barrel
  - Any block with an inventory (i.e brewing stands, dispensers, hoppers)
    - Technically these blocks will add to the database and function.
    - They do not have a custom interface that would differ from the standard Chest.
    - Meaning, they always open with a chest-sized inventory. 
- Generated Structures
  - Automatically add containers from generated structures to the database.
  - Minecart chests included.
- Manually add loot containers
  - Cannot manually add minecart chests.
- Add an optional vanilla LootTable on the container
- Make loot invincible from destruction
  - Protects against breaking and exploding
  - If disabled, the container will break and drop nothing.
- Restock all loot or restock loot by world

### Planned Features
- Double chests
- Caching database calls
- Query Revamp
  - Restock by LootTable
  - Restock by location
  - Add by location
- Config field to add/ensure a list of plunders is in the database on start

### Possible Features
- Custom LootTables
- All blocks lootable
- Loot Events
  - Care Packages
  - King of the Hill
 
### config.yml

```
datasource:
  host: "127.0.0.1"
  port: 3306
  username: "root"
  password: ""
  # The plugin uses HikariCP to pool connections.
  # poolSize = How many open connections the plugin will manage
  # This value must be greater than 0. A value of 1 is technically a standard database and not optimal
  # The value of this will vary depending on each server's individual needs.
  # Setting the value to high can cause just as many performance issues as not high enough. Needs to be dialed in.
  # This is the formula given from the developer of HikariCP: connections = ((core_count * 2) + effective_spindle_count)
  # For more information - https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing#connections--core_count--2--effective_spindle_count
  poolSize: 50

debug:
  enabled: false

plunder:
  generated-structures:
    enabled: true
    world-whitelist:
      - "world"
    structure-blacklist:
  invincible: true

messages:
  broadcast-restock:
    enabled: true
  plunder:
    title: "&5Plunder"
```

### FAQ
- Q: How do I change the items that come from a LootTable?
  - A: Currently, there is no support for custom LootTables. A workaround for this is to use a datapack to modify the vanilla LootTables.
- Q: How do I connect to my database?
  - A: When loading the plugin for the first time it will create the config with fields to enter the database connection information.


 
