package me.hapyl.fight.game.heroes.archive.swooper;

import me.hapyl.fight.CF;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.heroes.UltimateResponse;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.swooper.SwooperPassive;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.talents.archive.witcher.Akciy;
import me.hapyl.fight.game.ui.UIComplexComponent;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.WorldBorder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Swooper extends Hero implements Listener, UIComplexComponent, PlayerDataHandler<SwooperData>, TickingHero {

    private final PlayerDataMap<SwooperData> playerData = PlayerMap.newDataMap(player -> new SwooperData(this, player));

    private final double knockbackChance = 0.12d;
    private final int stunDuration = 40;

    public Swooper(@Nonnull Heroes handle) {
        super(handle, "Swooper");

        setArchetype(Archetype.RANGE);
        setAffiliation(Affiliation.MERCENARY);
        setGender(Gender.MALE);

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
        setUltimate(new SwooperUltimate());
    }

    @Override
    public boolean processInvisibilityDamage(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity, double damage) {
        return false; // Allow damaging while invisible lol
    }

    @Override
    public boolean isValidIfInvisible(@Nonnull GamePlayer player) {
        return true; // This will allow Swooper to be damaged and abilities to work against him
    }

    @Override
    public void processDamageAsDamager(@Nonnull DamageInstance instance) {
        final LivingGameEntity entity = instance.getEntity();
        final GamePlayer player = instance.getDamagerAsPlayer();
        final EnumDamageCause cause = instance.getCause();

        if (player == null) {
            return;
        }

        final SwooperData data = getPlayerData(player);

        // Butt
        if (cause == EnumDamageCause.ENTITY_ATTACK) {
            data.lastButt = Math.max(0, data.lastButt - 1);

            if (data.lastButt == 0 && player.random.nextDouble() <= knockbackChance) {
                final Vector vector = player.getLocation().getDirection().normalize().multiply(0.5d);

                data.lastButt = 5;

                entity.setVelocity(vector);
                Talents.AKCIY.getTalent(Akciy.class).stun(entity, stunDuration);

                // Fx
                entity.playWorldSound(Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 0.0f);
            }
        }

        if (!data.isHighlighted(entity)) {
            return;
        }

        data.removeHighlighted(entity);
    }

    @Override
    public void processDamageAsVictim(@Nonnull DamageInstance instance) {
        final GamePlayer player = instance.getEntityAsPlayer();
        final SwooperData data = getPlayerData(player);

        if (data.isStealthMode()) {
            data.setStealthMode(false);
        }
    }

    @Override
    public void tick(int tick) {
        final SwooperPassive passiveTalent = getPassiveTalent();

        getAlivePlayers().forEach(player -> {
            if (player.hasCooldown(passiveTalent)) {
                return;
            }

            // Passive check
            final SwooperData data = getPlayerData(player);
            final boolean isSneaking = player.isSneaking();

            data.sneakTicks = isSneaking ? Math.max(data.sneakTicks + 1, 0) : 0;

            final boolean canActivePassive = data.sneakTicks >= passiveTalent.sneakThreshold;

            if (data.isStealthMode()) {
                if (!canActivePassive || data.isTooFarAwayFromNest()) {
                    data.sneakTicks = 0;
                    data.setStealthMode(false);
                }

                // Fx
                data.drawNestParticles();
            }
            else if (canActivePassive) {
                if (!data.isStealthMode()) {
                    data.setStealthMode(true);
                }
            }

            // Display
            if (data.sneakTicks > 2 && !data.isStealthMode()) {
                player.sendSubtitle(data.makeBars(), 0, 10, 0);
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
    public List<String> getStrings(@Nonnull GamePlayer player) {
        final SwooperData data = getPlayerData(player);
        final SwooperPassive talent = getPassiveTalent();

        final boolean stealthMode = data.isStealthMode();
        final int ultimateShots = data.ultimateShots;

        final int cdTimeLeft = talent.getCdTimeLeft(player);

        return List.of(
                "%s".formatted(stealthMode ? "&3&k| &b&l⛺ &3&k|" : ("&8⛺" +
                        (cdTimeLeft > 0 ? " " + CFUtils.decimalFormatTick(cdTimeLeft) : ""))),
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

    @Override
    public SwooperUltimate getUltimate() {
        return (SwooperUltimate) super.getUltimate();
    }

    public class SwooperUltimate extends UltimateTalent {

        @DisplayField public final double ultimateRadius = 100.0d;
        @DisplayField public final double ultimateDamageMultiplier = 2.5d;

        public SwooperUltimate() {
            super("Echolocation", 60);

            setDescription("""
                    Instantly cast a &awave&7 outwards that &bscans&7 for enemies.
                                    
                    After a short delay, &bhighlight&7 all hit &cenemies&7.
                                    
                    Also, gain &4&l🧨 &cOvercharge&7 shots, that have &aincreased &cdamage&7 and can &nshoot&7 &nthrough&7 walls.
                                    
                    &8;;Ultimate is considered as active until all Overcharge shots are fired.
                    """);

            setType(Talent.Type.ENHANCE);
            setItem(Material.PURPLE_GLAZED_TERRACOTTA);
            setSound(Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, 2.0f);
            setCastDuration(20);

        }

        @Override
        public int getDuration() {
            return -1; // because of casting time
        }

        @Nonnull
        @Override
        public UltimateResponse useUltimate(@Nonnull GamePlayer player) {
            player.setUsingUltimate(true);

            final WorldBorder border = Bukkit.createWorldBorder();
            final SwooperData data = getPlayerData(player);

            border.setCenter(player.getLocation());
            border.setSize(2.0d);
            border.setSize(ultimateRadius * 2, TimeUnit.MILLISECONDS, getUltimate().getCastDuration() * 50L);

            player.setWorldBorder(border);

            // Fx
            player.playWorldSound(Sound.ENTITY_ELDER_GUARDIAN_CURSE, 0.75f);
            player.playWorldSound(Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1.25f);

            return new UltimateResponse() {
                @Override
                public void onCastFinished(@Nonnull GamePlayer player) {
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
    }
}
