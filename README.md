# FriendUp

A simple, no-frills friend system for Minecraft servers.

FriendUp lets players send and accept friend requests, remove friends, and view their friend list. That’s all it does — and that’s intentional. The goal is to provide a clean, lightweight experience without overcomplicating anything.

## Features

- `/friend add <player>` – Send a friend request
- `/friend accept <player>` – Accept a pending request
- `/friend deny <player>` – Deny a pending request
- `/friend remove <player>` – Remove a friend
- `/friend list` – View your current friends

Players are notified when their friends join or leave the server.

## Notes

- Player data is stored in a local SQLite database (`friendup.db`)
- Messages and colors can be adjusted in the config
- No metrics, no update checks, no internet calls

## Why?

Most friend plugins try to do too much. This one doesn’t. It just does one thing, and hopefully does it well.
