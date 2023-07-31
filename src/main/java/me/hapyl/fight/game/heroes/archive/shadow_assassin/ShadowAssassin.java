package me.hapyl.fight.game.heroes.archive.shadow_assassin;

import me.hapyl.fight.CF;
import me.hapyl.fight.event.DamageInput;
import me.hapyl.fight.event.DamageOutput;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroEquipment;
import me.hapyl.fight.game.heroes.Role;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.particle.ParticleBuilder;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ShadowAssassin extends Hero implements Listener, UIComponent {

    private final int BACK_STAB_CD = 400;
    private final int NEVERMISS_CD = 15;

    public ShadowAssassin() {
        super("Shadow Assassin");

        setRole(Role.ASSASSIN);
        setArchetype(Archetype.STRATEGY);

        setDescription("Well-trained assassin from dimension of shadows.");
        setItem("9598fcbbf65b9ff66da99487403e4baf7e4c50144d06c7417bbded578d76d004");

        final HeroEquipment equipment = getEquipment();
        equipment.setChestplate(Color.BLACK);
        equipment.setLeggings(Color.BLACK);
        equipment.setBoots(Color.BLACK);

        setWeapon(new Weapon(Material.IRON_SWORD).setName("Livid Dagger").setDescription(String.format(
                "A dagger made of bad memories.____&e&lBACKSTAB &7to perform a charged attack that knocks enemies and stuns them for a short time.____&aCooldown: &l%ss",
                BukkitUtils.roundTick(BACK_STAB_CD)
        )).setDamage(8.0d));

        setUltimate(new UltimateTalent(
                "Extreme Focus",
                "Enter &bExtreme Focus &7for {duration}. While active, you will not miss your hits if target is close enough and has no cover.",
                80
        ).setDuration(200).setCooldownSec(40).setItem(Material.GOLDEN_CARROT));

        getUltimate().addAttributeDescription("Cooldown Per Hit", NEVERMISS_CD);
    }

    @Override
    public void useUltimate(Player player) {
        player.setCooldown(getWeapon().getMaterial(), 0);

        // fx
        PlayerLib.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.75f);
        PlayerLib.playSound(player.getLocation(), Sound.BLOCK_BEACON_AMBIENT, 1.75f);
        PlayerLib.addEffect(player, PotionEffectType.SLOW, getUltimateDuration(), 0);

        GameTask.runLater(() -> PlayerLib.playSound(Sound.BLOCK_BEACON_DEACTIVATE, 1.85f), getUltimateDuration());
    }

    @EventHandler()
    public void handleUltimate(PlayerInteractEvent ev) {
        final Player player = ev.getPlayer();
        if (ev.getHand() == EquipmentSlot.OFF_HAND || ev.getAction() == Action.PHYSICAL || !validatePlayer(player) ||
                !isUsingUltimate(player) || player.hasCooldown(getWeapon().getMaterial())) {
            return;
        }

        final LivingEntity livingEntity = getNearestEntity(player);

        if (livingEntity == null) {
            Chat.sendMessage(player, "&cNo valid opponent!");
            return;
        }

        CF.getEntityOptional(livingEntity).ifPresent(gameEntity -> {
            gameEntity.damage(getWeapon().getDamage(), player, EnumDamageCause.NEVERMISS);
        });

        GamePlayer.setCooldown(player, getWeapon().getMaterial(), NEVERMISS_CD);

        // fx
        PlayerLib.playSound(player.getLocation(), Sound.BLOCK_NETHER_ORE_BREAK, 1.75f);
    }

    @Nullable
    private LivingEntity getNearestEntity(Player player) {
        final GameEntity gameEntity = Collect.targetEntity(player, 10, 0.5d, t -> t.hasLineOfSight(player));
        return gameEntity == null ? null : gameEntity.getEntity();

        //final Location location = player.getLocation();
        //LivingEntity closest = null;
        //double distance = 0.0d;
        //for (final LivingEntity living : Utils.getEntitiesInRange(location, 10)) {
        //    if (player == living) {
        //        continue;
        //    }
        //    final double currentDistance = living.getLocation().distance(location);
        //    if (closest == null || currentDistance < distance) {
        //        closest = living;
        //        distance = currentDistance;
        //    }
        //}
        //return closest;
    }

    @Override
    public boolean processInvisibilityDamage(GamePlayer player, GameEntity entity, double damage) {
        if (player.isSneaking()) {
            player.sendMessage("&cCannot deal damage while in &lDark Cover&c!");
            player.playSound(Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
            return true;
        }

        return false;
    }

    @Override
    public DamageOutput processDamageAsDamager(DamageInput input) {
        final Player player = input.getBukkitPlayer();

        if (input.getDamageCause() != EnumDamageCause.ENTITY_ATTACK) {
            return DamageOutput.OK;
        }

        // Calculate back stab
        final GameEntity gameEntity = input.getDamager();
        if (gameEntity == null) {
            return null;
        }

        final LivingEntity entity = gameEntity.getEntity();

        if (validateCanBackStab(player, entity)) {
            if (player.getLocation().getDirection().dot(gameEntity.getLocation().getDirection()) > 0) {
                performBackStab(player, entity);
            }
        }

        return null;
    }

    @Override
    public DamageOutput processDamageAsVictim(DamageInput input) {
        final Player player = input.getBukkitPlayer();
        if (!canHide(player)) {
            return null;
        }

        if (player.isSneaking()) {
            player.setSneaking(false);
            kickFromDarkCover(player);
        }
        else {
            setDarkCoverCd(player, 60);
        }

        return null;
    }

    public void setDarkCoverCd(Player player, int cd) {
        GamePlayer.setCooldown(player, Talents.SECRET_SHADOW_WARRIOR_TECHNIQUE.getTalent().getMaterial(), cd);
    }

    public int getDarkCoverCd(Player player) {
        return player.getCooldown(Talents.SECRET_SHADOW_WARRIOR_TECHNIQUE.getTalent().getMaterial());
    }

    public void setDarkCover(Player player, boolean flag) {
        // Enter
        if (flag) {
            PlayerLib.addEffect(player, PotionEffectType.INVISIBILITY, 999999, 5);
            Utils.hidePlayer(player);

            playDarkCoverFx(player, true);
        }

        else {
            PlayerLib.removeEffect(player, PotionEffectType.INVISIBILITY);
            Utils.showPlayer(player);

            playDarkCoverFx(player, false);
        }
    }

    public void displayFootprints(Location location) {
        ParticleBuilder
                .blockDust(location.getBlock().getRelative(BlockFace.DOWN).getType())
                .setAmount(3)
                .setOffX(0.25d)
                .setOffZ(0.25d)
                .display(location);
    }

    public void playDarkCoverFx(Player player, boolean flag) {
        final Location location = player.getEyeLocation();
        if (flag) {
            PlayerLib.spawnParticle(location, Particle.CRIT, 20, 0, 0.2, 0, 1.0f);
            PlayerLib.spawnParticle(location, Particle.CRIT_MAGIC, 20, 0, 0.2, 0, 0.5f);
            PlayerLib.spawnParticle(location, Particle.WARPED_SPORE, 10, 0, 0.5, 0, 0);
            PlayerLib.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.75f);

            Chat.sendTitle(player, "&8&l\uD83E\uDEA3", "&7In Dark Cover", 0, 200000, 0);
        }
        else {
            PlayerLib.spawnParticle(location, Particle.ENCHANTMENT_TABLE, 10, 0, 0, 0, 2);
            PlayerLib.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.25f);

            Chat.clearTitle(player);
        }
    }

    public boolean canHide(Player player) {
        return getDarkCoverCd(player) == 0;
    }

    public void kickFromDarkCover(Player player) {
        final Location location = player.getEyeLocation();
        setDarkCoverCd(player, 200);
        setDarkCover(player, false);

        // fx
        Chat.sendMessage(player, "&cYou took damage and lost your &lDark Cover&c!");
        PlayerLib.spawnParticle(location, Particle.DRAGON_BREATH, 30, 0, 0, 0, 0.5f);
        PlayerLib.spawnParticle(location, Particle.LAVA, 35, 0, 0, 0, 0);
        PlayerLib.playSound(location, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1.75f);
        PlayerLib.playSound(location, Sound.ENTITY_ENDERMAN_SCREAM, 1.25f);
    }

    private boolean validateCanBackStab(Player player, LivingEntity entity) {
        return entity != null
                && !isUsingUltimate(player)
                && player != entity
                && !player.hasCooldown(getWeapon().getMaterial()) && player.getInventory().getHeldItemSlot() == 0;
    }

    public void performBackStab(Player player, @Nonnull LivingEntity entity) {
        final Location location = entity.getLocation();
        final Vector vector = location.getDirection();
        entity.setVelocity(new Vector(vector.getX(), 0.1d, vector.getZ()).multiply(2.13f));

        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 5));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 40, 5));

        if (entity instanceof Player playerEntity) {
            Chat.sendMessage(playerEntity, "&a%s stabbed you!", player.getName());
        }

        GamePlayer.setCooldown(player, getWeapon().getMaterial(), BACK_STAB_CD);

        // fx
        PlayerLib.playSound(location, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 0.65f);
        PlayerLib.spawnParticle(location, Particle.CRIT, 10, 0.25d, 0.0d, 0.25d, 0.076f);
    }

    @EventHandler()
    public void handlePlayerToggleSneakEvent(PlayerToggleSneakEvent ev) {
        final Player player = ev.getPlayer();
        if (!validatePlayer(player) || !canHide(player)) {
            return;
        }

        setDarkCover(player, ev.isSneaking());
    }

    @EventHandler()
    public void handlePlayerMoveEvent(PlayerMoveEvent ev) {
        final Player player = ev.getPlayer();
        final Location from = ev.getFrom();
        final Location to = ev.getTo();
        if (to == null || !validatePlayer(player) || !player.isSneaking()) {
            return;
        }

        // make sure we moved, not mouse movement
        if (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) {
            return;
        }

        displayFootprints(to);
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.SHADOW_PRISM.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.SHROUDED_STEP.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.SECRET_SHADOW_WARRIOR_TECHNIQUE.getTalent();
    }

    @Nonnull
    @Override
    public String getString(Player player) {
        final int cooldown = getDarkCoverCd(player);
        return cooldown > 0 ? "&f&l\uD83E\uDEA3 &f%ss".formatted(BukkitUtils.roundTick(cooldown)) : "";
    }

}
