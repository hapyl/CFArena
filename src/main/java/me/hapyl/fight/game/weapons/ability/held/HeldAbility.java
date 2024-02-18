package me.hapyl.fight.game.weapons.ability.held;

import me.hapyl.fight.CF;
import me.hapyl.fight.event.custom.game.GameEndEvent;
import me.hapyl.fight.event.custom.game.GameStartEvent;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public abstract class HeldAbility extends Ability implements Listener, Runnable {

    private final PlayerMap<HeldData> heldData;
    private final long maxIdleDuration;

    public HeldAbility(@Nonnull String name, @Nonnull String description, long maxIdleDuration) {
        super(name, description);

        this.heldData = PlayerMap.newMap();
        this.maxIdleDuration = maxIdleDuration;

        CF.registerEvents(this);
    }

    @Nonnull
    public abstract Weapon getWeapon();

    @Nonnull
    public HeldData getHeldData(@Nonnull GamePlayer player) {
        return heldData.computeIfAbsent(player, fn -> new HeldData(player, maxIdleDuration));
    }

    @Override
    public final Response execute(@Nonnull GamePlayer player, @Nonnull ItemStack item) {
        return Response.OK;
    }

    @Override
    public final void run() {
        heldData.forEach((player, data) -> {
            if (data.isIdling()) {
                data.unit = 0;
            }
        });
    }

    @EventHandler()
    public void handleGameStartEvent(GameStartEvent ev) {
        new GameTask() {
            @Override
            public void run() {
                HeldAbility.this.run();
            }
        }.runTaskTimer(0, 1);
    }

    @EventHandler()
    public void handleGameEndEvent(GameEndEvent ev) {
        heldData.forEachAndClear(HeldData::remove);
    }

    public abstract boolean onUnitGain(@Nonnull GamePlayer player, int totalUnits);

    @EventHandler()
    public void handlePlayerInteractEvent(PlayerInteractEvent ev) {
        final Player player = ev.getPlayer();

        if (ev.getHand() == EquipmentSlot.OFF_HAND) { // evil! 👿
            return;
        }

        final Action action = ev.getAction();

        if (action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR) {
            return;
        }

        final ItemStack item = ev.getItem();

        if (item == null) {
            return;
        }

        final Weapon weapon = getWeapon();

        if (!weapon.getItem().isSimilar(item)) {
            return;
        }

        final GamePlayer gamePlayer = CF.getPlayer(player);

        if (gamePlayer == null) {
            return;
        }

        final HeldData data = getHeldData(gamePlayer);
        final boolean shouldAdd = onUnitGain(gamePlayer, data.unit + 1);

        if (shouldAdd) {
            data.addUnit(1);
        }
    }

}
