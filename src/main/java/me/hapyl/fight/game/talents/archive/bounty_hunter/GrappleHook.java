package me.hapyl.fight.game.talents.archive.bounty_hunter;

import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.Nulls;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.entity.EntityUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

public class GrappleHook extends GameTask {

    public final GamePlayer player;
    private final GrappleHookTalent talent;
    private final LivingEntity anchor;
    private final LivingEntity hook;

    protected LivingGameEntity hookedEntity;
    protected int ropeCuts;

    private Block hookedBlock;
    private GameTask extendTask;
    private GameTask retractTask;

    public GrappleHook(GrappleHookTalent talent, GamePlayer player) {
        this.talent = talent;
        this.player = player;

        this.anchor = createEntity();
        this.hook = createEntity();

        this.anchor.setLeashHolder(this.hook);

        extendHook();

        runTaskTimer(0, 1);
    }

    @Override
    public void run() {
        anchor.teleport(player.getLocation());

        if (hookedEntity != null) {
            hook.teleport(hookedEntity.getLocation());
            hook.getWorld().spawnParticle(Particle.ITEM_CRACK, hook.getLocation(),
                    5,
                    0.125,
                    0.125,
                    0.125,
                    0.05f,
                    new ItemStack(Material.LEAD)
            );

            final String title = ropeCuts == 0
                    ? "&6ʏᴏᴜ'ʀᴇ ʜᴏᴏᴋᴇᴅ"
                    : "&a\uD83E\uDE9D".repeat(ropeCuts) + "&8\uD83E\uDE9D".repeat(talent.cutsToRemove - ropeCuts);

            final int ticks = hookedEntity.aliveTicks() % 40;
            final String subTitle = ticks == 0 || ticks > 20 ? "&e&lSNEAK&6 to cut the rope!" : "&6&lSNEAK&e to cut the rope!";

            hookedEntity.sendTitle(title, subTitle, 0, 10, 0);
        }
    }

    public boolean isHookBroken() {
        return hook.isDead() || !anchor.isLeashed();
    }

    public void remove() {
        cancel();

        anchor.remove();
        hook.remove();

        Nulls.runIfNotNull(extendTask, GameTask::cancel);
        Nulls.runIfNotNull(retractTask, GameTask::cancel);

        talent.playerHooks.remove(player, this);
    }

    private boolean isHookToAnchorObstructed() {
        final Location hookLocation = hook.getLocation();
        final Location anchorLocation = anchor.getLocation().add(0.0d, player.getEyeHeight(), 0.0d); // ray-cast from player's eyes
        double distance = anchorLocation.distance(hookLocation);

        final double step = 0.5d;
        final Vector vector = anchorLocation.toVector().subtract(hookLocation.toVector()).normalize().multiply(step);

        for (double i = 0.0; i < distance; i += step) {
            // Don't check the first and the last block
            if (i == 0.0 || i >= (distance - step)) {
                continue;
            }

            hookLocation.add(vector);

            if (hookLocation.getBlock().getType().isOccluding()) {
                return true;
            }
        }

        return false;
    }

