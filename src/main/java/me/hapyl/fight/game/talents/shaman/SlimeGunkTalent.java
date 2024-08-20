package me.hapyl.fight.game.talents.shaman;

import me.hapyl.fight.CF;
import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperInstance;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import javax.annotation.Nonnull;

public class SlimeGunkTalent extends Talent implements Listener {

    @DisplayField(suffix = "blocks") protected final double diameter = 5.0d;
    @DisplayField protected final int period = 5;

    protected final BlockData blockData = Material.VINE.createBlockData();
    protected final TemperInstance temperInstance = Temper.SLIME_GUNK.newInstance()
            .decrease(AttributeType.SPEED, 0.1); // 50%

    private final PlayerMap<Snowball> snowballMap = PlayerMap.newMap();
    private final PlayerMap<SlimeGunk> gunkMap = PlayerMap.newMap();

    public SlimeGunkTalent(@Nonnull DatabaseKey key) {
        super(key, "Gunk of Slime");

        setDescription("""
                Throw a gunk of slime in the direction you're facing.
                
                Upon landing, creates a &atoxic&7 fields that &2poisons&7 and drastically &3slow &cenemies&7.
                """
        );

        setType(TalentType.IMPAIR);
        setItem(Material.SLIME_BALL);
        setDurationSec(3);
        setCooldownSec(12);
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        gunkMap.removeAnd(player, SlimeGunk::cancel);
    }

    @Override
    public void onStop(@Nonnull GameInstance instance) {
        snowballMap.forEachAndClear(Snowball::remove);
        gunkMap.forEachAndClear(SlimeGunk::cancel);
    }

    @EventHandler()
    public void handleProjectileLand(ProjectileHitEvent ev) {
        final Projectile entity = ev.getEntity();

        if (!(entity instanceof Snowball snowball)) {
            return;
        }

        final ProjectileSource shooter = entity.getShooter();

        if (!(shooter instanceof Player playerShooter)) {
            return;
        }

        final GamePlayer player = CF.getPlayer(playerShooter);

        if (player == null) {
            return;
        }

        final Snowball playerSnowball = snowballMap.get(player);

        if (snowball != playerSnowball) {
            return;
        }

        snowballMap.remove(player);

        spawnGunk(player, snowball.getLocation());
        snowball.remove();
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        player.launchProjectile(Snowball.class, self -> {
            self.setItem(new ItemStack(getMaterial()));

            snowballMap.put(player, self);
        });

        // Fx
        player.playWorldSound(Sound.ENTITY_LLAMA_SPIT, 0.0f);

        return Response.OK;
    }

    public void spawnGunk(GamePlayer player, Location location) {
        final SlimeGunk oldGunk = gunkMap.remove(player);

        if (oldGunk != null) {
            oldGunk.cancel();
        }

        gunkMap.put(player, new SlimeGunk(this, player, location));
    }
}
