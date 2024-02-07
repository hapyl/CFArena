# [⚙] System Changes

* Bows and Range Weapons now have a cooldown and are affected by **ATTACK SPEED**.
* Changes the winning screen.
* Migrated to use custom effects rather than vanilla ones.

---

# [🆕] New Heroes

Shaman
---

*A **SUPPORT** hero, focused on healing and buffer teammate's damage.*

### Totem

* Toss either a damaging or healing totem.

### Imprisonment

* Imprison the target enemy in a stone cage, impairing their movement.

### Shaman's Mark

* Launch a projectile that will link with a teammate, boosting their **SPEED**, **ATTACK** and **ATTACK SPEED**.

### Overheal (Passive)

* When healing a teammate (or self) whom health is already max, the overflow healing will be converted into Overheal.
  When a teammate (or self!!!) deals damage, it will be increased by a percentage of Overheal.

### Spiritual Cleansing (Ultimate)

* Instantly cleanse all negative effects from nearby teammates and increase their **EFFECT RESISTANCE**.

Heavy Knight
---

*A **DAMAGE** hero, focused on chaining talents.*

### Uppercut

* Perform an uppercut attack with your Claymore, dealing damage and knocking enemies up.

### Touchdown

* While airborne, perform a plunging attack, dealing damage upon landing.
* If there are enemies at the same height level as you, push them down with you.

### Slash

* Creates a slash in front of you, dealing damage and knocking enemies back.

### Perfect Sequence (Passive)

* If Uppercut >> Touchdown >> Slash used in the correct order, empower the Slash and reset its cooldown.

### Ultimate Sacrifice (Ultimate)

* Sacrifice your armor to gain more power, becoming a glass cannon.

Rogue
---

*A **DAMAGE** hero, who relies on 'dying'.*

### Throwing Knife

* Throw a knife forward, damage and impairing the first enemy hit.

### Swayblade

* Hit all enemies in front of you with the hilt of your blade, impairing their vision.

### Second Wind (Passive)

* When taking lethal damage, instead of dying, gain Second Wind for short duration.
    * Increases **ATTACK**.
    * Decreases **COOLDOWN MODIFIER**.
    * Creates a shield.
        * If the shield breaks, you die.
        * If the shield is not broken after the duration ends, convert a given percentage of remaining shield into
          healing.

### Pipe Bomb (Ultimate)

* Toss a pipe bomb in front of you, that explodes upon contact with a block or an enemy.
* If the explosion hits at least one enemy, damage them, apply bleeding and refresh Second Wind charges.

---

# [🦸‍♀️] Hero Changes

Witcher
---

### Stun

- Changes how stun works.

Harbinger
---

### Melee Stance

* Crit increase reduced 50% >> 30%.

Shadow Assassin
---

### Shadow Clone (Fury)

* Changed the ability:
* Before:
    * Spawn three clones that deal damage and debuff enemies in AoE.
* After:
    * Spawn up to three clones behind up to three enemies that deal damage.
* Reduce energy cost 60 >> 50.

Nightmare
---

### Omen (Passive)

* Increase damage multiplier 125% >> 150%.
* Optimize particles so it's easier to see enemies.

Juju
---

### Climb (Passive)

* Optimize climbing.

Zealot
---

*Zealot is now a **DAMAGE** type hero, instead of **HEXBANE**.*

### Attributes

* Increase base **FEROCITY** 25 >> 50.

### Weapon

* Reduce base damage 3.0 >> 2.5.

### Soul Cry (Weapon Ability)

* Increase **SPEED** increase 15% >> 20%.
* Increase **FEROCITY** increase 75% >> 100%.
* Decrease duration 4s >> 3s.
* Decrease cooldown 16s >> 13s.

### Ferocious Strikes (New Passive)

* Upon scoring a ferocity, gain one stack of Ferocious Strike.
* Each stack improves ultimate damage.

### Maintain Order (New Ultimate)

* Command a giant sword to fall from the sky.
* Upon landing, creates an explosion and executes ferocity on all nearby enemies based on your Ferocious Strike stacks.

---

# [🐜] Bug Fixes

* **Fixed a bug where `Entity Attack` could not crit.**
* **Fixed a bug where rejoining the game with a random hero selected would not set the right hero properly.**

* Fixed a bug where player could get exclusive cosmetics from a crate.
* Fixed some abilities being able to crit when they shouldn't.
* Fixed a bug where teammates could not see each other pings.
* Fixed Broken Heart Radiation not being removed when the game ends.
* Fixed some talents that apply blinding doing it wrong.
* Fixed Mage teleport not centering on a block.
* Fixed Nightmare not being able to find a target.
* Fixed Shadow Assassin's Shadow Clone being able to hit teammates.
* Fixed Ninja not having **SPEED** at the start of the game.
* Fixed player screen (red outline) not resetting properly.
* Fixed Witcher's Combo not showing properly.
* Fixed invisibility hiding the wrong player.
* Fixed some abilities not anchoring the location properly.
* Fixed Pytaria's Feel the Breeze lock location being wrong.
* Fixed player taking fall damage in Dwarf's Vault lava.

**And many more!**

---