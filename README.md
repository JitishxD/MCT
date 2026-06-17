<p align="center">
  <img src="https://img.shields.io/badge/Minecraft-1.8.8--Latest-62B47A?style=for-the-badge&logo=minecraft&logoColor=white" alt="Minecraft Version"/>
  <img src="https://img.shields.io/badge/Spigot_API-1.13-F7931A?style=for-the-badge&logo=spigotmc&logoColor=white" alt="Spigot API"/>
  <img src="https://img.shields.io/badge/Java-8-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java Version"/>
  <img src="https://img.shields.io/badge/Gradle-9.5.1-02303A?style=for-the-badge&logo=gradle&logoColor=white" alt="Gradle Version"/>
</p>

<h1 align="center">⛏️ Minecraft Tools (MCT)</h1>

<p align="center">
  <em>A lightweight, all-in-one server utility plugin for Minecraft: Java Edition</em>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/version-1.2.0-blue?style=flat-square" alt="Plugin Version"/>
  <img src="https://img.shields.io/badge/license-All_Rights_Reserved-red?style=flat-square" alt="License"/>
  <img src="https://img.shields.io/badge/status-Active-brightgreen?style=flat-square" alt="Status"/>
  <img src="https://img.shields.io/badge/author-Jitish-purple?style=flat-square" alt="Author"/>
</p>

---

## 📖 About

**Minecraft Tools (MCT)** is a Spigot plugin that provides essential server management commands right out of the box. From player survival utilities like healing and feeding, to admin tools like god mode and fly, MCT gives server operators a clean and simple toolkit — no bloat, no dependencies.

---

## ✨ Features at a Glance

| Feature | Description | Targets |
|:--------|:------------|:--------|
| 💀 **Instant Death** | Kill yourself instantly with a custom death message | Self |
| 🛡️ **God Mode** | Toggle invulnerability on/off | Self, Others, All |
| 🍖 **Feed** | Restore food level to max (20) | Self, Others, All |
| ❤️ **Heal** | Restore health to max (20) | Self, Others, All |
| 🔢 **Set Health** | Set health to a specific value (0–20) | Self, Others, All |
| 🏠 **Spawn System** | Set & teleport to a custom spawn point | Server-wide |
| 🌙 **Night Vision** | Toggle permanent night vision effect | Self, Others, All |
| 🕊️ **Fly Mode** | Toggle creative-style flight in survival | Self, Others, All |
| 📡 **Ping Display** | Live ping shown on the action bar | Self |
| 💤 **AFK System** | Hologram AFK indicator with auto-idle detection | Self, Automatic |
| 🔧 **Repair Items** | Instantly repair hand item or entire inventory | Self |
| 🍗 **Set Food** | Set food level to a specific value (0–20) | Self, Others, All |
| ✨ **Super Enchant** | Apply any enchantment up to level 255 | Self, Others, All |
| 🚫 **Disenchant** | Remove enchantments from items or entire inventory | Self, Others, All |
| 👾 **Mass Summon** | Summon multiple entities at once (up to 500) | Self, Others |
| 🧭 **Player Warps** | Players can create named warps and teleport to them | Self |
| 🗺️ **Server Warps** | Admin-managed global warps for all players | Server-wide |
| 🔒 **Private Warps** | Make warps private and grant/revoke access per-player | Self, Others |
| 📨 **TPA System** | Request to teleport to/from other players with accept/deny | Self, Others |
| ↩️ **Back Command** | Return to your previous location (death, quit, or teleport) | Self, Others |
| 🔕 **TPA Toggle** | Disable receiving TPA requests entirely | Self, Others |
| 🚫 **TPA Ignore** | Block a specific player's TPA requests | Self |
| ⚡ **TPA Auto-Accept** | Automatically accept all incoming /tpa requests | Self |
| 📢 **TPA Here All** | Request all online players to teleport to you | Server-wide |
| 🎨 **Welcome Messages** | Styled join messages with time-since-last-visit | Automatic |
| ⚰️ **Respawn at Spawn** | Players respawn at the custom spawn point | Automatic |
| 🆕 **First-Join Teleport** | New players spawn at the custom spawn point | Automatic |

