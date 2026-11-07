# Homestead Bosses (Forge 1.20.1)

A standalone Forge mod adding three harvest-themed boss mobs, built to behave like proper
Minecraft bosses rather than reskinned hostile mobs:

- **The Pumpkin King** — fire-immune, launches fireball volleys, and calls in Pumpkin Minions
  once it drops below half health.
- **Baker's Bane** — a heavy melee brawler with a wide ground-slam that knocks back, blinds,
  and slows anyone caught in it.
- **The Wheat Harvester** — the toughest of the three: roots its target in place with slowness
  and periodically unleashes a sweeping whirlwind.

Pumpkin Minions are disposable reinforcements, not a fourth boss — they despawn normally and
don't get a boss bar.

## What makes these "real" bosses
All three named bosses share this behaviour (see `BaseHomesteadBoss`):
- A tracked boss bar shown to every player who can see them.
- They never naturally despawn (no wandering off and vanishing mid-fight).
- Can't be shoved around, can't be leashed away, and shrug off fall damage.
- Immune to the Wither effect, so the fight can't be cheesed with stacked debuffs.
- Broadcast a server-wide message when they spawn and when they're defeated.
- Each has its own ambient/hurt/death sounds and a sound cue on its special attack, built
  from vanilla sound events (no custom audio assets required).
- Spawn eggs are registered into vanilla's own Spawn Eggs creative tab.
- The player-based model is scaled per boss so what you see on screen actually matches the
  (much larger) hitbox you're fighting, instead of a normal-sized model floating in an
  oversized box.

## Build
Requires JDK 17 and internet access for Gradle/Forge/Minecraft dependency downloads (this
project ships the Gradle wrapper, so you don't need Gradle installed separately).

```
./gradlew build        # Linux/macOS
gradlew.bat build       # Windows
```

The output jar is in `build/libs/`. First build will take a while — Gradle has to download
Gradle itself, then ForgeGradle has to download and deobfuscate Minecraft 1.20.1 and Forge
47.3.0.

To test it: `./gradlew runClient` (or `runServer`) launches a dev environment with the mod
loaded.

> **Note on this build:** this source tree was assembled and reviewed in a sandboxed
> environment with no access to Minecraft/Forge's Maven repositories, so it could not be
> compiled end-to-end here. Every API call was checked against the official 1.20.1 mappings
> by hand (method signatures, constructors, event bus wiring). Run `./gradlew build` locally
> to get a real compiler's verdict — if anything doesn't line up, the error will point at
> exactly which line to look at.

## Spawn
`/summon homesteadbosses:pumpkin_king`, `/summon homesteadbosses:bakers_bane`, or
`/summon homesteadbosses:wheat_harvester`. All three (plus the Pumpkin Minion) have spawn
eggs, findable in the creative inventory's Spawn Eggs tab or giveable with `/give`.

None of the three spawn naturally in the world — they're intended to be placed deliberately
(by summon, egg, or your own structure/spawner setup), not stumbled into.

## Notes
This is a source project. Rendering reuses the vanilla player model with custom textures
(64×64, standard player-skin UV layout) and procedural scaling — GeckoLib is not required.
Bosses target players and are intended for survival combat. Test in a copy of your world
before adding to a long-running modpack.
