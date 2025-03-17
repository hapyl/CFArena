package me.hapyl.fight.gui;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.util.RomanNumber;
import me.hapyl.fight.CF;
import me.hapyl.fight.Message;
import me.hapyl.fight.database.entry.CollectibleEntry;
import me.hapyl.fight.game.collectible.relic.Relic;
import me.hapyl.fight.game.collectible.relic.RelicHunt;
import me.hapyl.fight.game.collectible.relic.Type;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.reward.Reward;
import me.hapyl.fight.gui.styled.ReturnData;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.gui.styled.StyledTexture;
import me.hapyl.fight.gui.styled.eye.RelicHuntGUI;
import me.hapyl.fight.registry.Registries;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.List;

public class RelicRewardGUI extends StyledGUI {

    private final RelicHunt relicHunt;


    public RelicRewardGUI(Player player) {
        super(player, "Relic Rewards", Size.FIVE);

        relicHunt = CF.getPlugin().getRelicHunt();

        openInventory();
    }

    @Nullable
    @Override
    public ReturnData getReturnData() {
        return ReturnData.of("Relic Hunt", RelicHuntGUI::new);
    }

    @Override
    public void onUpdate() {
        final CollectibleEntry entry = CF.getDatabase(player).collectibleEntry;

        setHeader(StyledTexture.ICON_RELIC_REWARDS.asIcon());

        // Tiered
        int slot = 37;
        for (Type type : Type.values()) {
            final ItemBuilder relicBuilder = ItemBuilder.playerHeadUrl(type.getTexture());
            relicBuilder.setName(type.getName());
            relicBuilder.addLore();
            relicBuilder.addSmartLore(type.getDescription());

            setItem(slot, relicBuilder.asIcon());

            // Tiers
            for (int index = 1; index <= 3; index++) {
                final int finalIndex = index;
                final boolean hasClaimedTier = entry.hasClaimed(type, index);
                final int tierSlot = slot - (9 * index);
                final ItemBuilder builder = new ItemBuilder(hasClaimedTier ? Material.MINECART : Material.CHEST_MINECART);

                builder.setName(Chat.capitalize(type.name()) + " Collector " + RomanNumber.toRoman(index));
                builder.addLore("&8One Time Exchange");
                builder.addLore();

                final Reward reward = relicHunt.getCollectorReward(index);

                if (reward == null) {
                    continue;
                }

                reward.getDescription(player).forEach(builder::addLore);
                builder.addLore();

                switch (index) {
                    case 1 -> builder.addSmartLore("Collect at least one relic of this type.", "&7&o");
                    case 2 -> builder.addSmartLore("Collect at least half of the relics of this type.", "&7&o");
                    case 3 -> builder.addSmartLore("Collect all the relics of this type.", "&7&o");
                }

                builder.addLore();

                if (hasClaimedTier) {
                    builder.addLore(Color.SUCCESS + "Already claimed!");
                    setItem(tierSlot, builder.asIcon(), click -> {
                        Chat.sendMessage(player, Color.SUCCESS + "Already claimed!");
                        PlayerLib.playSound(player, Sound.BLOCK_ANVIL_LAND, 1.0f);
                    });
                }
                else {
                    final boolean canClaim = entry.canClaim(type, index);

                    if (canClaim) {
                        builder.glow();
                        builder.addLore(Color.BUTTON + "Click to claim!");

                        setItem(tierSlot, builder.asIcon(), click -> {
                            reward.grant(player);
                            entry.setClaimed(type, finalIndex, true);

                            Message.success(player, "Claimed!");
                            PlayerLib.playSound(player, Sound.BLOCK_CHEST_LOCKED, 0.75f);
                            PlayerLib.playSound(player, Sound.BLOCK_CHEST_CLOSE, 1.25f);

                            update();
                        });
                    }
                    else {
                        builder.addLore(Color.ERROR + "Cannot claim yet!");
                        setItem(tierSlot, builder.asIcon(), click -> {
                            Message.error(player, "Cannot claim yet!");
                            PlayerLib.playSound(player, Sound.BLOCK_ANVIL_LAND, 1.0f);
                        });
                    }
                }
            }

            slot++;
        }

        // All relics
        final Cosmetic cosmetic = Registries.cosmetics().RELIC_HUNTER;
        final ItemBuilder builder = new ItemBuilder(Material.DIAMOND);

        builder.setName("Exclusive Cosmetic");
        builder.addLore("&8One Time Exchange");
        builder.addLore();
        builder.addSmartLore("Collect every single relic to claim:", "&7&o");
        builder.addLore(cosmetic.getFormatted());
        builder.addLore();

        if (cosmetic.isUnlocked(player)) {
            builder.addLore(Color.SUCCESS + "Already claimed!");

            setItem(25, builder.asIcon(), click -> {
                Message.error(player, "Already claimed!");
                PlayerLib.villagerNo(player);
            });
        }
        else {
            if (relicHunt.hasClaimedAll(player)) {
                builder.addLore(Color.BUTTON + "Click to claim!");
                builder.glow();

                setItem(25, builder.asIcon(), click -> {
                    cosmetic.setUnlocked(player, true);

                    Message.success(player, "Claimed!");
                    PlayerLib.villagerYes(player);
                    PlayerLib.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0f);

                    update();
                });
            }
            else {
                builder.addLore(Color.ERROR + "Cannot claim yet!");

                setItem(25, builder.asIcon(), click -> {
                    Message.error(player, "Cannot claim yet!");
                    PlayerLib.playSound(player, Sound.BLOCK_ANVIL_LAND, 1.0f);
                });
            }
        }

        // Permanent
        final List<Relic> foundList = relicHunt.getFoundList(player);
        final int totalExchanged = entry.getPermanentExchangeCount();
        final int canExchange = foundList.size() - totalExchanged;
        final int exchangeTier = totalExchanged / CollectibleEntry.PERMANENT_EXCHANGE_RATE + 1;
        final Reward reward = relicHunt.getExchangeReward(exchangeTier);

        final ItemBuilder exchangeBuilder = new ItemBuilder(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
        exchangeBuilder.setAmount(Math.max(1, exchangeTier));
        exchangeBuilder.setName("Relic Stabilizer");
        exchangeBuilder.addLore("&8Permanent Exchange");
        exchangeBuilder.addLore();
        exchangeBuilder.addLore("Current Exchange Tier: &b&l" + RomanNumber.toRoman(exchangeTier));
        exchangeBuilder.addLore();
        exchangeBuilder.addLore("Rewards:");

        reward.getDescription(player).forEach(exchangeBuilder::addLore);
        exchangeBuilder.addLore();

        if (canExchange < CollectibleEntry.PERMANENT_EXCHANGE_RATE) {
            exchangeBuilder.addLore(
                    Color.ERROR + "Cannot exchange! (%s/%s)".formatted(canExchange, CollectibleEntry.PERMANENT_EXCHANGE_RATE)
            );

            setItem(34, exchangeBuilder.asIcon(), click -> {
                Message.error(player, "Cannot exchange!");
                PlayerLib.playSound(player, Sound.BLOCK_ANVIL_LAND, 1.0f);
            });
        }
        else {
            exchangeBuilder.addLore(Color.BUTTON + "Click to exchange!");

            setItem(34, exchangeBuilder.asIcon(), click -> {
                reward.grant(player);
                entry.incrementPermanentExchangeCount(CollectibleEntry.PERMANENT_EXCHANGE_RATE);

                Message.success(player, "Successfully exchanged!");
                PlayerLib.playSound(player, Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 2.0f);

                update();
            });
        }
    }

}
