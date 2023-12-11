package me.hapyl.fight.game.heroes.archive.knight;

import me.hapyl.fight.event.io.DamageInput;
import me.hapyl.fight.event.io.DamageOutput;
import me.hapyl.fight.game.PlayerElement;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.shield.Shield;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.Discharge;
import me.hapyl.fight.game.talents.archive.knight.StoneCastle;
import me.hapyl.fight.game.task.TimedGameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.fight.util.displayfield.DisplayFieldProvider;
import me.hapyl.fight.util.displayfield.DisplayFieldSerializer;
import me.hapyl.fight.util.shield.PatternTypes;
import me.hapyl.fight.util.shield.ShieldBuilder;
import org.bukkit.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

public class BlastKnight extends Hero implements PlayerElement, UIComponent, PlayerDataHandler, DisplayFieldProvider {

    public final ItemStack shieldItem = new ShieldBuilder(DyeColor.BLACK)
            .with(DyeColor.WHITE, PatternTypes.DLS)
            .with(DyeColor.PURPLE, PatternTypes.MR)
            .with(DyeColor.BLACK, PatternTypes.DLS)
            .with(DyeColor.PINK, PatternTypes.MC)
            .with(DyeColor.BLACK, PatternTypes.FLO)
            .build();
    private final PlayerMap<BlastKnightData> dataMap = PlayerMap.newMap();
    private final Material shieldRechargeCdItem = Material.HORSE_SPAWN_EGG;

    @DisplayField private final double ultimateRadius = 7.0d;
    @DisplayField private final double healingTotal = 30;
    @DisplayField private final double shieldCapacity = 20;

    public BlastKnight() {
        super("Blast Knight");

        setArchetype(Archetype.DEFENSE);
        setAffiliation(Affiliation.KINGDOM);

        setDescription("Royal Knight with high-end technology gadgets.");
        setItem("f6eaa1fd9d2d49d06a894798d3b145d3ae4dcca038b7da718c7b83a66ef264f0");

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

        setWeapon(Material.IRON_SWORD, "Royal Sword", "", 8.0d);

        setUltimate(new UltimateTalent(
                "Nanite Rush", """
                Instantly release a &dNanite Swarm&7 that &brushes&7 upwards, rapidly &ahealing&7 all nearby &ateammates&7 and granting them a &eshield&7.
                """, 60
        )
                .setType(Talent.Type.SUPPORT)
                .setItem(Material.PURPLE_DYE)
                .setCooldownSec(30)
                .setDuration(30));

        DisplayFieldSerializer.copy(this, getUltimate());
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        dataMap.removeAnd(player, PlayerData::remove);
    }

    @Override
    public void onStop() {
        dataMap.forEachAndClear(PlayerData::remove);
    }

    @Override
    public UltimateCallback useUltimate(@Nonnull GamePlayer player) {
        final double healingPerTick = healingTotal / getUltimateDuration();
        final Location location = player.getLocation();

        new TimedGameTask(getUltimate()) {
            @Override
            public void run(int tick) {
                nearbyPlayers().forEach(player -> {
                    player.heal(healingPerTick);
                });

                // Fx
                final float pitch = 0.5f + (1.5f / maxTick * tick);

                player.spawnWorldParticle(location, Particle.SPELL_WITCH, 50, ultimateRadius / 4, 0.1d, ultimateRadius / 4, 1f);

                player.playWorldSound(location, Sound.ITEM_FLINTANDSTEEL_USE, pitch);
                player.playWorldSound(location, Sound.ENTITY_ENDER_DRAGON_FLAP, pitch);
            }

            @Override
            public void onFirstTick() {
                nearbyPlayers().forEach(player -> {
                    player.setShield(new Shield(player, shieldCapacity));
                });
            }

            private List<GamePlayer> nearbyPlayers() {
                final List<GamePlayer> players = Collect.nearbyPlayers(location, ultimateRadius);
                players.removeIf(other -> {
                    return !player.equals(other) && !player.isTeammate(other);
                });

                return players;
            }
        }.runTaskTimer(0, 1);

        return UltimateCallback.OK;
    }

    @Override
    public DamageOutput processDamageAsVictim(DamageInput input) {
        final GamePlayer player = input.getEntityAsPlayer();
        final GameEntity damager = input.getDamager();
        final double damage = input.getDamage();

        if (!player.isBlocking() || damager == null || damage > 0.0d) {
            return DamageOutput.OK;
        }

        final double dot = player.dot(damager.getLocation());

        if (dot <= 0.6d) {
            return DamageOutput.OK;
        }

        final BlastKnightData data = getPlayerData(player);

        if (data.isShieldOnCooldown()) {
            return DamageOutput.OK;
        }

        data.incrementShieldCharge();

        // Interrupt shield
        player.setItem(EquipmentSlot.OFF_HAND, null);
        player.schedule(() -> player.setItem(EquipmentSlot.OFF_HAND, shieldItem), 1);

        // Fx
        player.playSound(Sound.ITEM_SHIELD_BREAK, 1.0f);

        return new DamageOutput(0.0d, false);
    }

    @Nonnull
    @Override
    public BlastKnightData getPlayerData(@Nonnull GamePlayer player) {
        return dataMap.computeIfAbsent(player, BlastKnightData::new);
    }

    public int getShieldCharge(GamePlayer player) {
        return getPlayerData(player).getShieldCharge();
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        player.setItem(EquipmentSlot.OFF_HAND, shieldItem);
    }

    @Override
    public void onRespawn(@Nonnull GamePlayer player) {
        onStart(player);
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
}
