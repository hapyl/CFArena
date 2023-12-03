package me.hapyl.fight.game.talents;

import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Response;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * This talent requires a mouse input to be executed.
 */
public abstract class InputTalent extends Talent {

    private String leftClick;
    private String rightClick;

    private int cdLeft;
    private int cdRight;

    private int pointLeft;
    private int pointRight;

    public InputTalent(@Nonnull String name) {
        this(name, "No description provided.");
    }

    public InputTalent(@Nonnull String name, @Nonnull String description) {
        this(name, description, Material.DIAMOND_SWORD);
    }

    public InputTalent(@Nonnull String name, @Nonnull String description, @Nonnull Material material) {
        super(name, description, Type.COMBAT_INPUT);
        setItem(material);

        leftClick = "do nothing";
        rightClick = "do NOTHING";

        cdLeft = 0;
        cdRight = 0;
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
     * @param player - Player who equipped talent.
     */
    @Nonnull
    public Response onEquip(Player player) {
        return Response.OK;
    }

    /**
     * Called whenever player cancels the talent.
     */
    public void onCancel(Player player) {
    }

    public void setLeftClick(String leftClick) {
        this.leftClick = leftClick;
    }

    public String getLeftClick() {
        return leftClick;
    }

    public void setRightClick(String rightClick) {
        this.rightClick = rightClick;
    }

    public String getRightClick() {
        return rightClick;
    }

    @Deprecated()
    @Override
    public final InputTalent setCd(int cd) {
        setCdLeft(cd);
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

    public int getPointLeft() {
        return pointLeft;
    }

    public void setPointLeft(int pointLeft) {
        this.pointLeft = pointLeft;
    }

    public int getPointRight() {
        return pointRight;
    }

    public void setPointRight(int pointRight) {
        this.pointRight = pointRight;
    }

    public void startCdLeft(Player player) {
        super.startCd(player, cdLeft);
    }

    public void startCdRight(Player player) {
        super.startCd(player, cdRight);
    }

    public void setCdLeft(int cdLeft) {
        this.cdLeft = cdLeft;
    }

    public void setCdLeftSec(int cdLeft) {
        setCdLeft(cdLeft * 20);
    }

    public void setCdRight(int cdRight) {
        this.cdRight = cdRight;
    }

    public void setCdRightSec(int cdRight) {
        setCdRight(cdRight * 20);
    }

    public int getCdLeft() {
        return cdLeft;
    }

    public int getCdRight() {
        return cdRight;
    }

    @Override
    public final Response execute(Player player) {
        final Response response = onEquip(player);

        Chat.sendTitle(
                player,
                "&6&lL&e&lCLICK     &6&lR&e&lCLICK",
                ("&ato " + trim(leftClick) + "         &ato " + trim(rightClick)),
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
            return leftClick;
        }

        return rightClick;
    }

    public final void addPoint(Player player, boolean isLeftClick) {
        int point = isLeftClick ? pointLeft : pointRight;

        if (point > 0) {
            GamePlayer.getPlayer(player).addUltimatePoints(point);
        }
    }
}
