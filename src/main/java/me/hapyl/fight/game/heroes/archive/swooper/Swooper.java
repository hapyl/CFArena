package me.hapyl.fight.game.heroes.archive.swooper;

import me.hapyl.fight.CF;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.MoveType;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.swooper.SwooperPassive;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.ui.UIComplexComponent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Swooper extends Hero implements Listener, UIComplexComponent, PlayerDataHandler<SwooperData>, TickingHero {

    private final PlayerDataMap<SwooperData> playerData = PlayerMap.newDataMap(SwooperData::new);

    private final double ultimateRadius = 25.0d;
    private final BlockData passiveEffectBlockData = Material.LIGHT_GRAY_CONCRETE.createBlockData();

    public Swooper(@Nonnull Heroes handle) {
        super(handle, "Swooper");

        setArchetype(Archetype.RANGE);
        setAffiliation(Affiliation.MERCENARY);

        setDescription("""
                A mercenary sniper with a slow firing rifle.
                """);

        setItem("f181c811ad37467550d7c01cac2e5223c4e99fa7906348f940c9456d8aa0cd1b");

        final HeroAttributes attributes = getAttributes();
        attributes.set(AttributeType.SPEED, 0.23d);

        final Equipment equipment = this.getEquipment();
        equipment.setChestPlate(25, 53, 82);
        equipment.setLeggings(25, 53, 92);
        equipment.setBoots(25, 53, 102);

        setWeapon(new SwooperWeapon(this));

        setUltimate(new UltimateTalent(this, "Echolocation", """
                Instantly cast a &awave&7 outwards that &bscans&7 for enemies.
                                
                After a short delay, &bhighlight&7 all hit &cenemies&7.
                                
                Also, gain &4&l🧨 &cOvercharge&7 shots, that have &aincreased &cdamage&7 and can &nshoot&7 &nthrough&7 walls.
                                
                &8;;Ultimate is considered as active until all Overcharge shots are fired.
                """, 60) {
            @Override
            public int getDuration() {
                return -1; // because of casting time
            }
        }
                .setType(Talent.Type.ENHANCE)
                .setItem(Material.PURPLE_GLAZED_TERRACOTTA)
                .setSound(Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, 2.0f)
                .setCastDuration(20));
    }

    @Override
    public void processDamageAsDamager(@Nonnull DamageInstance instance) {
        final LivingGameEntity entity = instance.getEntity();
        final GamePlayer player = instance.getDamagerAsPlayer();

        if (player == null) {
            return;
        }

        final SwooperData data = getPlayerData(player);

        if (!data.isHighlighted(entity)) {
            return;
        }

        data.removeHighlighted(entity);
    }

    @Override
    public void tick(int tick) {
        final SwooperPassive passiveTalent = getPassiveTalent();

        getAlivePlayers().forEach(player -> {
            // Passive check
            final SwooperData data = getPlayerData(player);
            final boolean hasMoved = player.hasMovedInLast(MoveType.KEYBOARD, passiveTalent.standStillTime * 50L);
            final boolean canActivePassive = player.getSneakTicks() > passiveTalent.standStillTime && !hasMoved;

            if (!canActivePassive && data.isStealthMode()) {
                data.setStealthMode(false);
            }
            else if (canActivePassive) {
                if (!data.isStealthMode()) {
                    data.setStealthMode(true);
                }

                // Fx
                final Location location = player.getEyeLocation();

                CF.getPlayers().forEach(other -> {
                    if (player.equals(other)) {
                        return;
                    }

                    other.spawnParticle(location, Particle.BLOCK_DUST, 5, 0.15d, 0, 0.15d, 0, passiveEffectBlockData);
                });
            }

            // Zoom check
            final HotbarSlots heldSlot = player.getHeldSlot();
            if (heldSlot != HotbarSlots.WEAPON) {
                player.removePotionEffect(PotionEffectType.SLOW);
            }
        });
    }

    @Nullable
    @Override
    public UltimateCallback useUltimate(@Nonnull GamePlayer player) {
        setUsingUltimate(player, true);

        final WorldBorder border = Bukkit.createWorldBorder();
        final SwooperData data = getPlayerData(player);

        border.setCenter(player.getLocation());
        border.setSize(2.0d);
        border.setSize(ultimateRadius * 2, TimeUnit.MILLISECONDS, getUltimate().getCastDuration() * 50L);

        player.setWorldBorder(border);

        // Fx
        player.playWorldSound(Sound.ENTITY_ELDER_GUARDIAN_CURSE, 0.75f);
        player.playWorldSound(Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1.25f);

        return new UltimateCallback() {
            @Override
            public void callback(@Nonnull GamePlayer player) {
                player.setWorldBorder(null);

                final int enemyCount = Collect.enemyPlayers(player).size();

                data.ultimateShots = enemyCount <= 1 ? 2 : 3;

                Collect.nearbyEntities(player.getLocation(), ultimateRadius).forEach(entity -> {
                    if (player.isSelfOrTeammate(entity)) {
                        return;
                    }

                    data.addHighlighted(entity);

                    // Fx
                    player.playSound(entity.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1.25f);
                });
            }
        };
    }

    @Nullable
    @Override
    public List<String> getStrings(@Nonnull GamePlayer player) {
        final SwooperData data = getPlayerData(player);

        final boolean stealthMode = data.isStealthMode();
        final int ultimateShots = data.ultimateShots;

        return List.of(
                "%s".formatted(stealthMode ? "&3&k| &b&l⛺ &3&k|" : "&8⛺"),
                ultimateShots > 0 ? "&4&l🧨 &c&l" + ultimateShots : ""
        );
    }

    @EventHandler()
    public void handleSniperScope(PlayerToggleSneakEvent ev) {
        final GamePlayer player = CF.getPlayer(ev);

        if (player == null || !validatePlayer(player)) {
            return;
        }

        if (player.getHeldSlot() != HotbarSlots.WEAPON) {
            return;
        }

        if (ev.isSneaking()) {
            player.addPotionEffectIndefinitely(PotionEffectType.SLOW, 4);
            player.playWorldSound(Sound.ITEM_SPYGLASS_USE, 1.25f);
        }
        else {
            player.removePotionEffect(PotionEffectType.SLOW);
            player.playWorldSound(Sound.ITEM_SPYGLASS_USE, 0.75f);
        }
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.BLAST_PACK.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.SWOOPER_SMOKE_BOMB.getTalent();
    }

    @Override
    public SwooperPassive getPassiveTalent() {
        return (SwooperPassive) Talents.SWOOPER_PASSIVE.getTalent();
    }

    @Nonnull
    @Override
    public PlayerDataMap<SwooperData> getDataMap() {
        return playerData;
    }


}
