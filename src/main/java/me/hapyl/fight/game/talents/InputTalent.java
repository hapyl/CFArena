package me.hapyl.fight.game.talents;

import me.hapyl.fight.game.Event;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import org.bukkit.Material;

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
        this(name, material, TalentType.DAMAGE);
    }

    public InputTalent(@Nonnull String name, @Nonnull Material material, @Nonnull TalentType type) {
        super(name, "null", type);
        setItem(material);

        leftData = new InputTalentData(true);
        rightData = new InputTalentData(false);
    }

    /**
     * @deprecated {@link InputTalentData#setType(TalentType)}
     */
    @Deprecated
    @Override
    public Talent setType(@Nonnull TalentType type) {
        return this;
    }

    @Nonnull
    @Override
    public String getTalentClassType() {
        return "Input Talent";
    }

    @Nonnull
    @Override
    public String getTypeFormattedWithClassType() {
        return getTalentClassType();
    }

    @Override
    public void appendLore(@Nonnull ItemBuilder builder) {
        builder.addTextBlockLore("""
                                
                &e&lLEFT CLICK&e to %s
                &8%s
                %s
                &6&lRIGHT CLICK&6 to %s
                &8%s
                %s
                """.formatted(
                // Left
                leftData.getAction(),
                leftData.getType().getName(),
                format(leftData),

                // Right
                rightData.getAction(),
                rightData.getType().getName(),
                format(rightData)
        ));
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
    public abstract Response onLeftClick(@Nonnull GamePlayer player);

    /**
     * Called whenever player right clicks after equipping the talent.
     *
     * @param player - Player who clicked.
     */
    @Nonnull
    public abstract Response onRightClick(@Nonnull GamePlayer player);

    /**
     * Called whenever player equips talent. Would be {@link Talent#execute(GamePlayer)} for normal talents.
     *
     * @param player - Player, who equipped talent.
     */
    @Nonnull
    public Response onEquip(@Nonnull GamePlayer player) {
        return Response.OK;
    }

    /**
     * Called whenever a player uses this talent, regardless if it was successful, left or right-clicked.
     *
     * @param player - Player, who used the talent.
     */
    @Event
    public void onUse(@Nonnull GamePlayer player) {
    }

    /**
     * Called whenever a player cancels the talent.
     */
    @Event
    public void onCancel(@Nonnull GamePlayer player) {
    }

    @Deprecated
    @Override
    public final InputTalent setCooldown(int cd) {
        leftData.setCooldown(cd);
        return this;
    }

    @Deprecated
    @Override
    public final Talent setCooldownSec(float cd) {
        return setCooldown((int) (cd * 20));
    }

    @Deprecated
    @Override
    public final void startCd(@Nonnull GamePlayer player) {
    }

    @Override
    public int getPoint() {
        return 0;
    }

    @Deprecated
    @Override
    public void setPoint(int point) {
    }

    public void startCdLeft(GamePlayer player) {
        super.startCd(player, leftData.getCooldown());
    }

    public void startCdRight(GamePlayer player) {
        super.startCd(player, rightData.getCooldown());
    }

    @Override
    public final Response execute(@Nonnull GamePlayer player) {
        final Response response = onEquip(player);

        player.sendTitle("&6&lL&e&lCLICK     &6&lR&e&lCLICK",
                ("&ato " + trim(leftData.action) + "         &ato " + trim(rightData.action)),
                1, 10000, 1
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

    public final void addPoint(GamePlayer player, boolean isLeftClick) {
        int point = isLeftClick ? leftData.pointGeneration : rightData.pointGeneration;

        if (point > 0) {
            player.addEnergy(point);
        }
    }


    private String format(InputTalentData data) {
        String string = data.getDescription();

        string = StaticFormat.NAME.format(string, this);
        string = StaticFormat.DURATION.format(string, data);
        string = StaticFormat.COOLDOWN.format(string, data);

        return string;
    }

    private String format(@Nonnull String string, @Nonnull InputTalentData data) {
        string = StaticFormat.NAME.format(string, this);
        string = StaticFormat.DURATION.format(string, data);
        string = StaticFormat.COOLDOWN.format(string, data);

        return string;
    }
}
