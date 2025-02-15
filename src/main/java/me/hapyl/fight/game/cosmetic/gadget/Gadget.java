package me.hapyl.fight.game.cosmetic.gadget;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.Message;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.talents.Cooldown;
import me.hapyl.fight.registry.Registries;
import me.hapyl.fight.util.ItemStacks;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public abstract class Gadget extends Cosmetic implements Cooldown {

    private ItemStack item;
    private int cooldown;

    public Gadget(@Nonnull Key key, @Nonnull String name) {
        super(key, name, Type.GADGET);
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }

    @Override
    public Cooldown setCooldown(int cooldown) {
        this.cooldown = cooldown;
        return this;
    }

    @Nonnull
    public ItemStack getItem(@Nonnull Player player) {
        if (item == null) {
            final ItemBuilder builder = new ItemBuilder(icon, getKey())
                    .setName(getName() + Color.BUTTON.bold() + " RIGHT CLICK")
                    .addLore("&8Gadget")
                    .addLore()
                    .addTextBlockLore(description)
                    .setCooldownGroup(getKey())
                    .addClickEvent(this::execute0);

            if (texture != null) {
                builder.setType(Material.PLAYER_HEAD);
                builder.setHeadTextureUrl(texture);
            }

            this.item = builder.build();
        }

        return item;
    }

    public void give(@Nonnull Player player) {
        if (Manager.current().isGameInProgress()) {
            Message.error(player, "&cCannot get gadgets while in game!");
            return;
        }

        player.getInventory().setItem(5, getItem(player));
    }

    @Override
    public void onEquip(@Nonnull Player player) {
        give(player);
    }

    @Override
    public void onUnequip(@Nonnull Player player) {
        player.getInventory().setItem(5, ItemStacks.AIR);
    }

    @Nonnull
    public abstract Response execute(@Nonnull Player player);

    public void execute0(Player player) {
        if (Manager.current().isGameInProgress()) {
            Message.error(player, "&cCannot use gadgets while in game!");
            return;
        }

        if (PlayerLib.isOnCooldown(player, getKey())) {
            Message.error(player, "This gadget is on cooldown!");
            PlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
            return;
        }

        final Response response = execute(player);

        if (!response.isOk()) {
            Message.error(player, "Cannot use gadget! " + response.getReason());
            return;
        }

        PlayerLib.setCooldown(player, getKey(), cooldown);
        Registries.getAchievements().USE_GADGETS.complete(player);
    }

    @Override
    public void onDisplay(@Nonnull Display display) {
        final Player player = display.getPlayer();

        if (player == null) {
            Debug.warn("Cannot use gadget for null player! " + getName());
            return;
        }

        execute0(player);
    }
}
