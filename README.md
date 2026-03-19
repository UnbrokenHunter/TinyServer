# TinyServer – Daily Playtime Limit Plugin

TinyServer is a lightweight Minecraft Paper plugin that limits how long players can play each day.
It is designed for small private servers where you want to enforce healthy playtime limits while still allowing administrators to grant temporary bonus time when needed.

The plugin supports:

* A configurable **global daily playtime limit**
* **Per-player permanent limits**
* **Temporary bonus time for a single day**
* Simple administrative commands
* Tab display showing remaining playtime
* Colorful command output for clear feedback

TinyServer is built for the **Paper** server platform.

---

# Features

### Daily Playtime Limit

Each player has a maximum amount of time they can play per day. When their time runs out, they are automatically kicked from the server.

### Per-Player Permanent Limits

Admins can permanently change a specific player's daily limit.

Example:

```
/timelimit setlimit 1800 PlayerName
```

### Temporary Bonus Time

Admins can grant additional playtime that only applies to the current day.

Example:

```
/timelimit add PlayerName 300
```

### Clear Limit

Remove a player's custom limit and return them to the global limit.

### Tab Time Display

Remaining playtime is displayed directly in the player list (tab).

### Admin Tools

Includes commands for checking, resetting, and modifying playtime limits.

---

# Installation

## Download from Hanagar
Follow this [link](https://hangar.papermc.io/UnbrokenHunter/TinyServer) and download from Hangar. 

## Manual

1. Download the latest release `.jar` file.
2. Place it inside your server's `plugins/` folder.
3. Start or restart your server.
4. Configure the plugin in `config.yml` if desired.

Server must be running **Paper** (or a compatible fork).

---

# Configuration

Example `config.yml`:

```yaml
daily-limit-seconds: 900
```

Default value:
`900 seconds = 15 minutes`

This is the **global daily playtime limit**.

---

# Commands

| Command                                  | Description                                            |
| ---------------------------------------- | ------------------------------------------------------ |
| `/timelimit check <player>`              | Check a player's used time, remaining time, and limits |
| `/timelimit reset <player>`              | Reset a player's playtime for today                    |
| `/timelimit add <player> <seconds>`      | Give a player bonus playtime for today                 |
| `/timelimit setlimit <seconds>`          | Set the global daily playtime limit                    |
| `/timelimit setlimit <seconds> <player>` | Set a permanent limit for a specific player            |
| `/timelimit clearlimit <player>`         | Remove a player's personal limit                       |
| `/timelimit globalreset`                 | Reset all players' playtime for today                  |

---

# Permissions

| Permission                   | Description                                | Default  |
| ---------------------------- | ------------------------------------------ | -------- |
| `tinyserver.timelimit.check` | Allows checking playtime                   | everyone |
| `tinyserver.timelimit.admin` | Allows modifying limits and resetting time | OP       |

---

# How It Works

Each player has three possible components affecting their playtime:

1. **Global Limit**
   Default daily playtime defined in `config.yml`.

2. **Personal Limit (optional)**
   A permanent override set with `/timelimit setlimit`.

3. **Bonus Time (temporary)**
   Extra time granted with `/timelimit add`.
   This only applies to the current day.

The effective daily limit is calculated as:

```
effective_limit = base_limit + bonus_time_today
```

Where `base_limit` is either the player's personal limit or the global limit.

---

# Contributing

Pull requests are welcome.

If you find bugs or new features, feel free to open an issue.

---

# License

MIT License

