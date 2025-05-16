package me.hapyl.fight.game.talents;

import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This talent requires a mouse input to be executed.
 * fixme -> I don't like how this works
 */
public abstract class InputTalent extends Talent {
    
    protected final InputTalentData leftData;
    protected final InputTalentData rightData;
    
    protected InputTalent(@Nonnull Key key, @Nonnull String name) {
        super(key, name);
        
        leftData = new InputTalentData(true);
        rightData = new InputTalentData(false);
    }
    
    /**
     * @deprecated {@link InputTalentData#setType(TalentType)}
     */
    @Deprecated
    @Override
    public Talent setType(@Nonnull TalentType type) throws IllegalStateException {
        throw new IllegalStateException("InputTalent uses separate types for left and right clicks!");
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
    public void juiceDescription(@Nonnull ItemBuilder builder) {
        builder.addTextBlockLore("""
                                 
                                 &e&lʟᴇꜰᴛ ᴄʟɪᴄᴋ&e to %s
                                 &8%s
                                 %s
                                 &6&lʀɪɢʜᴛ ᴄʟɪᴄᴋ&6 to %s
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
    @EventLike
    public void onUse(@Nonnull GamePlayer player) {
    }
    
    /**
     * Called whenever a player cancels the talent.
     */
    @EventLike
    public void onCancel(@Nonnull GamePlayer player) {
    }
    
    @Override
    public final InputTalent setCooldown(int cd) {
        leftData.setCooldown(cd);
        rightData.setCooldown(cd);
        return this;
    }
    
    @Override
    public final Talent setCooldownSec(float cd) {
        return setCooldown((int) (cd * 20));
    }
    
    @Deprecated
    @Override
    public final void startCooldown(@Nonnull GamePlayer player) {
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
        super.startCooldown(player, leftData.getCooldown());
    }
    
    public void startCdRight(GamePlayer player) {
        super.startCooldown(player, rightData.getCooldown());
    }
    
    @Override
    public final @Nullable Response execute(@Nonnull GamePlayer player) {
        final Response response = onEquip(player);
        
        player.sendTitle(
                "&6&lʟᴇꜰᴛ     &6&lʀɪɢʜᴛ",
                "&ato %s         &ato %s".formatted(trim(leftData.action), trim(rightData.action)),
                1, 10000, 1
        );
        
        return response;
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
            player.incrementEnergy(point);
        }
    }
    
    private String format(InputTalentData data) {
        String string = data.getDescription();
        
        string = StaticTalentFormat.NAME.format(string, this);
        string = StaticTalentFormat.DURATION.format0(string, data);
        string = StaticTalentFormat.COOLDOWN.format0(string, data);
        
        return string;
    }
    
    private static String trim(String name) {
        if (name.length() > 20) {
            return name.substring(0, 20);
        }
        
        return name;
    }
    
    private static IllegalStateException ise(String reason) {
        return new IllegalStateException(reason);
    }
}
