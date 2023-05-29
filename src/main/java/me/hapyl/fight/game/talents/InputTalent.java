package me.hapyl.fight.game.talents;

import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Response;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * This talent requires a mouse input to be executed.
 */
public abstract class InputTalent extends Talent {

    protected final InputTalentData leftData;
    protected final InputTalentData rightData;

    public InputTalent(@Nonnull String name) {
        this(name, Material.DIAMOND_SWORD);
    }

    public InputTalent(@Nonnull String name, @Nonnull Material material) {
        super(name, "null", Type.COMBAT_INPUT);
        setItem(material);

        leftData = new InputTalentData(true);
        rightData = new InputTalentData(false);
    }

    @Override
    public void appendLore(@Nonnull ItemBuilder builder) {
    }

    @Nonnull
    public final InputTalentData getLeftData() {
        return leftData;
    }

    @Nonnull
    public final InputTalentData getRightData() {
        return rightData;
    }

    /**
     * Called whenever player left clicks after equipping the talent.
     *
     * @param player - Player who clicked.
     */
    @Nonnull
    public abstract Response onLeftClick(Player player);

    /**
     * Called whenever player right clicks after equipping the talent.
     *
     * @param player - Player who clicked.
     */
    @Nonnull
    public abstract Response onRightClick(Player player);

    /**
     * Called whenever player equips talent. Would be {@link #execute(Player)} for normal talents.
     *
     * @param player - Player, who equipped talent.
     */
    @Nonnull
    public Response onEquip(Player player) {
        return Response.OK;
    }

    /**
     * Called whenever a player cancels the talent.
     */
    public void onCancel(Player player) {
    }

    @Deprecated
    @Override
    public final InputTalent setCd(int cd) {
        leftData.setCooldown(cd);
        return this;
    }

    @Deprecated
    @Override
    public final Talent setCdSec(int cd) {
        return setCd(cd * 20);
    }

    @Deprecated
    @Override
    public final void startCd(Player player) {
    }

    @Override
    public int getPoint() {
        return 0;
    }

    @Deprecated
    @Override
    public void setPoint(int point) {
    }

    public void startCdLeft(Player player) {
        super.startCd(player, leftData.getCooldown());
    }

    public void startCdRight(Player player) {
        super.startCd(player, rightData.getCooldown());
    }

    @Override
    public final Response execute(Player player) {
        final Response response = onEquip(player);

        Chat.sendTitle(
                player,
                "&6&lL&e&lCLICK     &6&lR&e&lCLICK",
                ("&ato " + trim(leftData.action) + "         &ato " + trim(rightData.action)),
                5,
                10000,
                5
        );

        return response;
    }

    public String trim(String name) {
        if (name.length() > 20) {
            return name.substring(0, 20);
        }

        return name;
    }

    public String getUsage(boolean isLeftClick) {
        if (isLeftClick) {
            return leftData.action;
        }

        return rightData.action;
    }

    public final void addPoint(Player player, boolean isLeftClick) {
        int point = isLeftClick ? leftData.pointGeneration : rightData.pointGeneration;

        if (point > 0) {
            GamePlayer.getPlayer(player).addUltimatePoints(point);
        }
    }
}
