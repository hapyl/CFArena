package me.hapyl.fight.game.cosmetic.gadget;

import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.talents.Cooldown;
import me.hapyl.fight.registry.Registries;
import me.hapyl.fight.util.ItemStacks;
import me.hapyl.fight.Notifier;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public abstract class Gadget extends Cosmetic implements Cooldown {

    private ItemStack item;
    private int cooldown;

    public Gadget(String name, Rarity rarity, String texture) {
        this(name, rarity, Material.PLAYER_HEAD, texture);
    }

    public Gadget(String name, Rarity rarity, Material material) {
        this(name, rarity, material, null);
    }

    protected Gadget(String name, Rarity rarity, Material material, String texture) {
        super(name, null, Type.GADGET, rarity, material);
        this.texture = texture;
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
    public ItemStack getItem(Player player) {
        if (item == null) {
            final ItemBuilder builder = new ItemBuilder(icon, name.replace(" ", "_"))
                    .setName(name + Color.BUTTON.bold() + " RIGHT CLICK")
                    .addLore("&8Gadget")
                    .addLore()
                    .addTextBlockLore(description)
                    .addClickEvent(this::execute0);

            addExtraLore(builder, player);

            if (texture != null) {
                builder.setHeadTextureUrl(texture);
            }

            this.item = builder.build();
        }

        return item;
    }

    public void give(@Nonnull Player player) {
        if (Manager.current().isGameInProgress()) {
            Notifier.error(player, "&cCannot get gadgets while in game!");
            return;
        }

        player.getInventory().setItem(5, getItem(player));
    }

    @Override
    public void onEquip(Player player) {
        give(player);
    }

    @Override
    public void onUnequip(Player player) {
        player.getInventory().setItem(5, ItemStacks.AIR);
    }

    @Nonnull
    public abstract Response execute(@Nonnull Player player);

    public void execute0(Player player) {
        if (Manager.current().isGameInProgress()) {
            Notifier.error(player, "&cCannot use gadgets while in game!");
            return;
        }

        if (player.hasCooldown(icon)) {
            Notifier.error(player, "This gadget is on cooldown!");
            PlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
            return;
        }

        final Response response = execute(player);

        if (!response.isOk()) {
            Notifier.error(player, "Cannot use gadget! " + response.getReason());
            return;
        }

        player.setCooldown(icon, cooldown);
        Registries.getAchievements().USE_GADGETS.complete(player);
    }

    @Override
    protected void onDisplay(Display display) {
        final Player player = display.getPlayer();

        if (player == null) {
            Debug.warn("Cannot use gadget for null player! " + getName());
            return;
        }

        execute0(player);
    }
}
