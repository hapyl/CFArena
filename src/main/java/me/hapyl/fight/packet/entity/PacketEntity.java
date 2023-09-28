package me.hapyl.fight.packet.entity;

import com.google.common.collect.Sets;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.spigotutils.module.reflect.DataWatcherType;
import me.hapyl.spigotutils.module.reflect.Reflect;
import me.hapyl.spigotutils.module.util.TeamHelper;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.World;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class PacketEntity<T extends Entity> implements PacketEntityBase {

    public final int entityId;
    private final T entity;
    private final Set<Player> players;

    public PacketEntity(@Nonnull T entity, @Nonnull Location location) {
        this.entity = entity;
        this.players = Sets.newHashSet();
        this.entityId = Reflect.getEntityId(entity);

        Reflect.setEntityLocation(entity, location);
    }

    public T getEntity() {
        return entity;
    }

    public boolean isShowingTo(Player player) {
        return players.contains(player);
    }

    @Override
    public void spawn(Player player) {
        players.add(player);
        Reflect.createEntity(entity, player);
    }

    @Override
    public void hide(Player player) {
        players.remove(player);
        hide0(player);
    }

    @Override
    public void destroy() {
        players.forEach(this::hide0);
        players.clear();
    }

    @Override
    public void setVisible(boolean visibility) {
        final byte bitMask = getDataWatcherValue(DataWatcherType.BYTE, 0, (byte) 0);
        setDataWatcherValue(DataWatcherType.BYTE, 0, (byte) (visibility ? (bitMask & ~0x20) : (bitMask | 0x20)));
    }

    @Override
    public void setCollision(boolean collision) {
        players.forEach(player -> {
            final TeamHelper fakeEntity = TeamHelper.FAKE_ENTITY;
            final Team team = fakeEntity.getTeam(player.getScoreboard());
            final String uuid = entity.cu();

            if (collision) {
                team.removeEntry(uuid);
            }
            else {
                team.addEntry(uuid);
            }
        });
    }

    @Override
    public void setSilent(boolean silent) {
        setDataWatcherValue(DataWatcherType.BOOL, 4, silent);
    }

    @Override
    public void setGravity(boolean gravity) {
        setDataWatcherValue(DataWatcherType.BOOL, 5, !gravity);
    }

    @Override
    public void setMarker() {
        setSilent(true);
        setGravity(false);
        setVisible(false);
        setCollision(false);
    }

    @Override
    public void teleport(@Nonnull Location location) {
        Reflect.setEntityLocation(entity, location);
        players.forEach(player -> {
            Reflect.updateEntityLocation(entity, player);
        });
    }

    @Override
    public int getId() {
        return entityId;
    }

    protected void updateMetadata() {
        players.forEach(player -> {
            Reflect.updateMetadata(entity, player);
        });
    }

    protected <D> void setDataWatcherValue(DataWatcherType<D> type, int key, D value) {
        final DataWatcher dataWatcher = getDataWatcher();

        Reflect.setDataWatcherValue0(dataWatcher, type.get().a(key), value);
        updateMetadata();
    }

    @Nullable
    protected <D> D getDataWatcherValue(DataWatcherType<D> type, int key) {
        return getDataWatcherValue(type, key, null);
    }

    @Nonnull
    protected <D> D getDataWatcherValue(DataWatcherType<D> type, int key, D def) {
        final D value = Reflect.getDataWatcherValue(entity, type, key);

        return value == null ? def : value;
    }

    private void hide0(Player player) {
        Reflect.destroyEntity(entity, player);
    }

    private DataWatcher getDataWatcher() {
        return Reflect.getDataWatcher(entity);
    }

    @Nonnull
    public static World getWorld(Location location) {
        return Reflect.getMinecraftWorld(CFUtils.getWorld(location));
    }

}
