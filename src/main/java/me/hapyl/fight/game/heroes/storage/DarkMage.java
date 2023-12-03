package me.hapyl.fight.game.heroes.storage;

import com.google.common.collect.Maps;
import me.hapyl.fight.annotate.KeepNull;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.cosmetic.CosmeticsHandle;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.heroes.ComplexHero;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroEquipment;
import me.hapyl.fight.game.heroes.Role;
import me.hapyl.fight.game.heroes.storage.extra.DarkMageSpell;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIFormat;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.reflect.Reflect;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.Map;

import static org.bukkit.Sound.ENTITY_WITHER_DEATH;
import static org.bukkit.Sound.ENTITY_WITHER_SHOOT;

public class DarkMage extends Hero implements ComplexHero, Listener {

    private final Map<Player, DarkMageSpell> spellMap = Maps.newHashMap();

    public DarkMage() {
        super("Dark Mage");

        setRole(Role.MELEE);
        setInfo("A mage that was cursed by &8&lDark &8&lMagic&7&o. But even it couldn't kill him...");
        setItem("e6ca63569e8728722ecc4d12020e42f086830e34e82db55cf5c8ecd51c8c8c29");

        final HeroEquipment equipment = this.getEquipment();
        equipment.setChestplate(102, 255, 255);
        equipment.setLeggings(Material.IRON_LEGGINGS);
        equipment.setBoots(153, 51, 51);

        setWeapon(new Weapon(Material.WOODEN_HOE).setName("Ancient Wand")
                .setDamage(7.0d)
                .setDescription(
                        "A powerful wand, that's capable of casting multiple spells!____&e&lRIGHT CLICK &7to enter casting, then, combine &e&lRIGHT CLICK &7and/or &e&lLEFT CLICK &7to execute the spell!"
                )
                .setWeaponLore(
                        "Long ago, a powerful wand was crafted from the bones of long-dead wizards and imbued with dark magic, granting immense power to its wielder. The wand was used by a cruel and merciless ruler to subjugate kingdoms."
                ));

        // TODO (hapyl): 019, Apr 19, 2023: Make DM actually ride the wither?
        setUltimate(new UltimateTalent(
                "Wither Rider",
                "Transform into the wither for {duration}.____" +
                        "While transformed, &e&lRIGHT CLICK &7to shoot wither skulls that deals massive damage on impact.__" +
                        "After wither disappears, you perform plunging attack that deals damage in AoE upon hitting the ground.",
                70
        ).setItem(Material.WITHER_SKELETON_SKULL).setDuration(240).setCdSec(30).setSound(Sound.ENTITY_WITHER_SPAWN, 2.0f));
    }

    @Override
    public void useUltimate(Player player) {
        player.setAllowFlight(true);
        player.setFlying(true);

        final double playerHealth = GamePlayer.getPlayer(player).getHealth();
        Utils.hidePlayer(player);

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
                            CosmeticsHandle.GROUND_PUNCH_COSMETIC.playAnimation(player.getLocation(), 2);

                            Utils.getPlayersInRange(player.getLocation(), 4).forEach(target -> {
                                if (target == player) {
                                    return;
                                }
                                GamePlayer.damageEntity(target, 5.0d, player);
                            });
                        }
                    }
                }.runTaskTimer(0, 1);
            }

            private void killWither(@Nullable Player player, Wither wither) {
                if (player != null) {
                    player.setFlying(false);
                    player.setAllowFlight(false);
                    Utils.showPlayer(player);
                }
                PlayerLib.playSound(wither.getLocation(), ENTITY_WITHER_DEATH, 1.0f);
                wither.remove();
            }
        }.runTaskTimer(0, 1);
    }

    private void updateWitherName(Player player, Wither wither) {
        wither.setCustomName(Chat.format(
                "&4&l☠ &c%s %s &a&l%s ❤",
                player.getName(),
                UIFormat.DIV,
                BukkitUtils.decimalFormat(wither.getHealth())
        ));
    }

    @EventHandler()
    public void handleProjectileHit(ProjectileHitEvent ev) {
        if (!(ev.getEntity() instanceof WitherSkull skull) || !(skull.getShooter() instanceof Player player)) {
            return;
        }

        Utils.getPlayersInRange(skull.getLocation(), 3.0d).forEach(victim -> {
            GamePlayer.damageEntity(victim, 10.0d, player, EnumDamageCause.WITHER_SKULLED);
        });
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
        final boolean isRightClick = !isLeftClick;

        // Handle wand
        if (!isUsingUltimate(player)) {
            final DarkMageSpell spell = spellMap.computeIfAbsent(player, a -> new DarkMageSpell(player));

            // When empty:
            // - Right-clicking ONCE will enter the mode.

            // When not empty:
            // Either left-clicking or right-clicking will add the button.
            // If after adding the button, the spell is full, it will be casted.

            // Check for timeout
            if (spell.isTimeout()) {
                spell.clear();
            }

            if (isRightClick) {
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

            if (spell.isFull()) {
                spell.cast();
            }

            ev.setCancelled(true);
            return;
        }

        // Handle ultimate
        final WitherSkull skull = player.launchProjectile(WitherSkull.class, player.getLocation().getDirection().multiply(3.0d));
        skull.setCharged(true);
        skull.setYield(0.0f);
        skull.setShooter(player);

        player.setCooldown(this.getWeapon().getMaterial(), 20);
        PlayerLib.playSound(player, ENTITY_WITHER_SHOOT, 1.0f);
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

    @Override
    public Talent getFourthTalent() {
        return Talents.SHADOW_CLONE.getTalent();
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

}
