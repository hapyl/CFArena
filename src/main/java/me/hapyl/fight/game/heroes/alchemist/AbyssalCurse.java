package me.hapyl.fight.game.heroes.alchemist;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.fight.Message;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.ShutdownAction;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.ObfString;
import me.hapyl.fight.util.TickExecutor;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Set;

public class AbyssalCurse extends TickingGameTask {

    private static final long TRANSFER_COOLDOWN = 250L;
    private static final int FALLING_BLOCK_COUNT = 30;

    private static final BlockData[] FALLING_BLOCK_DATA = {
            Material.PURPLE_STAINED_GLASS.createBlockData(),
            Material.MAGENTA_STAINED_GLASS.createBlockData(),
            Material.PURPLE_WOOL.createBlockData(),
            Material.MAGENTA_WOOL.createBlockData(),
            Material.PURPLE_TERRACOTTA.createBlockData(),
            Material.MAGENTA_TERRACOTTA.createBlockData()
    };

    private final GamePlayer owner;
    private final int duration;

    private GamePlayer player;
    private long lastTransfer;

    public AbyssalCurse(@Nonnull GamePlayer owner, final int duration) {
        this.owner = owner;
        this.player = owner;
        this.duration = duration;

        runTaskTimer(0, 1);
    }

    @Nonnull
    public GamePlayer owner() {
        return owner;
    }

    @Nonnull
    public GamePlayer player() {
        return player;
    }

    public void transfer(@Nonnull GamePlayer player) {
        if (this.player.equals(player)) {
            this.player.sendMessage(Message.ERROR, "Cannot transfer curse to yourself!");
            return;
        }

        if (this.player.isTeammate(player)) {
            this.player.sendMessage(Message.ERROR, "Cannot transfer curse to a teammate!");
            return;
        }

        // Silent cooldown check
        if (System.currentTimeMillis() - lastTransfer < TRANSFER_COOLDOWN) {
            return;
        }

        final String curseChar = Named.ABYSSAL_CURSE.getPrefix();

        // Play fx to previous player as well
        this.player.playSound(Sound.ENTITY_EVOKER_CAST_SPELL, 0.75f);
        this.player.playSound(Sound.ENTITY_EVOKER_PREPARE_SUMMON, 0.75f);

        this.player.sendMessage("%s &dYou transferred the curse to %s!".formatted(curseChar, player.getName()));

        this.player = player;
        this.lastTransfer = System.currentTimeMillis();

        // Fx
        this.player.sendMessage("%s &5&lYOU ARE NOW CURSED!".formatted(curseChar));
        this.player.sendMessage("%s &dHit another player to transfer the curse or &4&ldie&d!".formatted(curseChar));

        this.player.playSound(Sound.ENTITY_GHAST_HURT, 1.5f);
        this.player.playSound(Sound.ENTITY_GHAST_HURT, 1.75f);
        this.player.playSound(Sound.ENTITY_GHAST_HURT, 2.0f);
    }

    @Override
    public void run(int tick) {
        // If the curse bearer has died, cancel the curse
        if (player.isDeadOrRespawning()) {
            HeroRegistry.ALCHEMIST.removeCurse(this);
            cancel();

            final Location location = player.getLocation();

            player.playWorldSound(location, Sound.BLOCK_FIRE_EXTINGUISH, 0.0f);
            player.spawnWorldParticle(location, Particle.SMOKE, 20, 0.2, 0.2, 0.2, 0.05f);
            return;
        }

        if (tick >= duration) {
            explode();
            return;
        }

        // Fx
        final Location location = player.getMidpointLocation();
        final double unstablePercent = (double) tick / duration;

        player.spawnWorldParticle(location, Particle.WITCH, 5, 0.2d, 0.33d, 0.2d, 0.01f);

        // Sfx
        if (modulo(2)) {
            player.playWorldSound(location, Sound.BLOCK_LAVA_POP, (float) (0.5f + (1.5f * unstablePercent)));
        }

        // Display
        if (modulo(5)) {
            player.sendSubtitle(
                    ObfString.of("&5&l", "ʏᴏᴜ ᴀʀᴇ ᴄᴜʀꜱᴇᴅ", 3, "&5&k0&5&l"), 0, 10, 5
            );
        }
    }

    private void explode() {
        HeroRegistry.ALCHEMIST.removeCurse(this);

        player.setLastDamager(owner);
        player.dieBy(DamageCause.ABYSS_CURSE);

        // Fx
        final Location location = player.getMidpointLocation();

        new TickExecutor()
                .at(0, () -> player.playWorldSound(location, Sound.ENTITY_BLAZE_HURT, 0.75f))
                .at(2, () -> player.playWorldSound(location, Sound.ENTITY_BLAZE_HURT, 1.0f))
                .at(4, () -> player.playWorldSound(location, Sound.ENTITY_BLAZE_HURT, 1.25f))
                .runTaskTimer(0, 1);

        player.playWorldSound(location, Sound.ENTITY_BLAZE_DEATH, 0.75f);
        player.playWorldSound(location, Sound.ENTITY_WARDEN_DEATH, 0.0f);

        player.spawnWorldParticle(location, Particle.WITCH, 50, 1d, 1d, 1d, 1f);
        player.spawnWorldParticle(location, Particle.SCULK_SOUL, 1);

        // Falling block fx
        final World world = player.getWorld();
        final Set<FallingBlock> fallingBlocks = Sets.newHashSet();

        for (int i = 0; i < FALLING_BLOCK_COUNT; i++) {
            final FallingBlock fallingBlock = world.spawn(
                    location, FallingBlock.class, self -> {
                        self.setGravity(false);
                        self.setHurtEntities(false);
                        self.setDropItem(false);
                        self.setCancelDrop(true);

                        self.setBlockData(CollectionUtils.randomElementOrFirst(FALLING_BLOCK_DATA));
                    }
            );

            // Throw randomly
            fallingBlock.setVelocity(
                    new Vector(
                            player.random.nextDoubleBool(0.5d),
                            player.random.nextDouble(-0.3d, 0.3d),
                            player.random.nextDoubleBool(0.5d)
                    )
            );

            fallingBlocks.add(fallingBlock);
        }

        new GameTask() {
            @Override
            public void run() {
                fallingBlocks.forEach(block -> {
                    final Location location = block.getLocation();

                    player.spawnWorldParticle(location, Particle.WITCH, 5, 0.5d, 0.5d, 0.5d, 0.1f);
                    player.spawnWorldParticle(location, Particle.POOF, 5, 0.5d, 0.5d, 0.5d, 0.1f);

                    block.remove();
                });

                fallingBlocks.clear();
            }
        }.shutdownAction(ShutdownAction.IGNORE)
         .runTaskLater(40);

        this.cancel();
    }

}
