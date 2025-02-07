package me.hapyl.fight.game.talents;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.registry.Registries;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public abstract class OverchargeUltimateTalent extends UltimateTalent {

    private final int overchargeCost;

    private String overchargeDescription = "";

    public OverchargeUltimateTalent(@Nonnull Hero hero, @Nonnull String name, int pointCost, int overchargeCost) {
        super(hero, name, pointCost);

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
    public abstract UltimateInstance newInstance(@Nonnull GamePlayer player, @Nonnull ChargeType type);

    @Nonnull
    @Override
    public final UltimateInstance newInstance(@Nonnull GamePlayer player) {
        final double energy = player.getEnergy();
        final boolean isOvercharged = energy >= overchargeCost;

        final UltimateInstance instance = newInstance(player, isOvercharged ? ChargeType.OVERCHARGED : ChargeType.CHARGED);
        final Response response = instance.response();

        if (response.isOk()) {
            Registries.getAchievements().OVERCHARGED.complete(player);
        }

        return instance;
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
    public void atEnergy(@Nonnull GamePlayer player, double previousEnergy, double energy) {
        if (energy == overchargeCost) {
            sendChargedMessage(player, "&d&l&oovercharged");

            player.sendTitle("&3※&b&l※&3※", "&d&k|| &d&lULTIMATE OVERCHARGED! &d&k||", 5, 15, 5);

            player.playSound(Sound.BLOCK_CONDUIT_DEACTIVATE, 1.75f);
            player.playSound(Sound.ENTITY_WARDEN_SONIC_BOOM, 1.25f);
            return;
        }

        super.atEnergy(player, previousEnergy, energy);
    }


}