---

## 📋 Commands

| Command | Aliases | Description | Usage |
|:--------|:--------|:------------|:------|
| `/die` | — | Kill yourself instantly | `/die` |
| `/god` | — | Toggle god mode (invulnerability) | `/god [all \| player...] [on \| off]` |
| `/feedme` | `/feed` | Restore food level to max | `/feedme [all \| player...]` |
| `/healme` | `/heal` | Restore health to max | `/healme [all \| player...]` |
| `/setHealth` | — | Set health to a specific value | `/setHealth <0-20>` or `/setHealth <player\|all> <0-20>` |
| `/setspawn` | — | Set the server spawn point | `/setspawn` |
| `/spawn` | — | Teleport to the spawn point | `/spawn` |
| `/nightvision` | — | Toggle night vision | `/nightvision [all \| player...] [on \| off]` |
| `/fly` | — | Toggle flight mode | `/fly [all \| player...] [on \| off]` |
| `/ping` | `/latency` | Toggle live ping display on action bar | `/ping [on \| off]` |
| `/afk` | — | Toggle AFK mode with a holographic display | `/afk` |
| `/setFood` | `/setfoodlevel`| Set food level to a specific value | `/setFood <0-20>` or `/setFood <player\|all> <0-20>` |
| `/repair` | `/fix` | Repair item in hand or all items | `/repair [all]` |
| `/enchantt` | — | Enchant held item up to level 255 | `/enchantt <player\|all> <enchantment> <level>` |
| `/denchant` | — | Remove enchantments from items | `/denchant <player\|all> [enchantment \| all]` |
| `/summonn` | — | Summon multiple entities at once | `/summonn <entity> [amount] [player \| x y z]` |
| `/pwarp` | `/pw`, `/playerwarps` | Create, list, inspect, remove, and use player warps | `/pwarp <name>` or `/pwarp create <name>` |
| `/swarp` | `/sw`, `/serverwarp`, `/serverwarps` | Use admin-managed server warps | `/swarp <name>` or `/swarp create <name>` |
| `/pwarp list` | — | List warps with optional player name filter | `/pwarp list [player] [page]` |
| `/pwarp private` | — | Toggle a player warp between public/private | `/pwarp private <name>` |
| `/pwarp allow` | — | Grant a player access to a private warp | `/pwarp allow <warp> <player>` |
| `/pwarp deny` | — | Revoke a player's access to a private warp | `/pwarp deny <warp> <player>` |
| `/pwarp allowed` | — | List all players with access to a warp | `/pwarp allowed <warp>` |
| `/tpa` | — | Request to teleport to another player | `/tpa <player>` |
| `/tpahere` | — | Request another player to teleport to you | `/tpahere <player>` |
| `/tpaccept` | `/tpyes`, `/accept` | Accept a pending teleport request | `/tpaccept` |
| `/tpdeny` | `/tpno`, `/deny` | Deny a pending teleport request | `/tpdeny` |
| `/tpcancel` | — | Cancel your outgoing teleport request | `/tpcancel <player>` |
| `/back` | — | Teleport to your previous location | `/back [player]` |
| `/tpatoggle` | — | Toggle receiving TPA requests on/off | `/tpatoggle [player]` |
| `/tpaignore` | `/tpignore`, `/tpblock`, `/tpablock` | Ignore a player's TPA requests (toggle) | `/tpaignore <player>` |
| `/tpauto` | — | Toggle auto-accepting incoming /tpa requests | `/tpauto [on \| off \| check \| status]` |
| `/tpahereall` | — | Send teleport-here request to all online players | `/tpahereall` |

