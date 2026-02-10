package me.hapyl.fight.game.talents;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.ultimate.EnumResource;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.registry.Registries;
import org.bukkit.ChatColor;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public abstract class OverchargeUltimateTalent extends UltimateTalent {
    
    private String overchargeDescription = "";
    
    public OverchargeUltimateTalent(@Nonnull Hero hero, @Nonnull String name, int cost, int overchargeCost) {
        super(hero, name, EnumResource.ENERGY /* Force to use energy */, overchargeCost);
        
        if (overchargeCost <= cost) {
            throw new IllegalArgumentException("Overcharge must be greater than normal cost!");
        }
        
        // Cost becomes minimum cost
        this.minimumCost = cost;
    }
    
    // The cost is always the maximum cost
    @Override
    public final double consumption() {
        return cost;
    }
    
    public void setOverchargeDescription(@Nonnull String description) {
        this.overchargeDescription = description;
    }
    
    @Override
    public void juiceDescription(@Nonnull ItemBuilder builder) {
        builder.addTextBlockLore("""
                                 
                                 &d&nᴛʜɪꜱ ᴜʟᴛɪᴍᴀᴛᴇ ᴄᴀɴ ʙᴇ ᴏᴠᴇʀᴄʜᴀʀɢᴇᴅ
                                 &8&o;;Overcharged the ultimate consumes more energy and empowers it.
                                 
                                 """);
        
        builder.addTextBlockLore(overchargeDescription, "&7 ", ItemBuilder.DEFAULT_SMART_SPLIT_CHAR_LIMIT);
    }
    
    @Nonnull
    public abstract UltimateInstance newInstance(@Nonnull GamePlayer player, @Nonnull ChargeType type);
    
    @Nonnull
    @Override
    public final UltimateInstance newInstance(@Nonnull GamePlayer player, boolean isFullyCharged) {
        final double energy = player.getEnergy();
        final boolean isOvercharged = energy >= cost;
        
        final UltimateInstance instance = newInstance(player, isOvercharged ? ChargeType.OVERCHARGED : ChargeType.CHARGED);
        final Response response = instance.response();
        
        if (response.isOk()) {
            Registries.achievements().OVERCHARGED.complete(player);
        }
        
        return instance;
    }
    
    
    @Nonnull
    @Override
    public String getTalentClassType() {
        return "Overcharge Ultimate";
    }
    
    @Nonnull
    @Override
    public ChatColor ultimateColor(@Nonnull DisplayColor displayColor) {
        return displayColor == DisplayColor.PRIMARY ? ChatColor.LIGHT_PURPLE : ChatColor.DARK_PURPLE;
    }
    
    @Override
    public void atIncrement(@Nonnull GamePlayer player, double previousEnergy, double energy) {
        if (energy >= cost) {
            sendChargedMessage(player, "&d&l&oovercharged");
            
            player.sendSubtitle(
                    "&d&k|| &d&lᴜʟᴛɪᴍᴀᴛᴇ ᴏᴠᴇʀᴄʜᴀʀɢᴇᴅ! &d&k||",
                    5, 15, 5
            );
            
            player.playSound(Sound.BLOCK_CONDUIT_DEACTIVATE, 1.75f);
            player.playSound(Sound.ENTITY_WARDEN_SONIC_BOOM, 1.25f);
            return;
        }
        
        super.atIncrement(player, previousEnergy, energy);
    }
    
    @Nonnull
    @Override
    public String getChargeString(@Nonnull DisplayColor displayColor, double energy) {
        if (energy >= cost) {
            return ultimateColor(displayColor) + "&lOVERCHARGED!";
        }
        else if (energy > minimumCost) {
            final double percent = (energy - minimumCost) / (cost - minimumCost);
            final ChatColor colorSuper = super.ultimateColor(displayColor);
            final ChatColor colorPrimary = ultimateColor(DisplayColor.PRIMARY);
            
            return colorSuper + "&lCHARGED! " + colorPrimary + "&l%.0f%%".formatted(percent * 100);
        }
        else {
            final double percent = energy / minimumCost;
            
            return super.ultimateColor(DisplayColor.PRIMARY) + "&l%.0f%%".formatted(percent * 100);
        }
    }
}
