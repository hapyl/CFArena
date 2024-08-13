package me.hapyl.fight.game.heroes.techie;

import me.hapyl.fight.event.custom.GameDeathEvent;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperInstance;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.TalentLock;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.heroes.UltimateResponse;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.techie.DeviceHack;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.ShutdownAction;
import me.hapyl.fight.game.ui.UIComplexComponent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.fight.util.displayfield.DisplayFieldProvider;
import me.hapyl.eterna.module.hologram.Hologram;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.player.PlayerSkin;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

public class Techie extends Hero implements UIComplexComponent, Listener, PlayerDataHandler<TechieData>, DisplayFieldProvider {

    private final int neuralTheftPeriod = 200;
    private final int neuralTheftDuration = 40;
    private final int neuralTheftEnergy = 4;

    private final PlayerDataMap<TechieData> playerData = PlayerMap.newDataMap(TechieData::new);
    private final String neuralTheftTitle = "&3&lɴᴇᴜʀᴀʟ ᴛʜᴇғᴛ";

    private final TemperInstance temperInstance = Temper.LOCKDOWN
            .newInstance()
            .decrease(AttributeType.SPEED, 0.1) // 50%
            .decrease(AttributeType.ATTACK_SPEED, 0.5);

    public Techie(@Nonnull Heroes handle) {
        super(handle, "Cryptshade");

        setArchetypes(Archetype.HEXBANE);
        setAffiliation(Affiliation.UNKNOWN);
        setGender(Gender.UNKNOWN);
        setRace(Race.CYBERNETIC);

        setDescription("""
                Anonymous hacker, who hacked his way to the fight. Specializes in locking enemies talents.
                """);

        setItem("4e3b15e5eb0ada16e2e1751644bdc28e0ceae8d398439a6b8037d4da097b9c37");

        setSkin(new PlayerSkin(
                "ewogICJ0aW1lc3RhbXAiIDogMTYwMzY0NTY4MTg5NywKICAicHJvZmlsZUlkIiA6ICI3MjI2Mjg2NzYyZWY0YjZlODRlMzc2Y2JkYWNhZjU1NyIsCiAgInByb2ZpbGVOYW1lIiA6ICJicmFuZG9uZDI2IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzRlM2IxNWU1ZWIwYWRhMTZlMmUxNzUxNjQ0YmRjMjhlMGNlYWU4ZDM5ODQzOWE2YjgwMzdkNGRhMDk3YjljMzciCiAgICB9CiAgfQp9",
                "DYpJG7/gq5paUh9/xrymHlyg1pI5vQ5rWmU8/x+RdUInYVa0TO4Of5b+V1USEg3yGVG3ncwfuOim9kK7qbDXW+Hg0wYbgXr9UYHA3MegKDhov/+DVWPowAQ/FOnNuMhVgG0hFT2UDn8sl2VtaRZbYn3Z8w28By7/pp/9jST38Jcf98aA/JCHpatVGw8hJIlcy5fsAUzujULDUNclfml6jzjoahHOo9A2JYR3wdzaV8bRyTdYLVvyenMUq3y6IcQGnqKV3hfOwrtlP2AXDI8YyUZbf2ISfc+47D6tJeCxAJJQ8rViWgZbLR+Ld7qJq9mQOuZkhZ4+XPQ1FonMTZ5RBhEEn+djoui6JHB/nGPvRIqjBO02PWhXylrjQber8qhRRiD53cx+FIyq9Ccqq4Uh6uhtrbNCxJuouPrjsOdD8uqkM4Hyj75jfG71aYJrygB0M5z7P6NmHbnYYG4tUa5bvz1/YnZymUq8re6X5qDzfBGSMn7LsU/EBwSzmyg04rHlr8xI8yFZMKOBJi8PbwYf7z5E/atA46eqHjeOQiOcso6aY+6GqeF9Upd8OybGDA1SU+RfREVZCNk91MHxwhJrtU8yTMxiL70n7YRmek4hkiOfrdkqrgER6p/1lftJsjpU9MYtN0S1mN/oeong9MVE0EVmgapW4y+Zi4mEGBodqsg="
        ));

        final HeroAttributes attributes = getAttributes();
        attributes.setSpeed(110);

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(245, 245, 245, TrimPattern.TIDE, TrimMaterial.NETHERITE);
        equipment.setLeggings(Material.NETHERITE_LEGGINGS, TrimPattern.SILENCE, TrimMaterial.NETHERITE);
        equipment.setBoots(Material.NETHERITE_BOOTS, TrimPattern.WARD, TrimMaterial.NETHERITE);

        setWeapon(new Weapon(Material.IRON_SWORD).setName("Nano Sword")
                .setDescription("""
                        A sword made with nano energy.
                        """)
                .setDamage(4.0d)
                .addEnchant(Enchantment.KNOCKBACK, 1));

        setUltimate(new TechieUltimate());
    }

    @EventHandler()
    public void handleGameDeathEvent(GameDeathEvent ev) {
        final LivingGameEntity entity = ev.getEntity();

        playerData.forEach((player, data) -> data.remove(entity));
    }

    public void revealEntity(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity, @Nonnull Set<BugType> bugs) {
        entity.setGlowing(player, ChatColor.AQUA, neuralTheftDuration);

        final Hologram hologram = new Hologram()
                .create(entity.getLocationToTheLeft(1.5).add(0, 0.5, 0))
                .setLines(
                        neuralTheftTitle, // todo: Maybe add some classified name for lore here
                        "&fName: " + entity.getName(),
                        "&cHealth: " + entity.getHealthFormatted()
                );

        if (entity instanceof GamePlayer gamePlayer) {
            hologram.addLine("&bUltimate: " + gamePlayer.getUltimateString());
        }

        final StringBuilder builder = new StringBuilder();
        for (BugType bug : bugs) {
            builder.append(bug.getName()).append(" ");
        }

        hologram.addLine("&4Bugs: " + builder.toString().trim());
        hologram.updateLines();

        hologram.show(player.getPlayer());

        GameTask.runLater(hologram::destroy, neuralTheftDuration).setShutdownAction(ShutdownAction.IGNORE);

        // Sfx
        player.playSound(Sound.ENCHANT_THORNS_HIT, 1.75f);
    }

