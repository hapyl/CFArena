# [⚙] System Changes

Damage Changes
---

* Removed vanilla invulnerability ticks in favor of a custom system.
    * The system stores per-player per-cause damage, meaning each player has their own cooldown for each different
      attack.

* Increase max damage `32,767` >> `9,999,999`.
* True damage no longer ignores attacker's Damage attribute.
* The `last damager` is now cleared before applying damage calculations if they have died (Unless player).

Attribute Changes
---

### Attack

* Renamed to Damage and changes the display from `attack%` > `*attack` stating clearly that the attribute is a
  multiplier.
* Increase the max value `10` >> `10,000`.

---

# [🦸‍♀️] Hero Changes

Mage
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

Troll
---

### Last Laugh (Passive)

* Made the passive not work on bosses and mini-bosses.

### Sticky Situation (Ultimate)

* Reworked to allow it affecting entities other than players.
* The cobweb is now removed when an entity steps in it.

Nightmare
---

* Updated description for all abilities.

### Your Worst Nightmare

* Changed to affect enemies in large AoE instead of all players but add the ability to affect entities other than
  players.

Dr. Ed
---

### Dr. Ed's Amnesia Extract Serum

* Reworked the ability to properly work on players and affect entities other than players.

Ender
---

### Ender Skin (Passive)

* Changed the passive to be based on percentages rather than flat values.
* Increase the damage deals 1 (effectively 1%) >> 3% of enemy Max Health.
* Increase healing 4 (effectively 3%) >> 6% of Max Health.
* Increase damage boost 20 (effectively 20%) >> 20% of *current* Damage.
* Change water damage to be 2 (effectively 1%) >> 2% of Max Health.

Spark
---

### Hot Hands
* Change healing to always be 40% of Max Health.
* Updated fx.

### Run It Back (Ultimate)
* Back-end changes.

---

# [🐜] Bug Fixes

* Fixed Block Harvest not being able to damage multiple entities.

---