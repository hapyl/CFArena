# [⚙] System Changes

Damage Changes
---

* Removed vanilla invulnerability ticks in favor of a custom system.
    * The system stores per-player per-cause damage, meaning each player has their own cooldown for each different
      attack.

* Increase max damage `32,767` >> `9,999,999`.
* True damage no longer ignores attacker's Damage attribute.

---

Attribute Changes
---

* Completely overhaul the attribute system in favour of dynamic modifiers.
* Changed modifier to show what attribute they modified instead of modifier name.

### Attack

* Increase the max value `10` >> `10,000`.

---
Charged Talent Changes
---

* Overhaul the backend system.
* Removed cooldown between charges, allowing "spamming" the talent.
    * There is a hardcoded 5-tick cooldown to prevent accidentally using the talent.
* The cooldown on item now shows the cooldown until next charge, rather than the total cooldown.

---
Ultimate Changes
---

* Reworked ultimate to allow resource sources other than energy.
* Energy supply pack now regenerated 10% energy, regardless of ultimate resource.
* Change the emoji for ultimate.

---
Other Changes
---

* Improved all text displays by making them smoother.
* Show Damage in Chat setting in now called "Show Combat Feedback", and it also shows healing.

---

# [🦸‍♀️] Hero Changes

Archer
---

### Shock Dart

* The shock explosion can no longer crit.

---
Pytaria
---

### Flower Escape

* Can now damage multiple entities at the same time.
* No longer damages teammates.
* No longer deals knockback.
* Updated description.

### Flower Breeze

* Updated description.

### Feel the Breeze (Ultimate)

* Add a little animation to the bee.

### Excellency (Passive)

* The attribute increase provided by the talent is now percentage based on the current stats, instead of flat increase.
* Updated description.

---
Troll
---

### Last Laugh (Passive)

* Made the passive not work on bosses and mini-bosses.

### Sticky Situation (Ultimate)

* Reworked to allow it affecting entities other than players.
* The cobweb is now removed when an entity steps in it.

---
Nightmare
---

* Updated description for all abilities.

### Your Worst Nightmare

* Changed to affect enemies in large AoE instead of all players but add the ability to affect entities other than
  players.

---
Dr. Ed
---

### Dr. Ed's Amnesia Extract Serum

* Reworked the ability to properly work on players and affect entities other than players.

---
Ender
---

### Ender Skin (Passive)

* Changed the passive to be based on percentages rather than flat values.
* Increase the damage deals 1 (effectively 1%) >> 3% of enemy Max Health.
* Increase healing 4 (effectively 3%) >> 6% of Max Health.
* Increase damage boost 20 (effectively 20%) >> 20% of *current* Attack.
* Change water damage to be 2 (effectively 1%) >> 2% of Max Health.

---
Spark
---

### Hot Hands

* Change healing to always be 40% of Max Health.
* Updated fx.

### Run It Back (Ultimate)

* Back-end changes.

---
Vortex
---

### Astral Star

* Changed health sacrifice to be 20% of Max Health instead of flat 20 ❤️.

> This results in sort of _buff_ because the cost is based on <u>current</u> Max Health.

---
Blast Knight
---

### Nanite Rush (Ultimate)

* The shield can now be applied to allied entities.

---
Taker
---

### Hook of Death

* Reworked how the talent works, allowing moving while the chain is travelling.

### Shadowfall (NEW!)

* Creates a zone that blinds and impairs enemies.

### Spiritual Bones (Passive)

* Increased the damage dealt bonus per bone 2% >> 5%.

### Embodiment of Death (Ultimate)

* Increase damage from `5 + bones` >> `10 + bones`.
* Increase healing from `1 + bones` >> `2% + 0.5% * bones`.
* Holding **SNEAK** now makes the travelling speed faster rather than slower.
* Improved Fx.

---
JuJu
---

### Arrow Shield

* Reworked the ability to be an actual shield.
* Increase the AoE of arrows explosion 2 >> 5.

### Climb (Passive)

* Back-end changes.
* Swapped the climb and backflip buttons.

### Poison Ivy (Ultimate)

* Poison Ivy no longer affects Juju or his teammates.
* Is now considered an effect.
* Improved Fx. (Change the leaf displayColor after 1.21.5)

---
Tamer
---

### Taming the Wind

* Change the right-click to use directional movement instead of constant forward movement.

### Taming the Time

* Change the left-click to be radius based but allowed entities other than players to be affected.
* Is now considered an effect.
* Add sound fx.

---
Techie
---

* Improved and cleaned up all talent descriptions.

---
Harbinger
---

* Change faction to The Mercenaries.

### Melee Stance

* Increase crit rate increase +30 >> +50.
* Changed the weapon to be Diamond Sword instead of Iron Sword.

### Tidal Vortex

* Reworked the ability to apply Riptide effect and lock talents instead of pushing enemies.

### Riptide (Passive)

* Updated description to note that fully charged arrows are always critical.

### Arrow Typhoon (NEW! Ultimate)

* Reworked the ultimate:
    * Creates an arrow typhoon that constantly deals damage.
    * Cannot be charged passively, instead charged by applying Riptide on enemies.
    * Can be executed early after triggering a Riptide Slash.

---
Shaman
---

### Totem

#### Harmony

* Change healing from being flat 3 to 3% of Max Health.
* Increase radius 3 >> 4.5.
* Improved fx.

### Shaman's Mark

* Backend changes.
* Description changes.
* Add a sound when a line of sight breaks for the first time.

### Overheal (Passive)

