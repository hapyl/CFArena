package me.hapyl.fight.game.heroes.knight;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.shield.Shield;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.knight.Discharge;
import me.hapyl.fight.game.talents.knight.StoneCastle;
import me.hapyl.fight.game.task.TimedGameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.fight.util.displayfield.DisplayFieldProvider;
import me.hapyl.fight.util.shield.PatternTypes;
import me.hapyl.fight.util.shield.ShieldBuilder;
import org.bukkit.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

public class BlastKnight extends Hero implements UIComponent, PlayerDataHandler<BlastKnightData>, DisplayFieldProvider {

    public final ItemStack shieldItem = new ShieldBuilder(DyeColor.BLACK)
            .with(DyeColor.WHITE, PatternTypes.DLS)
            .with(DyeColor.PURPLE, PatternTypes.MR)
            .with(DyeColor.BLACK, PatternTypes.DLS)
            .with(DyeColor.PINK, PatternTypes.MC)
            .with(DyeColor.BLACK, PatternTypes.FLO)
            .build();

    private final PlayerDataMap<BlastKnightData> dataMap = PlayerMap.newDataMap(BlastKnightData::new);
    private final Material shieldRechargeCdItem = Material.HORSE_SPAWN_EGG;

    public BlastKnight(@Nonnull DatabaseKey key) {
        super(key, "Blast Knight");

        setArchetypes(Archetype.SUPPORT, Archetype.DEFENSE);
        setAffiliation(Affiliation.KINGDOM);
        setGender(Gender.MALE);

        setDescription("A royal knight with high-end technology gadgets.");
        setItem("f6eaa1fd9d2d49d06a894798d3b145d3ae4dcca038b7da718c7b83a66ef264f0");

        final HeroAttributes attributes = getAttributes();
        attributes.setDefense(200);
        attributes.setSpeed(90);

        final Equipment equipment = getEquipment();

        equipment.setName("Quantum Suit");
        equipment.setDescription("""
                A suit that is capable of channeling &dQuantum Energy&7.
                """);
        equipment.setFlavorText("""
                A carefully crafted suit, made from unknown materials.
                It emits a purplish glow, and very warm energy.
                """);

        equipment.setChestPlate(20, 5, 43);
        equipment.setLeggings(170, 55, 204);
        equipment.setBoots(Material.NETHERITE_BOOTS);

        setWeapon(Material.IRON_SWORD, "Royal Sword", """
                A royal sword, forget of the best quality ore possible.
                                
                It has tiny golden ornate pieces on the edge of the handle.
                """, 5.0d);

        setUltimate(new BlastKnightUltimate());
    }

    @Override
    public void processDamageAsVictim(@Nonnull DamageInstance instance) {
        final GamePlayer player = instance.getEntityAsPlayer();
        final GameEntity damager = instance.getDamager();
        final double damage = instance.getDamage();

        if (!player.isBlocking() || damager == null || damage > 0.0d) {
            return;
        }

        final double dot = player.dot(damager.getLocation());

        if (dot <= 0.6d) {
            return;
        }

        final BlastKnightData data = getPlayerData(player);

        if (data.isShieldOnCooldown()) {
            return;
        }

        data.incrementShieldCharge();

        // Interrupt shield
        player.interruptShield();

        // Fx
        player.playSound(Sound.ITEM_SHIELD_BREAK, 1.0f);

        instance.multiplyDamage(0.0d);
    }

    @Nonnull
    @Override
    public PlayerDataMap<BlastKnightData> getDataMap() {
        return dataMap;
    }

    public int getShieldCharge(GamePlayer player) {
        return getPlayerData(player).getShieldCharge();
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        player.setItem(EquipmentSlot.OFF_HAND, shieldItem);
    }

    @Override
    public StoneCastle getFirstTalent() {
        return (StoneCastle) Talents.STONE_CASTLE.getTalent();
    }

    @Override
    public Discharge getSecondTalent() {
        return (Discharge) Talents.DISCHARGE.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.SHIELDED.getTalent();
    }

    @Nonnull
    @Override
    public String getString(@Nonnull GamePlayer player) {
        if (player.hasCooldown(shieldRechargeCdItem)) {
            return "&7🛡 &l" + player.getCooldownFormatted(shieldRechargeCdItem);
        }

        return "&5&l✨ &l" + getShieldCharge(player);
    }

    private class BlastKnightUltimate extends UltimateTalent {

        @DisplayField private final double ultimateRadius = 7.0d;
        @DisplayField private final double initialShieldCapacity = 10;
        @DisplayField private final double shieldCapacity = 50;

        public BlastKnightUltimate() {
            super("Nanite Rush", 60);

            setDescription("""
                    Instantly release a &dNanite Swarm&7 that &brushes&7 upwards, creating a &eshield&7 and rapidly &aregenerates&7 all existing shields.
                    """);

            setType(TalentType.SUPPORT);
            setItem(Material.PURPLE_DYE);
            setCooldownSec(30);
            setDuration(30);
        }

        @Nonnull
        @Override
        public UltimateResponse useUltimate(@Nonnull GamePlayer player) {
            final double shieldPerTick = (shieldCapacity - initialShieldCapacity) / getUltimateDuration();
            final Location location = player.getLocation();

            new TimedGameTask(getUltimate()) {
                @Override
                public void onFirstTick() {
                    nearbyPlayers().forEach(player -> {
                        player.setShield(new Shield(player, shieldCapacity, initialShieldCapacity));
                    });
                }

                @Override
                public void run(int tick) {
                    nearbyPlayers().forEach(player -> {
                        final Shield shield = player.getShield();

                        if (shield != null) {
                            shield.regenerate(shieldPerTick);
                        }
                    });

                    // Fx
                    final float pitch = 0.5f + (1.5f / maxTick * tick);

                    player.spawnWorldParticle(location, Particle.WITCH, 50, ultimateRadius / 4, 0.1d, ultimateRadius / 4, 1f);

                    player.playWorldSound(location, Sound.ITEM_FLINTANDSTEEL_USE, pitch);
                    player.playWorldSound(location, Sound.ENTITY_ENDER_DRAGON_FLAP, pitch);
                }

                private List<GamePlayer> nearbyPlayers() {
                    final List<GamePlayer> players = Collect.nearbyPlayers(location, ultimateRadius);
                    players.removeIf(other -> {
                        return !player.equals(other) && !player.isTeammate(other);
                    });

                    return players;
                }
            }.runTaskTimer(0, 1);

            return UltimateResponse.OK;
        }
    }
}
