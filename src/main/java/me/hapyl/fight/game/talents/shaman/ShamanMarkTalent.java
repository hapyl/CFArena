package me.hapyl.fight.game.talents.shaman;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperInstance;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.RaycastTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.eterna.module.math.Tick;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class ShamanMarkTalent extends Talent {

    @DisplayField protected final int outOfSightDuration = Tick.fromSecond(2);
    @DisplayField(scaleFactor = 500, suffix = "%", suffixSpace = false) private final double speedIncrease = 0.1d; // 50%
    @DisplayField(percentage = true) private final double attackIncrease = 0.5d;
    @DisplayField(percentage = true) private final double attackSpeedIncrease = 0.5d;

    protected final TemperInstance temperInstance = Temper.SHAMANS_MARK.newInstance("&a\uD83D\uDC38 &2sʜᴀᴍᴀɴ's ᴍᴀʀᴋ")
            .increase(AttributeType.SPEED, speedIncrease)
            .increase(AttributeType.ATTACK, attackIncrease)
            .increase(AttributeType.ATTACK_SPEED, attackSpeedIncrease);

    @DisplayField private final double projectileStep = 0.5d;
    private final double maxDistance = 30;

    // this is only for the display
    @DisplayField(suffix = "blocks") private final double maxProjectileDistance = maxDistance * projectileStep;

    private final PlayerMap<ShamanMark> shamanMarkMap = PlayerMap.newMap();

    public ShamanMarkTalent() {
        super("Shaman's Mark");

        setDescription("""
                Launch a projectile forward that travels up to &b{maxProjectileDistance}&7.
                                
                If the projectile hits an &aally&7, it applies the &6Shaman's Mark&7 onto them.
                                
                &6Shaman's Mark
                While active, increases %s, %s and %s.
                                
                The mark exists &nindefinitely&7 as long as the &aally&7 is within the &bline of sight&7.
                                
                &8;;Only one mark can exist on a single ally at any given time.
                &8;;Marks from different Shamans don't stack.
                """.formatted(AttributeType.SPEED, AttributeType.ATTACK, AttributeType.ATTACK_SPEED));

        setItem(Material.LILY_PAD);
        setType(TalentType.SUPPORT);
        setCooldownSec(16);
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        shamanMarkMap.removeAnd(player, ShamanMark::cancel);
    }

    @Override
    public void onStop() {
        shamanMarkMap.forEachAndClear(ShamanMark::cancel);
    }

    public void remove(@Nonnull GamePlayer player) {
        final ShamanMark oldMark = shamanMarkMap.remove(player);

        if (oldMark != null) {
            oldMark.cancel();
        }
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        new RaycastTask(player.getEyeLocation()) {
            @Override
            public boolean step(@Nonnull Location location) {
                final LivingGameEntity nearestEntity = Collect.nearestEntity(location, 1.5d, player::isTeammate);

                if (nearestEntity != null) {
                    remove(player);

                    shamanMarkMap.put(player, new ShamanMark(ShamanMarkTalent.this, player, nearestEntity));
                    return true;
                }

                // Fx
                player.spawnWorldParticle(location, Particle.ITEM, 3, 0.1, 0.1, 0.1, 0.1f, new ItemStack(Material.SLIME_BALL));
                player.playWorldSound(location, Sound.ENTITY_FROG_TONGUE, 1.25f);
                return false;
            }
        }
                .setStep(0.5d)
                .setMax(maxDistance)
                .setIterations(2)
                .runTaskTimer(0, 1);

        return Response.OK;
    }
}
