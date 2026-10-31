# GodMace

A minimal Paper 26.2 plugin. Its marked mace is a regular Minecraft mace named **God Mace**. When a player directly kills another player while holding that marked mace in the main hand, the victim is kicked with `You were killed by the God Mace!`.

The plugin identifies the item using a namespaced persistent-data marker, not its display name. Get one with `/give <player> god_mace`; this handles only that exact custom-item form and leaves other `/give` commands to Minecraft.

## Requirements

- Java 25 JDK
- Maven 3.9 or later
- Paper 26.2

## Build

From this directory, run:

```sh
mvn package
```

The plugin JAR will be created at `target/godmace-1.1.0.jar`. The separate `build.yml` file runs the same build in GitHub Actions and uploads the JAR as a workflow artifact. In a GitHub repository, place it at `.github/workflows/build.yml`.

To get the JAR from GitHub Actions, open the successful **Build GodMace** run and download its **GodMace** artifact. Extract that artifact to get `godmace-1.1.0.jar`.

## Install

Copy `godmace-1.1.0.jar` into the `plugins` folder in the root of your Paper 26.2 server directory, replacing the older GodMace JAR, then restart the server. Do not put the source project ZIP or the workflow YAML in `plugins`; Paper loads the JAR. The server must run Java 25 or later.

After the server restarts, run `/give <online-player-name> god_mace` in-game. The command gives the marked mace to that player; other `/give` commands are left to Minecraft.
