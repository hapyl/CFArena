package me.hapyl.fight.game.heroes.archive.mage;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.Formatted;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public abstract class MageSpell extends Talent implements Formatted {

    private ItemStack item;

    public MageSpell(@Nonnull String name) {
        super(name);
    }

    @Override
    public void createItem() {
        super.createItem();

        this.item = new ItemBuilder(getItem(), getName().replace(" ", ""))
                .addClickEvent(player -> {
                    final GamePlayer gamePlayer = CF.getPlayer(player);

                    if (gamePlayer == null) {
                        player.sendMessage("Cannot execute right now!");
                        return;
                    }

                    execute(gamePlayer);
                })
                .setName(getName() + (Color.BUTTON.bold() + " RIGHT CLICK"))
                .build();
    }

    @Nonnull
    public ItemStack getSpellItem() {
        if (this.item == null) {
            createItem();
        }

        return this.item;
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Mage mage = Heroes.MAGE.getHero(Mage.class);

        mage.setUsingUltimate(player, true, getDuration());
        useSpell(player);

        player.snapToWeapon();

        // Remove items
        mage.spellWyvernHeart.removeItem(player);
        mage.spellDragonSkin.removeItem(player);

        // Fx
        player.sendMessage("&aYou have used &l%s&a!", getName());
        player.playWorldSound(Sound.ITEM_FLINTANDSTEEL_USE, 0.0f);

        return Response.OK;
    }

    @Nonnull
    @Override
    public String getFormatted() {
        return """
                &a&l%s
                %s
                """.formatted(getName(), getDescription());
    }

    protected abstract void useSpell(@Nonnull GamePlayer player);

    private void removeItem(GamePlayer player) {
        if (this.item == null) {
            return;
        }

        player.getInventory().remove(this.item.getType());
    }
}
