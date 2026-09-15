# SpawnerUpgrades

An addon that sits alongside SmartSpawner and adds a paid upgrade menu for
spawn rate and drop amount, with a fully configurable currency (Vault money,
a vanilla scoreboard stat, or any plugin's currency via PlaceholderAPI).

## How it works in-game

- **Shift + right-click** a spawner block → opens the Upgrade menu.
- A **plain right-click** is untouched → SmartSpawner's own menu opens exactly as before.
- Clicking an upgrade in the menu charges the configured currency and, if
  successful, bumps that spawner's level and tells SmartSpawner to update it.

## Building it

You'll need Java 17+ and Maven installed locally (this can't be compiled in
this sandbox since it has no internet access to pull dependencies).

```
mvn clean package
```

The finished jar will be at `target/SpawnerUpgrades.jar`. Drop it in your
`plugins/` folder next to SmartSpawner.

## Required one-time setup: the SmartSpawner admin command

SmartSpawner tracks spawn delay and drop amount as its own internal data,
not vanilla spawner NBT, so this addon can't safely poke those values
directly without risking breakage on every SmartSpawner update. Instead it
calls SmartSpawner's own admin command as console — which is what recent
SmartSpawner versions specifically added an "update spawner properties
in-game" admin command for.

**Before going live:**

1. In-game, run `/ss admin help` (or check your installed SmartSpawner
   version's docs) to see the exact command name and argument order for
   setting a spawner's minimum spawn delay and spawn count/drop amount.
2. Open `config.yml` in this plugin and fill in `apply.spawn-delay-command`
   and `apply.drop-amount-command` to match. Placeholders `%world% %x% %y%
   %z% %value%` are substituted automatically.
3. Leave `apply.dry-run: true` at first — it will just log the command it
   *would* run to console instead of running it, so you can copy that log
   line and test it manually before trusting it live.
4. Once you've confirmed the command actually changes the spawner, set
   `apply.dry-run: false`.

This is the one part of the setup you have to verify by hand, since the
exact command syntax depends on which SmartSpawner version you're running
and I can't query your live server from here.

## Switching currencies

Everything currency-related lives under `currency:` in `config.yml`.
Change `currency.mode` to one of:

- `vault` — charges your normal Vault-linked economy (e.g. EssentialsX money). No other setup needed.
- `scoreboard` — charges a vanilla scoreboard objective (e.g. the "gems" one). Set `currency.scoreboard.objective` to its name.
- `command` — works with any currency plugin. Set a PlaceholderAPI balance placeholder plus give/take console command templates (`%player%`, `%amount%`).

No other file needs to change when you switch modes — every other class
just talks to whichever provider is active.

## Configuring upgrade levels & costs

Edit the `upgrades:` section of `config.yml`. Each track (`spawn_delay`,
`drop_amount`) has a list of levels, each with a `cost` (in whatever
currency is active) and a `value` (the number that gets sent to
SmartSpawner via the apply command — e.g. ticks of delay, or spawn count).
Add, remove, or re-price levels freely; nothing else needs to change.

## Known limitations

- Requires manually filling in the SmartSpawner admin command syntax (see above) — this couldn't be verified without access to a live server running SmartSpawner.
- The upgrade menu is a separate GUI opened via shift+right-click rather than a button injected into SmartSpawner's own menu — this was a deliberate choice for stability, since SmartSpawner's internal GUI slot layout isn't a documented, versioned API and could silently break the integration on a future update.
- Per-spawner data is stored in a flat `data.yml`; for very large servers (tens of thousands of upgraded spawners) you may want to swap `UpgradeStorage` for a SQLite/MySQL-backed implementation, but the interface is small and easy to re-implement.
