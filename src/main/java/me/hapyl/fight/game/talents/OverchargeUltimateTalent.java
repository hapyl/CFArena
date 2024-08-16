package me.hapyl.fight.game.talents;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.UltimateResponse;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public abstract class OverchargeUltimateTalent extends UltimateTalent {

    private final int overchargeCost;

    private String overchargeDescription = "";

    public OverchargeUltimateTalent(@Nonnull String name, int pointCost, int overchargeCost) {
        super(name, pointCost);

        if (overchargeCost <= pointCost) {
            throw new IllegalArgumentException("Overcharge must be greater than normal cost!");
        }

        this.overchargeCost = overchargeCost;
    }

    public void setOverchargeDescription(@Nonnull String description) {
        this.overchargeDescription = description;
    }

    @Override
    public final void appendLore(@Nonnull ItemBuilder builder) {
        builder.addTextBlockLore("""
                
                &d&nᴛʜɪꜱ ᴜʟᴛɪᴍᴀᴛᴇ ᴄᴀɴ ʙᴇ ᴏᴠᴇʀᴄʜᴀʀɢᴇᴅ
                &8&o;;Overcharged the ultimate consumes more energy and empowers it.
                
                """);

        builder.addTextBlockLore(overchargeDescription, "&7 ", ItemBuilder.DEFAULT_SMART_SPLIT_CHAR_LIMIT);
    }

    @Nonnull
    public abstract UltimateResponse useUltimate(@Nonnull GamePlayer player, @Nonnull ChargeType type);

    @Nonnull
    @Override
    public final UltimateResponse useUltimate(@Nonnull GamePlayer player) {
        final double energy = player.getEnergy();

        return useUltimate(player, energy >= overchargeCost ? ChargeType.OVERCHARGED : ChargeType.CHARGED);
    }

    @Override
    public final int getCost() {
        // This will allow to 'overflow' the energy, since the actual cost is overcharge one
        return overchargeCost;
    }

    @Nonnull
    @Override
    public String getTalentClassType() {
        return "Overcharge Ultimate";
    }

    @Override
    public void atEnergy(@Nonnull GamePlayer player, double energy) {
        if (energy == overchargeCost) {
            sendChargedMessage(player, "&d&l&oovercharged");

            player.sendTitle("&3※&b&l※&3※", "&d&k|| &d&lULTIMATE OVERCHARGED! &d&k||", 5, 15, 5);

            player.playSound(Sound.BLOCK_CONDUIT_DEACTIVATE, 1.75f);
            player.playSound(Sound.ENTITY_WARDEN_SONIC_BOOM, 1.25f);
            return;
        }

        super.atEnergy(player, energy);
    }


}
