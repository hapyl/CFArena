package me.hapyl.fight.game.heroes.archive.shadow_assassin;

import me.hapyl.fight.CF;
import me.hapyl.fight.event.io.DamageInput;
import me.hapyl.fight.event.io.DamageOutput;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.Collect;
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

        setArchetype(Archetype.STRATEGY);

        setDescription("Well-trained assassin from dimension of shadows.");
        setItem("9598fcbbf65b9ff66da99487403e4baf7e4c50144d06c7417bbded578d76d004");

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(Color.BLACK);
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
    public void useUltimate(@Nonnull GamePlayer player) {
        player.setCooldown(getWeapon().getMaterial(), 0);

        // Fx
        player.playWorldSound(Sound.BLOCK_BEACON_ACTIVATE, 1.75f);
        player.playWorldSound(Sound.BLOCK_BEACON_AMBIENT, 1.75f);
        player.addPotionEffect(PotionEffectType.SLOW, getUltimateDuration(), 0);

        GameTask.runLater(() -> player.playWorldSound(Sound.BLOCK_BEACON_DEACTIVATE, 1.85f), getUltimateDuration());
    }

    @EventHandler()
    public void handleUltimate(PlayerInteractEvent ev) {
        final GamePlayer player = CF.getPlayer(ev.getPlayer());

        if (player == null
                || ev.getHand() == EquipmentSlot.OFF_HAND
                || ev.getAction() == Action.PHYSICAL
                || !validatePlayer(player)
                || !isUsingUltimate(player)
                || player.hasCooldown(getWeapon().getMaterial())) {
            return;
        }

        final LivingGameEntity livingEntity = getNearestEntity(player);

        if (livingEntity == null) {
            player.sendMessage("&cNo valid opponent!");
            return;
        }

        livingEntity.damage(getWeapon().getDamage(), player, EnumDamageCause.NEVERMISS);

        player.setCooldown(getWeapon().getMaterial(), NEVERMISS_CD);

        // Fx
        PlayerLib.playSound(player.getLocation(), Sound.BLOCK_NETHER_ORE_BREAK, 1.75f);
    }

    @Override
    public boolean processInvisibilityDamage(GamePlayer player, LivingGameEntity entity, double damage) {
        if (player.isSneaking()) {
            player.sendMessage("&cCannot deal damage while in &lDark Cover&c!");
            player.playSound(Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
            return true;
        }

        return false;
    }

    @Override
    public DamageOutput processDamageAsDamager(DamageInput input) {
        final GamePlayer player = input.getDamagerAsPlayer();

        if (player == null || input.getDamageCause() != EnumDamageCause.ENTITY_ATTACK) {
            return DamageOutput.OK;
        }

        // FIXME (hapyl): 030, Oct 30: Backstab does not work?

        // Calculate back stab
        final LivingGameEntity gameEntity = input.getDamagerAsLiving();
        if (gameEntity == null) {
            return DamageOutput.OK;
        }

        final LivingEntity entity = gameEntity.getEntity();

        if (validateCanBackStab(player, entity)) {
            if (player.getLocation().getDirection().dot(gameEntity.getLocation().getDirection()) > 0) {
                performBackStab(player, gameEntity);
            }
        }

        return DamageOutput.OK;
    }

    @Override
    public DamageOutput processDamageAsVictim(DamageInput input) {
        final GamePlayer player = input.getEntityAsPlayer();

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

    public void setDarkCoverCd(GamePlayer player, int cd) {
        player.setCooldown(Talents.SECRET_SHADOW_WARRIOR_TECHNIQUE.getTalent().getMaterial(), cd);
    }

    public int getDarkCoverCd(GamePlayer player) {
        return player.getCooldown(Talents.SECRET_SHADOW_WARRIOR_TECHNIQUE.getTalent().getMaterial());
    }

    public void setDarkCover(GamePlayer player, boolean flag) {
        // Enter
        if (flag) {
            player.addPotionEffect(PotionEffectType.INVISIBILITY, 99999, 5);
            player.hide();

            playDarkCoverFx(player, true);
        }

        else {
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            player.show();

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

    public void playDarkCoverFx(GamePlayer player, boolean flag) {
        final Location location = player.getEyeLocation();

        if (flag) {
            player.spawnWorldParticle(Particle.CRIT, 20, 0, 0.2, 0, 1.0f);
            player.spawnWorldParticle(Particle.CRIT_MAGIC, 20, 0, 0.2, 0, 0.5f);
            player.spawnWorldParticle(Particle.WARPED_SPORE, 10, 0, 0.5, 0, 0);
            player.playWorldSound(Sound.ENTITY_ENDERMAN_TELEPORT, 1.75f);

            player.sendTitle("&8&l\uD83E\uDEA3", "&7In Dark Cover", 0, 200000, 0);
        }
        else {
            player.spawnWorldParticle(Particle.ENCHANTMENT_TABLE, 10, 0, 0, 0, 2);
            player.playWorldSound(Sound.ENTITY_ENDERMAN_TELEPORT, 1.25f);

            player.clearTitle();
        }
    }

    public boolean canHide(GamePlayer player) {
        return getDarkCoverCd(player) == 0;
    }

    public void kickFromDarkCover(GamePlayer player) {
        final Location location = player.getEyeLocation();

        setDarkCoverCd(player, 200);
        setDarkCover(player, false);

        // Fx
        player.sendMessage("&cYou took damage and lost your &lDark Cover&c!");

        player.spawnWorldParticle(Particle.DRAGON_BREATH, 30, 0, 0, 0, 0.5f);
        player.spawnWorldParticle(Particle.LAVA, 35, 0, 0, 0, 0);
        player.playWorldSound(Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1.75f);
        player.playWorldSound(Sound.ENTITY_ENDERMAN_SCREAM, 1.25f);
    }

    public void performBackStab(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity) {
        final Location location = entity.getLocation();
        final Vector vector = location.getDirection();

        entity.setVelocity(new Vector(vector.getX(), 0.1d, vector.getZ()).multiply(2.13f));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 5));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 40, 5));

        entity.sendMessage("&a%s stabbed you!", player.getName());
        player.setCooldown(getWeapon().getMaterial(), BACK_STAB_CD);

        // Fx
        entity.playWorldSound(Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 0.65f);
        entity.spawnWorldParticle(Particle.CRIT, 10, 0.25d, 0.0d, 0.25d, 0.076f);
    }

    @EventHandler()
    public void handlePlayerToggleSneakEvent(PlayerToggleSneakEvent ev) {
        final GamePlayer player = CF.getPlayer(ev.getPlayer());

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
    public String getString(@Nonnull GamePlayer player) {
        final int cooldown = getDarkCoverCd(player);
        return cooldown > 0 ? "&f&l\uD83E\uDEA3 &f%ss".formatted(BukkitUtils.roundTick(cooldown)) : "";
    }

    @Nullable
    private LivingGameEntity getNearestEntity(GamePlayer player) {
        return Collect.targetEntity(player, 10, 0.5d, t -> t.hasLineOfSight(player));
    }

    private boolean validateCanBackStab(GamePlayer player, LivingEntity entity) {
        return entity != null
                && !isUsingUltimate(player)
                && player != entity
                && !player.hasCooldown(getWeapon().getMaterial()) && player.getInventory().getHeldItemSlot() == 0;
    }

}