> **Tip:** Commands that support `all` will apply the action to every online player. You can also list multiple player names separated by spaces.
> **Storage:** Player warps are saved in `config.yml` under `player-warps.warps`; server warps are saved under `server-warps.warps`.
> **Privacy:** The `private`, `allow`, `deny`, and `allowed` subcommands work identically for both `/pwarp` and `/swarp`. Private warps show a 🔒 icon in the list and are hidden from tab-complete for unauthorized players. The warp owner and admins can always manage access.
> **Filter:** Use `/pwarp list <player>` to show only warps owned by a specific player. Supports tab-completion of owner names. Works for both `/pwarp` and `/swarp`.
> **TPA:** Requests expire after 60 seconds (configurable). Teleportation has a 5-second countdown with move-cancel, post-teleport invincibility, particles, and sounds. All timings, cooldowns, and features are fully configurable in `config.yml` under `tpa:`.
> **Note:** `/summonn` supports custom entity variants. Try aliases like `charged_creeper`, `baby_zombie`, `black_cat`, `temperate_frog`, `pale_wolf`, `warm_chicken`, `red_mooshroom`.
> **Combo aliases:** `chicken_jockey`, `husk_jockey`, `drowned_jockey`, `zombie_villager_jockey`, `spider_jockey`, `cave_spider_jockey`, `skeleton_horse_trap`, `pillager_ravager`, `evoker_ravager`, `vindicator_ravager`, `strider_jockey`, `baby_piglin_hoglin`.

---

## 🔐 Permissions

| Permission | Description | Default | Parent |
|:-----------|:------------|:--------|:-------|
| `MCT.setSpawn` | Use `/setspawn` command | OP | — |
| `MCT.godMode` | Use `/god` on yourself | OP | — |
| `MCT.godMode.toOtherPlayers` | Use `/god` on other players & all | OP | `MCT.godMode` |
| `MCT.fly` | Use `/fly` on yourself | OP | — |
| `MCT.fly.toOtherPlayers` | Use `/fly` on other players & all | OP | `MCT.fly` |
| `MCT.ping` | Use `/ping` command | Everyone | — |
| `MCT.setHealth` | Use `/setHealth` on yourself | OP | — |
| `MCT.setHealth.toOtherPlayers` | Use `/setHealth` on other players & all | OP | `MCT.setHealth` |
| `MCT.setFood` | Use `/setFood` on yourself | OP | — |
| `MCT.setFood.toOtherPlayers` | Use `/setFood` on other players & all | OP | `MCT.setFood` |
| `MCT.afk` | Use `/afk` command | Everyone | — |
| `MCT.repair` | Use `/repair` command | OP | — |
| `MCT.repair.all` | Use `/repair all` command | OP | `MCT.repair` |
| `MCT.enchantt` | Use `/enchantt` on yourself | OP | — |
| `MCT.enchantt.toOtherPlayers` | Enchant other players' held items | OP | `MCT.enchantt` |
| `MCT.denchant` | Use `/denchant` on yourself | OP | — |
| `MCT.denchant.all` | Disenchant entire inventory | OP | `MCT.denchant` |
| `MCT.denchant.toOtherPlayers` | Disenchant other players' items | OP | `MCT.denchant` |
| `MCT.summonn` | Use `/summonn` command | OP | — |
| `MCT.summonn.toOtherPlayers` | Summon entities at other players' locations | OP | `MCT.summonn` |
| `MCT.pwarp` | Use `/pwarp` and teleport to player warps | Everyone | — |
| `MCT.pwarp.create` | Create player warps | Everyone | `MCT.pwarp` |
| `MCT.pwarp.remove` | Remove owned player warps | Everyone | `MCT.pwarp` |
| `MCT.pwarp.admin` | Remove any player warp, manage any warp's privacy/access | OP | `MCT.pwarp.remove` |
| `MCT.swarp` | Use `/swarp` and teleport to server warps | Everyone | — |
| `MCT.swarp.admin` | Create/remove server warps, manage any server warp's privacy/access | OP | `MCT.swarp` |
| `MCT.tpa` | Use `/tpa` to send teleport requests | Everyone | — |
| `MCT.tpahere` | Use `/tpahere` to request players to you | Everyone | — |
| `MCT.tpaccept` | Use `/tpaccept` to accept requests | Everyone | — |
| `MCT.tpdeny` | Use `/tpdeny` to deny requests | Everyone | — |
| `MCT.tpcancel` | Use `/tpcancel` to cancel outgoing requests | Everyone | — |
| `MCT.tpatoggle` | Use `/tpatoggle` to toggle TPA on/off | Everyone | — |
| `MCT.tpatoggle.others` | Toggle TPA for other players | OP | `MCT.tpatoggle` |
| `MCT.tpaignore` | Use `/tpaignore` to block a player's requests | Everyone | — |
| `MCT.tpauto` | Use `/tpauto` to auto-accept requests | Everyone | — |
| `MCT.back` | Use `/back` to return to previous location | Everyone | — |
| `MCT.back.others` | Send other players to their previous location | OP | `MCT.back` |
| `MCT.tpahereall` | Use `/tpahereall` to request all players | OP | — |
| `MCT.tpa.bypasscooldown` | Bypass all TPA cooldowns | OP | — |