    private void extendHook() {
        final Location location = player.getEyeLocation();
        final Vector vector = location.getDirection().normalize();

        // Fx
        player.playSound(Sound.ENTITY_BAT_TAKEOFF, 1.0f);
        player.playSound(Sound.ENTITY_LEASH_KNOT_PLACE, 0.0f);

        this.extendTask = new GameTask() {
            private final double speed = 0.075d;
            private final int checksPerTick = 2;
            private double distance = 0.0d;

            @Override
            public void run() {
                if (isHookBroken()) {
                    breakHook();
                    return;
                }

                if (distance >= (talent().maxDistance * speed)) {
                    remove();

                    // Fx
                    player.sendMessage("&6\uD83E\uDE9D &cYou didn't hook anything!");
                    return;
                }

                for (int i = 0; i < checksPerTick; i++) {
                    if (hookedBlock != null || hookedEntity != null) {
                        return;
                    }

                    if (nextLocation()) {
                        return;
                    }
                }

            }

            private boolean nextLocation() {
                final double x = vector.getX() * distance;
                final double y = vector.getY() * distance;
                final double z = vector.getZ() * distance;

                location.add(x, y, z);

                // Hook detection
                final Block block = location.getBlock();

                if (!block.getType().isAir()) {
                    if (!isValidBlock(block)) {
                        remove();
                        player.sendMessage("&6\uD83E\uDE9D &cYou can't hook to that!");
                        return true;
                    }

                    hookedBlock = block;
                    retractHook();
                    return true;
                }

                final LivingGameEntity nearest = Collect.nearestEntity(location, 1.5d, player);

                if (nearest != null) {
                    if (nearest.hasEffectResistanceAndNotify(player)) {
                        breakHook();
                        return true;
                    }

                    hookedEntity = nearest;

                    if (hook instanceof Slime slime) {
                        slime.setSize(2);
                        slime.setHealth(1);
                        slime.setInvulnerable(false);
                    }

                    retractHook();

                    // Fx
                    player.sendMessage("&6\uD83E\uDE9D &aYou hooked &e%s&a!", hookedEntity.getName());
                    return true;
                }

                hook.teleport(location);
                distance += speed;
                return false;
            }
        }.runTaskTimer(0, 1);
    }

    private void retractHook() {
        extendTask.cancel();
        player.playSound(Sound.ENTITY_LEASH_KNOT_PLACE, 0.0f);

        retractTask = new GameTask() {
            private final double step = 0.75d;

            @Override
            public void run() {
                if (isHookBroken()) {
                    breakHook();
                    return;
                }

                if (isHookToAnchorObstructed()) {
                    remove();
                    player.sendMessage("&6\uD83E\uDE9D &cYour hook broke!");
                    player.playSound(Sound.ENTITY_LEASH_KNOT_BREAK, 0.0f);
                    player.playSound(Sound.ENTITY_LEASH_KNOT_BREAK, 2.0f);
                    return;
                }

                final Location playerLocation = player.getLocation();
                final Location location = hook.getLocation();
                final Vector vector = location.toVector().subtract(playerLocation.toVector()).normalize().multiply(step);

                playerLocation.add(vector);

                // Finishes grappling
                if (playerLocation.distanceSquared(location) <= 1d) {
                    remove();
                    player.addEffect(Effects.FALL_DAMAGE_RESISTANCE, 600, true);
                }

                if (isVectorFinite(vector)) {
                    player.setVelocity(vector);
                }
            }
        }.runTaskTimer(0, 1);
    }

    private boolean isValidBlock(Block block) {
        final Material type = block.getType();

        // Add more checks
        if (type == Material.BARRIER) {
            return false;
        }

        if (!block.getType().isAir()) {
            return true;
        }

        return true;
    }

    private void breakHook() {
        remove();

        // Fx
        player.sendMessage("&6\uD83E\uDE9D &cYour hook broke!");
        player.playSound(Sound.ENTITY_LEASH_KNOT_BREAK, 0.0f);
    }

    private boolean isVectorFinite(Vector vector) {
        return NumberConversions.isFinite(vector.getX())
                && NumberConversions.isFinite(vector.getY())
                && NumberConversions.isFinite(vector.getZ());
    }

    private LivingEntity createEntity() {
        return Entities.SLIME.spawn(player.getLocation(), self -> {
            self.setSize(1);
            self.setGravity(false);
            self.setInvulnerable(true);
            self.setInvisible(true);
            self.setSilent(true);
            self.setAI(false);

            EntityUtils.setCollision(self, EntityUtils.Collision.DENY);
        });
    }

    private GrappleHookTalent talent() {
        return (GrappleHookTalent) Talents.GRAPPLE.getTalent();
    }

}
