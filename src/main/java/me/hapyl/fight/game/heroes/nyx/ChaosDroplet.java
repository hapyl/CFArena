package me.hapyl.fight.game.heroes.nyx;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Removable;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.util.ComparableTo;
import me.hapyl.fight.util.ItemStackRandomizedData;
import me.hapyl.fight.util.Located;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class ChaosDroplet implements Removable, Located, ComparableTo<Item> {

    protected final GamePlayer nyx;
    protected final ArmorStand entity;

    public ChaosDroplet(GamePlayer player, Location location) {
        this.nyx = player;
        this.entity = Entities.ARMOR_STAND.spawn(location, self -> {
            self.setSmall(true);
            self.setInvisible(true);
            self.setMarker(true);
            self.getEquipment().setHelmet(item());
        });
    }

    @Nonnull
    public GamePlayer getPlayer() {
        return nyx;
    }

    @Nonnull
    @Override
    public Location getLocation() {
        return entity.getLocation();
    }

    @Override
    public void remove() {
        this.entity.remove();
    }

    @Override
    public int compareTo(@Nonnull Item o) {
        return ComparableTo.comparingObjects(entity, o);
    }

    public boolean affect(@Nonnull GamePlayer player, @Nonnull NyxData data) {
        // Store the current droplet count before removing the droplet
        final Location location = getLocation();
        final int dropletCount = data.dropletCount();

        // Affecting here because yes
        if (this.nyx.isSelfOrTeammate(player)) {
            if (player.isFullHealth()) {
                return false;
            }

            final double healing = TalentRegistry.CHAOS_GROUND.healing.getHealing(dropletCount);

            player.heal(healing, this.nyx);

            // Fx
            player.playWorldSound(location, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1.75f);
            player.playWorldSound(location, Sound.ITEM_FIRECHARGE_USE, 2.0f);
        }
        else {
            player.damage(TalentRegistry.CHAOS_GROUND.getDamage(), nyx, EnumDamageCause.CHAOS);

            player.spawnWorldParticle(location, Particle.RAID_OMEN, 10, 0.5, 0.25, 0.5, 0.025f);
            player.playWorldSound(Sound.ENTITY_EVOKER_FANGS_ATTACK, 0.75f);
        }

        entity.remove();
        return true;
    }

    public static ItemStack item() {
        return ItemStackRandomizedData.of("ed5d46bafb21727276d202ccd130f598a6956c79a4cf07a143f74c97b1be918c");
    }

}
