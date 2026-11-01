# GodMace

A minimal Paper 1.21.11 plugin that adds one craftable, PDC-marked mace named **God Mace**. It behaves like a normal mace. When a player directly kills another player while using this item, the victim is kicked with `You were killed by the God Mace!`.

## Recipe

```text
Netherite Block | Sculk Catalyst | Netherite Block
Heavy Core      | Mace           | Heavy Core
Netherite Block | Sculk Catalyst | Netherite Block
```

The recipe produces one marked God Mace. The plugin also supports `/give <online-player> god_mace`; ordinary `/give` item commands are passed through to Minecraft.

## Build

Requirements: Java 21 JDK and Maven 3.9 or later.

From the project root, run:

```sh
mvn --batch-mode clean package
```

The plugin file will be `target/godmace-1.21.11.jar`. You can also push the project to GitHub and download the `GodMace` artifact from the successful **Build GodMace** workflow.

## Install

Copy `godmace-1.21.11.jar` into the server's `plugins` folder and restart a Paper 1.21.11 server running Java 21. Install only the JAR, not the source ZIP.
