package me.hapyl.fight.game.heroes.archive.dark_mage;

import com.google.common.collect.Maps;
import me.hapyl.fight.CF;
import me.hapyl.fight.annotate.KeepNull;
import me.hapyl.fight.event.io.DamageInput;
import me.hapyl.fight.event.io.DamageOutput;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.cosmetic.Cosmetics;
import me.hapyl.fight.game.cosmetic.archive.GroundPunchCosmetic;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.ComplexHero;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.archive.witcher.WitherData;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.dark_mage.ShadowClone;
import me.hapyl.fight.game.talents.archive.dark_mage.ShadowCloneNPC;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIFormat;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Collect;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.reflect.Reflect;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;

import static org.bukkit.Sound.*;

public class DarkMage extends Hero implements ComplexHero, Listener {

    private final Map<Player, DarkMageSpell> spellMap = Maps.newHashMap();
    private final Map<Player, WitherData> withers = Maps.newHashMap();
    private final double PASSIVE_CHANCE = 0.12d;

    public DarkMage() {
        super("Dark Mage");

        setArchetype(Archetype.MAGIC);

        setDescription("A mage that was cursed by &8&lDark &8&lMagic&8&o. But even it couldn't kill him...");
        setItem("e6ca63569e8728722ecc4d12020e42f086830e34e82db55cf5c8ecd51c8c8c29");

        final HeroAttributes attributes = getAttributes();
        attributes.setValue(AttributeType.CRIT_CHANCE, 0.15d);

        final Equipment equipment = this.getEquipment();
        equipment.setChestPlate(102, 255, 255);
        equipment.setLeggings(Material.IRON_LEGGINGS);
        equipment.setBoots(153, 51, 51);

        setWeapon(new Weapon(Material.WOODEN_HOE)
                .setName("Ancient Wand")
                .setDamage(7.0d)
                .setDescription("""
                        An ancient item capable of casting the darkest of spells...
                                                
                        &e&lSpell Mode:
                        &6&lRIGHT&7 click to enter spell mode.
                                                
                        Add buttons with &nright&7 or &nleft&7 clicks (R or L). Two buttons activate the corresponding spells.
                                                
                        &b;;Hover over the talents to see their usage.
                        """));
        //"A powerful wand, that's capable of casting multiple spells!____&e&lRIGHT CLICK &7to enter casting, then, combine &e&lRIGHT CLICK &7and/or &e&lLEFT CLICK &7to execute the spell!"

        setUltimate(new UltimateTalent("Witherborn", """
                Summon a baby wither that will assist you in battle for {duration}.
                                
                While attacking, the wither will unleash a coordinated attack.
                                
                While casting a spell, the wither will improve the spell.
                """, 70)
                .setItem(Material.WITHER_SKELETON_SKULL)
                .setDuration(240)
                .setCooldownSec(30)
                .setSound(Sound.ENTITY_WITHER_SPAWN, 2.0f));
    }

    @Override
    public void onDeath(Player player) {
        killWither(player);
    }

    @Override
    public void onStop() {
        withers.values().forEach(WitherData::remove);
        withers.clear();
    }

    @Override
    public void useUltimate(Player player) {
        killWither(player);
        withers.put(player, new WitherData(player));
    }

    @Override
    public void onUltimateEnd(Player player) {
        killWither(player);
    }

