# [⚙] System Changes

### ※ Ultimate Changes

**There are some big backend changes, so expect bugs!**

* Ultimates are now handled by a class, rather than a hero.
* Optimize on how ultimates are handled.
* And more nerdy stuff.

### Other changes

* Shields no longer block fall damage by default.
* Added sound FX to Archer's Hackeye arrows.
* Relic Hunt now shows it if there are no Relics in the current area.
* Increase Bounty Hunter's Fall Damage Resistance duration after using Grapple Hook.
* Updated Health Pack SFX.
* Using Bounty Hunter's Smoke Bomb now snaps to the weapon.
* Max Health below 40 is now shown with less max hearts.
* Increased the max length of a username display from 5 >> 9.
* Remove translations.
    * Dev comment: *`Fuck translations, learn english.`*.

---

# [🦸‍♀️] Hero Changes

Vortex
---

*The general gameplay of Vortex stays the same, but there are changes to Astral Stars.*

### Astral Star

* Is now **first** talent.
* To summon a star, it now costs **Max Health**.
* **The stars can now be destroyed.**
    * If the star is destroyed, the sacrificed health will **not** be returned!

There is a difference between **health** and **max health**.

Summoning stars spends **max health**, meaning you cannot regenerate past it.

*This change adds health management aspect.*

### Astral Vision

* Renamed "Star Aligner" >> "Astral Vision".
* Is now an Input Talent:


* **LEFT CLICKING** implodes the target star after a short delay, dealing AoE damage.
    * The damage is based on that star's current health.
    * **The sacrificed health will not be returned!**

*This change forces player to only use this ability in dire need.*

* **RIGHT CLICKING** links the player with the target star, teleporting you to it.
    * Teleport is not instant anymore.
    * Upon finishing teleport, collect the **star**, regain sacrificed health and regenerate health based on that
      star's remaining health.
    * The link damage can no longer crit.

### Like a Dream (Passive)

* The damage boost from the passive now applies to *any* **astral** damage.

### Arcana (Ultimate)

* Launch an Astral Energy in front of you that follows your crosshair.
* The energy deals rapid damage and knocks enemies back.

Any Vortex ability damage is considered **astral** damage, meaning it is boosted by the passive!

---

# [🐜] Bug Fixes

* **Fixed some hero stats not saving.**
* **Fixed Archer not being able to shoot if Triple Shot is locked.**

* Fixed Swayblade not counting as negative effect.
* Fixed Random Hero Preferences showing invalid archetype.
* Fixed Engineer's Turret shooting its own creator while in the mecha form.
* Fixed Taker not getting bones if enemy hit was recently damaged.
* Fixed Dice not removing after rolling a 6.
* Fixed Talent Lock overriding the longer lock.

---