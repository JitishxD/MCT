<p align="center">
  <img src="https://img.shields.io/badge/Minecraft-26.1.2-62B47A?style=for-the-badge&logo=minecraft&logoColor=white" alt="Minecraft Version"/>
  <img src="https://img.shields.io/badge/Spigot_API-26.1.2--R0.1-F7931A?style=for-the-badge&logo=spigotmc&logoColor=white" alt="Spigot API"/>
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java Version"/>
  <img src="https://img.shields.io/badge/Gradle-9.5.1-02303A?style=for-the-badge&logo=gradle&logoColor=white" alt="Gradle Version"/>
</p>

<h1 align="center">⛏️ Minecraft Tools (MCT)</h1>

<p align="center">
  <em>A lightweight, all-in-one server utility plugin for Minecraft: Java Edition</em>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/version-0.0.5--26.1.2-blue?style=flat-square" alt="Plugin Version"/>
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

> **Tip:** Commands that support `all` will apply the action to every online player. You can also list multiple player names separated by spaces.

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

> **Note:** Parent permissions automatically inherit their children. For example, granting `MCT.godMode.toOtherPlayers` also grants `MCT.godMode`.

---

## 🎭 Events & Listeners

| Event | Behavior | Source |
|:------|:---------|:-------|
| **Player Join** | Displays a styled welcome/welcome-back message with color codes and time since last visit | [ColorCodesDemo.java#L26](https://github.com/jitishxd/mct/blob/main/src/main/java/me/jitish/mCT/listeners/ColorCodesDemo.java#L26) |
| **Player Join (First Time)** | Teleports new players to the custom spawn point | [SpawnEvents.java#L19](https://github.com/jitishxd/mct/blob/main/src/main/java/me/jitish/mCT/listeners/SpawnEvents.java#L19) |
| **Player Respawn** | Respawns players at the custom spawn point (if set) | [SpawnEvents.java#L33](https://github.com/jitishxd/mct/blob/main/src/main/java/me/jitish/mCT/listeners/SpawnEvents.java#L33) |
| **Player Join (Ping)** | Auto-enables ping display on the action bar for eligible players | [PingDisplayListener.java#L29](https://github.com/jitishxd/mct/blob/main/src/main/java/me/jitish/mCT/listeners/PingDisplayListener.java#L29) |
| **Player Quit** | Automatically cleans up ping display tasks | [PingDisplayListener.java#L35](https://github.com/jitishxd/mct/blob/main/src/main/java/me/jitish/mCT/listeners/PingDisplayListener.java#L35) |
| **Activity Tracking** | Monitors chats, moves, interactions to reset AFK timeout | [AfkListener.java](https://github.com/jitishxd/mct/blob/main/src/main/java/me/jitish/mCT/listeners/AfkListener.java) |


---

## 🔧 Installation

1. Download the latest `MCT-x.x.x-xx.x.x.jar` from the releases (or build from source)
2. Place the JAR in your server's `plugins/` folder
3. Restart or reload the server
4. *(Optional)* Configure permissions using a permissions plugin like LuckPerms

---

## 🏗️ Building from Source

**Prerequisites:**
- Java 21 or higher
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
│   │   ├── commands/
│   │   │   ├── Afk.java                      # /afk command
│   │   │   ├── Die.java                      # /die command + death event
│   │   │   ├── FeedMe.java                   # /feedme command
│   │   │   ├── Fly.java                      # /fly command
│   │   │   ├── God.java                      # /god command
│   │   │   ├── HealMe.java                   # /healme command
│   │   │   ├── NightVision.java              # /nightvision command
│   │   │   ├── Ping.java                     # /ping command
│   │   │   ├── Repair.java                   # /repair command
│   │   │   ├── SetFood.java                  # /setFood command
│   │   │   ├── SetHealth.java                # /setHealth command
│   │   │   ├── SetSpawn.java                 # /setspawn command
│   │   │   └── Spawn.java                    # /spawn command
│   │   └── listeners/
│   │       ├── AfkListener.java              # AFK idle tracking
│   │       ├── ColorCodesDemo.java           # Join message formatting
│   │       ├── PingDisplayListener.java      # Action bar ping display
│   │       └── SpawnEvents.java              # Spawn/respawn handling
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
| Language | Java | 21 |
| Server API | Spigot API | 26.1.2-R0.1-SNAPSHOT |
| Build Tool | Gradle | 9.5.1 |


---

## 🗺️ Compatibility

| Minecraft Version | Supported | Notes |
|:-------------------|:---------:|:------|
| 26.1.2 | ✅ | Current target version |
| 26.1.x | ✅ | Should work on all 26.1 patches |
| < 26.1 | ❌ | Not supported (`api-version: 26.1.2`) |

---

## 📌 Version History

| Version | MC Version | Highlights |
|:--------|:-----------|:-----------|
| `0.0.5-26.1.2` | 26.1.2 | SetFood cleanup, AFK and repair commands |
| `0.0.4-26.1.2` | 26.1.2 | Updated to Minecraft 26.1.2, Gradle 9.5.1 |
| `0.0.3-1.21.11` | 1.21.x | Added ping display, fly, god mode |

---

<p align="center">
  <sub>Made with ❤️ for the Minecraft community</sub>
</p>