    public void useUltimateOld(Player player) {
        player.setAllowFlight(true);
        player.setFlying(true);

        final double playerHealth = GamePlayer.getPlayer(player).getHealth();
        CFUtils.hidePlayer(player);

        final Wither wither = Entities.WITHER.spawn(player.getLocation(), me -> {
            me.setAI(false);
            me.setMaxHealth(playerHealth);
            me.setHealth(playerHealth);
            me.setCustomName(player.getName());
            me.setCustomNameVisible(true);
            me.setGlowing(true);
            me.setInvulnerable(false); // killable eya
        });

        updateWitherName(player, wither);
        Reflect.hideEntity(wither, player);

        new GameTask() {
            private int tick = getUltimateDuration();

            @Override
            public void run() {

                if (wither.isDead() || GamePlayer.getPlayer(player).isDead()) {
                    killWither(!wither.isDead() ? null : player, wither);
                    this.cancel();
                    return;
                }

                if (tick < 0) {
                    killWither(player, wither);
                    plungeAttack(player);
                    this.cancel();
                    return;
                }

                if (tick % 10 == 0) {
                    updateWitherName(player, wither);
                }

                wither.teleport(player);
                --tick;
            }

            private void plungeAttack(Player player) {
                final int maxPlungeTime = 100;
                GamePlayer.getPlayer(player).addEffect(GameEffectType.FALL_DAMAGE_RESISTANCE, maxPlungeTime, true);
                player.setVelocity(new Vector(0.0d, -0.5d, 0.0d));

                new GameTask() {
                    private int maxAirTicks = maxPlungeTime;

                    @Override
                    public void run() {
                        if (maxAirTicks-- <= 0 || player.isOnGround()) {
                            this.cancel();
                            Cosmetics.GROUND_PUNCH.getCosmetic(GroundPunchCosmetic.class).playAnimation(player.getLocation(), 2);

                            Collect.nearbyPlayers(player.getLocation(), 4).forEach(target -> {
                                if (target.is(player)) {
                                    return;
                                }

                                target.damage(5.0d, CF.getPlayer(player));
                            });
                        }
                    }
                }.runTaskTimer(0, 1);
            }

            private void killWither(@Nullable Player player, Wither wither) {
                if (player != null) {
                    player.setFlying(false);
                    player.setAllowFlight(false);
                    CFUtils.showPlayer(player);
                }
                PlayerLib.playSound(wither.getLocation(), ENTITY_WITHER_DEATH, 1.0f);
                wither.remove();
            }
        }.runTaskTimer(0, 1);
    }

    @Nullable
    @Override
    public DamageOutput processDamageAsVictim(DamageInput input) {
        final ShadowClone talent = getFourthTalent();
        final Player player = input.getEntityAsPlayer().getPlayer();
        final LivingGameEntity entity = input.getDamagerAsLiving();
        final ShadowCloneNPC clone = talent.getClone(player);

        // Handle passive
        if (entity != null && new Random().nextDouble() < PASSIVE_CHANCE) {
            entity.addEffect(GameEffectType.WITHER_BLOOD, 60, true);

            entity.sendMessage("&8☠ &c%s poisoned your blood!", player.getName());
            Chat.sendMessage(player, "&8☠ &aYou poisoned %s's blood!", entity.getName());
        }

        // Handle clone
        if (clone != null && clone.ultimate) {
            player.teleport(clone.getLocation());
            talent.removeClone(clone);

            // Fx
            Chat.sendMessage(player, "&aYour %s nullified the damage!", talent.getName());

            PlayerLib.addEffect(player, PotionEffectType.BLINDNESS, 10, 1);

            PlayerLib.playSound(player, ENTITY_ENDERMAN_TELEPORT, 1.0f);
            PlayerLib.playSound(player, ENTITY_PLAYER_BREATH, 1.0f);
            PlayerLib.playSound(player, ENTITY_GHAST_SCREAM, 0.75f);

            return DamageOutput.CANCEL;
        }

        return null;
    }

    @Nullable
    @Override
    public DamageOutput processDamageAsDamager(DamageInput input) {
        final GamePlayer gamePlayer = input.getDamagerAsPlayer();
        final LivingGameEntity entity = input.getEntity();

        // Skip witherboard damage
        if (gamePlayer == null || input.getDamageCause() == EnumDamageCause.WITHERBORN) {
            return null;
        }

        final WitherData data = getWither(gamePlayer.getPlayer());

        if (data != null) {
            data.assistAttack(entity.getEntity());
        }

        return null;
    }

    @EventHandler()
    public void handleInteraction(PlayerInteractAtEntityEvent ev) {
        final Player player = ev.getPlayer();

        if (!validatePlayer(player) || ev.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }

        processSpellClick(player, false);
    }