* Increase damage increase per 1 overheal 5% >> 15%.
* Improved damage display to show the additional overheal damage next to final damage.
* Improve description.

---
Bounty Hunter
---

### Shorty

* Optimize the damage calculations.
* Optimize bleeding effect to remember who applied the bleeding.
* Improved fx.

### Grappling Hook

* Increase max distance 30 >> 50 blocks.
* Increase both extend and pulling speed.
* The hook no longer breaks if the anchor line of sight is obstructed.
    * To combat this, a 8s air limit was added after which the hook breaks.
* Optimize block anchoring, making it easier to land at higher elevations.
* Bounty Hunter now always gets Fall Damage Resistance, even if the hook breaks.
* Add the ability to strafe while on the hook using A & D keys.

### Smoke Bomb (NEW?)

* Moved form being passive to own talent.
* Backend changes.

### Backstab (NEW! Passive)

* Any damage from behind is increased by x2 and has increased Crit Chance.

### Severance (Ultimate)

* Renamed Backstab >> Severance.
* Increase damage dealt 30 >> 50.
* Increase max distance = 15 >> 25 blocks.
* Slightly increase lookup radius, making it easier to target enemies.
* Now works on non-player entities.

### Blood Bounty (NEW! Weapon Ability)

* Puts a bounty on a target enemy.
* Can ONLY damage that enemy but ignore % of their Defense.
* End bounty and apply bleeding after the 5th hit.

---
Heavy Knight
---

* Increased Speed 60 >> 75.

### Uppercut

* Now applies Daze effect which increases the chance for target to miss their attack.

### Touchdown

* Now deals more damage to dazed enemies.

### Break

* Renamed Slash >> Break.
* Reworked how the Perfect Sequence works:
    * Instead of increasing damage, spawns two slashes that deal additional damage.
    * Empowers the Leap ability to pull and daze enemies as well as resetting the cooldown of Break.

### Enchanted Armor (NEW! Passive)

* Your enchanted armor is undestructible, preventing your Defense from decreasing.

### Siegebreaker (NEW! Ultimate)

* Summons a sword and dashes forward, knocking enemies back and applying Daze.

---
Engineer
---

### Construct

* Increase construct limit 1 >> 2.
    * Only one instance of the same construct can exist at a time.
* Remove duration in favor of charges:
    * Construct now may exist indefinitely, unless their health or charges go below zero.
    * Each construct decrements charges in their own way.
* Upgrading the construct scales health to remain the same relative percentage of the new max.

### Sentry

* Increase building cost 1 >> 5.
* Increase upgrade cost 4 >> 5.
* Decrements charge on shot.
* Improve model.

### Dispenser (NEW!)

* Spawns a random Supply Pack for teammates.
* Decrements charge when a supply is picked up.

### Magnetic Attraction (Passive)

* Increase max iron 10 >> 20.
* Engineer now starts with `max / 2` iron.

### Mecha-Industries

* Change the Mecha height to be on par with player's height.
* Mecha is now invulnerable to suffocation damage.
* Player is now completely immune to damage while piloting.

---
Bloodfiend
---

### Twin Claws

* Update model.

### Blood Chalice

* Is now breakable by hitting with own Twin Claws.

### Impel (Ultimate)

* Increase delay between Impels 0.5s >> 0.75s

---
Zealot
---

### Broken Heart Radiation

* Offset the visual height of the beam.

---
Rogue
---

### Swayblade

* Add blindness and nausea fx.

### Second Wind (Passive)

* Now clears all negative effects upon entering.

---
Aurora
---

### Celeste Arrows

* Change healing 7 >> 7% of Max Health.
* Arrows no longer reduce damage to enemies; instead, the damage is dealt.

### Ethereal Arrows
* Increase linger duration 0.25s >> 1s.

### Guardian Angel (Passive)
* Change healing 5 >> 5% of Max Health.

### Divine Intervention (Ultimate)
* Change healing 0.5 >> 1% of Max Health.
* Ending the effect now adds a slow falling effect.
* Ending the effect now snaps to the weapon.
* Aurora can now manually break the bond by pressing the ultimate key.

---
Nyx
---

### Wither Path
* Update spike model.

### Chaos Expansion
* Change healing 7, 5, 3 >> 10%, 7%, 5% of Max Health.
* When an enemy picks up an orb, the energy stolen is now transferred to Nyx.
* Droplets are now glowing for Nyx and her teammates.


---
Himari
---

### Lucky Day
* Change healing/Max Health boost 80 >> 80%/+80% ADT.

### All In (Ultimate)
* Change healing 40 >> 40%.

---
Inferno
---

### Demonsplit: Typhoeus
* Reduce fire damage 10% >> 5% of Max Health.
* Improve fire mechanics.

### Fire Pit
* Improve fire mechanics.

### Fire Pillar (Ultimate)
* Change the pillar to damage any enemy within distance instead of only enemy players.

---

# [🐜] Bug Fixes

* Fixed the following not being able to damage multiple entities:
    * Triple Shot
    * BOOM BOW!
    * Block Harvest
    * All Astral damage
    * Shadow Swift
    * Dark Cover
    * Fatal Reap
    * Elusive Arrows
    * Twin Claws
    * Impel
* Fixed a bug where Blast Knight would regenerate 49 shield instead of 50.
* Fixed a bug where Twin Claws bite damage multiplier only applied for players.
* Fixed a bug where dying would not cancel Impel.
* Fixed a bug where Broken Heart Radiation would affect teammates.
* Fixed a bug where Inferno fire could damage teammates.

---