    @Override
    public void onStart(@Nonnull GameInstance instance) {
        new GameTask() {
            @Override
            public void run() {
                for (GamePlayer player : getAlivePlayers()) {
                    final TechieData data = getPlayerData(player);

                    data.removeDead();
                    int energyStolen = 0;

                    for (LivingGameEntity entity : data) {
                        player.getTeam().getPlayers().forEach(teammate -> {
                            revealEntity(teammate, entity, data.getBugs(entity));
                        });

                        // Steal energy
                        if (!player.isUltimateReady() && entity instanceof GamePlayer entityPlayer) {
                            energyStolen += neuralTheftEnergy;
                            entityPlayer.setEnergy(entityPlayer.getEnergy() - neuralTheftEnergy);
                            entityPlayer.spawnDebuffDisplay(neuralTheftTitle, 20);
                        }
                    }

                    if (energyStolen > 0) {
                        player.addEnergy(energyStolen);
                        player.spawnBuffDisplay(neuralTheftTitle + " &b+" + energyStolen + Named.ENERGY.getCharacter(), 20);
                    }
                }
            }
        }.runTaskTimer(neuralTheftPeriod, neuralTheftPeriod);
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.SABOTEUR.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.CIPHER_LOCK.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.NEURAL_THEFT.getTalent();
    }

    @Override
    public List<String> getStrings(@Nonnull GamePlayer player) {
        final TechieData data = getPlayerData(player);

        return List.of(Named.BUG.getCharacter() + "&f " + data.buggedSize());
    }

    @Nonnull
    @Override
    public PlayerDataMap<TechieData> getDataMap() {
        return playerData;
    }

    private class TechieUltimate extends UltimateTalent {

        @DisplayField private final int lockdownTalentLockDuration = Tick.fromSecond(30);
        @DisplayField private final double ultimateDistance = 20;
        @DisplayField(percentage = true) private final double ultimateLosePercent = 0.75d;

        public TechieUltimate() {
            super("Lockdown", 90);

            setDescription("""
                    Equip a &bhacking device&7; after a &nlong&7 &3casting time&7, &coverload&7 all implanted %s&fs.
                                    
                    &cOverloading&7 the &fbugs&7 &cimplodes&7 them, causing affected enemies' &btalents&7 to be &dlocked&7.
                    &8;;Overloading bugs causes them to break.
                                    
                    In addition, all &cenemies&7 &4lose&7 &b&n{ultimateLosePercent}&7 of their %s.
                    """.formatted(Named.BUG, Named.ENERGY));

            setType(TalentType.IMPAIR);
            setItem(Material.IRON_BARS);
            setCooldownSec(30);
        }

        @Nonnull
        @Override
        public UltimateResponse useUltimate(@Nonnull GamePlayer player) {
            new DeviceHack() {
                private double distance = 1;
                private final double distancePerTick = (ultimateDistance - distance) / getCastingTime();

                @Override
                public void onHack(@Nonnull GamePlayer player) {
                    final TechieData data = getPlayerData(player);

                    data.forEachAndRemove((entity) -> {
                        boolean shouldRemove = entity.getLocation().distance(player.getLocation()) <= distance;

                        if (!shouldRemove) {
                            return false;
                        }

                        temperInstance.temper(entity, lockdownTalentLockDuration);

                        if (!(entity instanceof GamePlayer entityPlayer)) {
                            return true;
                        }

                        final TalentLock talentLock = entityPlayer.getTalentLock();
                        talentLock.setLockAll(lockdownTalentLockDuration);

                        entityPlayer.setEnergy((int) (entityPlayer.getEnergy() * (1 - ultimateLosePercent)));

                        // Fx
                        entityPlayer.sendSubtitle("&4&lʟᴏᴄᴋᴅᴏᴡɴ", 5, 20, 5);
                        entityPlayer.playSound(Sound.BLOCK_ANVIL_LAND, 0.0f);

                        return true;
                    });
                }

                @Override
                public void onTick(@Nonnull GamePlayer player, int tick) {
                    final Location location = player.getLocation();

                    // Area Fx
                    for (double d = 0; d < Math.PI * 2; d += Math.PI / (tick + 2)) {
                        final double x = Math.sin(d) * distance;
                        final double y = Math.sin(Math.toRadians(tick / 2.0d)) * 0.2;
                        final double z = Math.cos(d) * distance;

                        location.add(x, y, z);

                        player.spawnWorldParticle(location, Particle.WITCH, 1);
                        player.spawnWorldParticle(location, Particle.EFFECT, 1);

                        location.subtract(x, y, z);
                    }

                    // Player Fx
                    player.spawnWorldParticle(Particle.WITCH, 10, 0.1d, 0.6d, 0.1d, 1);
                    player.spawnWorldParticle(Particle.ENCHANTED_HIT, 10, 0.1d, 0.6d, 0.1d, 1);

                    player.playWorldSound(Sound.ENTITY_IRON_GOLEM_HURT, (float) (1.0f + (1.0f / ultimateDistance * distance)));

                    distance += distancePerTick;
                }

                @Override
                public int getCastingTime() {
                    return 40;
                }
            }.startDevice(player);

            return UltimateResponse.OK;
        }
    }
}