> **Note:** Parent permissions automatically inherit their children. For example, granting `MCT.godMode.toOtherPlayers` also grants `MCT.godMode`.

---

## 🎭 Events & Listeners

| Event | Behavior | Source |
|:------|:---------|:-------|
| **Player Join** | Displays a styled welcome/welcome-back message with color codes and time since last visit | [ColorCodesDemo.java](https://github.com/jitishxd/mct/blob/main/src/main/java/me/jitish/mCT/tools/listeners/ColorCodesDemo.java) |
| **Player Join (First Time)** | Teleports new players to the custom spawn point | [SpawnEvents.java](https://github.com/jitishxd/mct/blob/main/src/main/java/me/jitish/mCT/tools/spawn/SpawnEvents.java) |
| **Player Respawn** | Respawns players at the custom spawn point (if set) | [SpawnEvents.java](https://github.com/jitishxd/mct/blob/main/src/main/java/me/jitish/mCT/tools/spawn/SpawnEvents.java) |
| **Player Join (Ping)** | Auto-enables ping display on the action bar for eligible players | [PingDisplayListener.java](https://github.com/jitishxd/mct/blob/main/src/main/java/me/jitish/mCT/tools/listeners/PingDisplayListener.java) |
| **Player Quit** | Automatically cleans up ping display tasks | [PingDisplayListener.java](https://github.com/jitishxd/mct/blob/main/src/main/java/me/jitish/mCT/tools/listeners/PingDisplayListener.java) |
| **Activity Tracking** | Monitors chats, moves, interactions to reset AFK timeout | [AfkListener.java](https://github.com/jitishxd/mct/blob/main/src/main/java/me/jitish/mCT/tools/listeners/AfkListener.java) |
| **Player Death (TPA)** | Saves death location for `/back` if enabled | [TpaListener.java](https://github.com/jitishxd/mct/blob/main/src/main/java/me/jitish/mCT/tpa/TpaListener.java) |
| **Player Quit (TPA)** | Saves quit location for `/back`, cleans up pending requests | [TpaListener.java](https://github.com/jitishxd/mct/blob/main/src/main/java/me/jitish/mCT/tpa/TpaListener.java) |
| **Player Move (TPA)** | Cancels pending teleport if player moves >2 blocks during delay | [TpaListener.java](https://github.com/jitishxd/mct/blob/main/src/main/java/me/jitish/mCT/tpa/TpaListener.java) |
| **Player Teleport (TPA)** | Optionally logs all teleport locations for `/back` | [TpaListener.java](https://github.com/jitishxd/mct/blob/main/src/main/java/me/jitish/mCT/tpa/TpaListener.java) |


---

## 🔧 Installation

1. Download the latest `MCT-x.x.x-xx.x.x.jar` from the releases (or build from source)
2. Place the JAR in your server's `plugins/` folder
3. Restart or reload the server
4. *(Optional)* Configure permissions using a permissions plugin like LuckPerms

---

## 🏗️ Building from Source

**Prerequisites:**
- Java 8 or higher
- Git

```bash
# Clone the repository
git clone https://github.com/JitishxD/MCT.git
cd MCT

# Build the plugin
./gradlew clean build        # Linux / macOS
.\gradlew.bat clean build    # Windows
```

The compiled JAR will be output to the configured destination directory.

---

## 📁 Project Structure

```
MCT/
├── src/main/
│   ├── java/me/jitish/mCT/
│   │   ├── MCT.java                          # Main plugin class
│   │   ├── tools/                            # General utility features
│   │   │   ├── commands/                     # Utility commands
│   │   │   │   ├── Afk.java                  # /afk command
│   │   │   │   ├── Die.java                  # /die command + death event
│   │   │   │   ├── FeedMe.java               # /feedme command
│   │   │   │   ├── Fly.java                  # /fly command
│   │   │   │   ├── God.java                  # /god command
│   │   │   │   ├── HealMe.java               # /healme command
│   │   │   │   ├── NightVision.java          # /nightvision command
│   │   │   │   ├── Ping.java                 # /ping command
│   │   │   │   ├── Repair.java               # /repair command
│   │   │   │   ├── Enchantt.java             # /enchantt command
│   │   │   │   ├── EnchantHelper.java         # Enchantment utilities & reflection
│   │   │   │   ├── Denchant.java             # /denchant command
│   │   │   │   ├── Summonn.java              # /summonn command
│   │   │   │   ├── SummonnVariants.java       # Entity variant registry
│   │   │   │   ├── SetFood.java              # /setFood command
│   │   │   │   └── SetHealth.java            # /setHealth command
│   │   │   ├── listeners/                    # Utility event listeners
│   │   │   │   ├── AfkListener.java          # AFK idle tracking
│   │   │   │   ├── ColorCodesDemo.java       # Join message formatting
│   │   │   │   └── PingDisplayListener.java  # Action bar ping display
│   │   │   ├── compatibility/                # Cross-version handlers
│   │   │   │   ├── VersionHandler.java       # Interface for modern features
│   │   │   │   ├── ModernFeaturesHandler.java # 1.13+ implementation
│   │   │   │   └── LegacyFeaturesHandler.java # 1.8-1.12 implementation
│   │   │   ├── spawn/                        # Spawn system
│   │   │   │   ├── SetSpawn.java             # /setspawn command
│   │   │   │   ├── Spawn.java                # /spawn command
│   │   │   │   └── SpawnEvents.java          # Spawn/respawn handling
│   │   │   └── LocationUtil.java             # Cross-version primitive location serializer
│   │   ├── tpa/                              # TPA teleport system
│   │   │   ├── TpaManager.java               # Core TPA logic (requests, teleport, safe-TP)
│   │   │   ├── TpaStorage.java               # In-memory state (requests, cooldowns, toggles)
│   │   │   ├── TpaSettings.java              # Config reader for tpa: section
│   │   │   ├── TpaListener.java              # TPA events (death, quit, move, teleport)
│   │   │   └── commands/
│   │   │       ├── TpaCommand.java            # /tpa
│   │   │       ├── TpaHereCommand.java        # /tpahere
│   │   │       ├── TpAcceptCommand.java       # /tpaccept
│   │   │       ├── TpDenyCommand.java         # /tpdeny
│   │   │       ├── TpCancelCommand.java       # /tpcancel
│   │   │       ├── BackCommand.java           # /back
│   │   │       ├── TpaToggleCommand.java      # /tpatoggle
│   │   │       ├── TpaIgnoreCommand.java      # /tpaignore
│   │   │       ├── TpautoCommand.java         # /tpauto
│   │   │       └── TpaHereAllCommand.java     # /tpahereall
│   │   └── warps/                            # Warp system
│   │       ├── WarpCommand.java              # /pwarp and /swarp command handler
│   │       ├── WarpPoint.java                # Warp data (name, location, privacy, access list)
│   │       └── WarpStore.java                # Warp persistence & access queries
│   └── resources/
│       ├── plugin.yml                        # Plugin descriptor
│       └── config.yml                        # Configuration file
├── build.gradle                              # Build configuration
├── settings.gradle                           # Gradle settings
└── README.md
```

---

## 🧰 Tech Stack

| Component | Technology | Version |
|:----------|:-----------|:--------|
| Language | Java | 8 |
| Server API | Spigot API | 1.13 |
| Build Tool | Gradle | 9.5.1 |


---

## 🗺️ Compatibility

| Minecraft Version | Supported | Notes |
|:-------------------|:---------:|:------|
| 1.13 - Latest | ✅ | Full feature support (Particles, clickable chat, sounds, etc) |
| 1.8.8 - 1.12 | ✅ | Core functionality works. Modern features fail gracefully via VersionHandler |

---

## ⚙️ Version Handling Architecture

MCT is designed to run seamlessly on virtually any Spigot version from **1.8.8 to the latest release** without requiring multiple different jars. This is achieved through a smart, reflection-based multi-version strategy:

1. **Base API Downgrade:** The plugin is compiled against Java 8 and the Spigot 1.13 API. This serves as a "middle-ground" that prevents `UnsupportedClassVersionError` on older servers while maintaining access to most modern Bukkit methods.
2. **Dynamic Version Detection:** On startup, the plugin parses the server's version string (e.g., `v1_8_R3`, `v1_21_R1`).
3. **The `VersionHandler` Interface:** Modern features that don't exist in older versions (like clickable TextComponents, action bar messages, or specific sound enums) are abstracted behind a `VersionHandler` interface.
4. **Pure Bukkit Fallbacks:** 
   - If the server is 1.13+, it loads `ModernFeaturesHandler`. If older (e.g., 1.8.8), it loads `LegacyFeaturesHandler`.
   - Both handlers actively `try/catch` BungeeCord/Spigot chat events. If the server is pure CraftBukkit (lacking `.spigot()` API), UI features like clickable TPA buttons gracefully degrade into standard chat messages to prevent crashes.
   - Transient features unsupported by legacy versions (e.g., continuous Ping Action Bars on 1.8-1.12) fail silently instead of spamming chat.
   - The plugin explicitly rejects Minecraft 1.7 and older on startup, as those versions lack critical APIs (ArmorStands, GameMode.SPECTATOR, etc.).
5. **Primitive Serialization:** To prevent fatal `ConstructorException` crashes from Bukkit's natively flawed `!!org.bukkit.Location` SnakeYAML serialization across drastically different server versions, all locations (warps, spawns) are mathematically saved as primitive maps (`x`, `y`, `z`, `yaw`, `pitch`, `world`).
6. **Reflection:** For deeply ingrained breaking changes (like `ItemMeta.Damageable` in 1.13+ vs `item.setDurability()` in 1.8), MCT heavily utilizes Java Reflection to determine available methods at runtime and cast appropriately, ensuring the exact same code executes correctly regardless of the underlying server software.

---

## 📌 Version History

| Version | MC Version | Highlights                                                                                                  |
|:--------|:-----------|:------------------------------------------------------------------------------------------------------------|
| `1.2.0` | 1.8.8-Latest | Dropped 1.7 support (minimum is now 1.8.8), added startup version gate, routed TPA sounds through VersionHandler, fixed modern Paper startup crash, removed `System.out` console warnings |
| `1.1.0` | 1.8.8-Latest | Downgraded to Java 8, added `VersionHandler` to gracefully support Minecraft 1.8.8 through the latest release, improved `/tpauto`, fixed enchant autocomplete, and patched 1.8.8 bugs (AFK holograms, Sounds) |
| `1.0.0-26.1` | 26.1       | Release V1! restructured into feature-based packages (tools/, tpa/, warps/)                                 |
| `0.0.9-26.1.2` | 26.1.2     | Integrated SimpleTpa: /tpa, /tpahere, /tpaccept, /tpdeny, /tpcancel, /back, /tpatoggle, /tpaignore, /tpauto, /tpahereall with full config |
| `0.0.8-26.1.2` | 26.1.2     | Private warps with per-player access control; compact inline warp list; player name filter for list command |
| `0.0.7-26.1.2` | 26.1.2     | Added dependency-free player warps and admin-managed server warps                                           |
| `0.0.6-26.1.2` | 26.1.2     | Enchant (up to 255), Disenchant, and Mass Summon commands                                                   |
| `0.0.5-26.1.2` | 26.1.2     | SetFood cleanup, AFK and repair commands                                                                    |
| `0.0.4-26.1.2` | 26.1.2     | Updated to Minecraft 26.1.2, Gradle 9.5.1                                                                   |
| `0.0.3-1.21.11` | 1.21.x     | Added ping display, fly, god mode                                                                           |

---

<p align="center">
  <sub>Made with ❤️ for the Minecraft community</sub>
</p>