    @EventHandler()
    public void handleInteraction(PlayerInteractEvent ev) {
        final Player player = ev.getPlayer();
        final Action action = ev.getAction();

        if (!validatePlayer(player)
                || ev.getHand() == EquipmentSlot.OFF_HAND
                || player.hasCooldown(getWeapon().getMaterial())
                || ev.getAction() == Action.PHYSICAL) {
            return;
        }

        final boolean isLeftClick = action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK;

        processSpellClick(player, isLeftClick);
        ev.setCancelled(true);

        //final WitherSkull skull = player.launchProjectile(WitherSkull.class, player.getLocation().getDirection().multiply(3.0d));
        //skull.setCharged(true);
        //skull.setYield(0.0f);
        //skull.setShooter(player);
        //
        //player.setCooldown(this.getWeapon().getMaterial(), 20);
        //PlayerLib.playSound(player, ENTITY_WITHER_SHOOT, 1.0f);
    }

    @Nullable
    public WitherData getWither(Player player) {
        return withers.get(player);
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.BLINDING_CURSE.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.SLOWING_AURA.getTalent();
    }

    @Override
    public Talent getThirdTalent() {
        return Talents.HEALING_AURA.getTalent();
    }

    //@EventHandler()
    //public void handleProjectileHit(ProjectileHitEvent ev) {
    //    if (!(ev.getEntity() instanceof WitherSkull skull) || !(skull.getShooter() instanceof Player player)) {
    //        return;
    //    }
    //
    //    Utils.getPlayersInRange(skull.getLocation(), 3.0d).forEach(victim -> {
    //        GamePlayer.damageEntity(victim, 10.0d, player, EnumDamageCause.WITHER_SKULLED);
    //    });
    //}

    @Override
    @Nonnull
    public ShadowClone getFourthTalent() {
        return (ShadowClone) Talents.SHADOW_CLONE.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.DARK_MAGE_PASSIVE.getTalent();
    }

    @Override
    @KeepNull
    public Talent getFifthTalent() {
        return null;
    }

    // [hapyl's rant about interaction detection]
    //
    // This is the stupidest thing in the fucking world,
    // why is it if I'm clicking on entity, it doesn't fire interact event,
    // I'm still fucking interacting aren't I?
    // And also the fucking left-clicking entity does not fire the event either, like why?
    // I'm clearly fucking interacting???
    // But of course, the event handles with 2 hands, even if I have nothing in my b hand, makes sense.
    private void processSpellClick(Player player, boolean isLeftClick) {
        // Check for actual wand maybe?
        if (player.getInventory().getItemInMainHand().getType() != getWeapon().getMaterial()) {
            return;
        }

        // Handle wand
        final DarkMageSpell spell = spellMap.computeIfAbsent(player, DarkMageSpell::new);

        // When empty:
        // - Right-clicking ONCE will enter the mode.

        // When not empty:
        // Either left-clicking or right-clicking will add the button.
        // If after adding the button, the spell is full, it will be cast.

        // Check for timeout
        if (spell.isTimeout()) {
            spell.clear();
        }

        final WitherData data = getWither(player);

        if (!isLeftClick) {
            if (spell.isEmpty()) {
                spell.markUsed();
                spell.display();
                return;
            }

            spell.addButton(DarkMageSpell.SpellButton.RIGHT);
        }
        else if (!spell.isEmpty()) {
            spell.addButton(DarkMageSpell.SpellButton.LEFT);
        }

        final boolean usingUltimate = isUsingUltimate(player);
        if (spell.isFull()) {
            spell.cast(data);
        }
    }

    private void killWither(Player player) {
        final WitherData data = withers.get(player);

        if (data != null) {
            data.remove();
        }

        withers.remove(player);
    }

    private void updateWitherName(Player player, Wither wither) {
        wither.setCustomName(Chat.format(
                "&4&l☠ &c%s %s &a&l%s ❤",
                player.getName(),
                UIFormat.DIV,
                BukkitUtils.decimalFormat(wither.getHealth())
        ));
    }

}
