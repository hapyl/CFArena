package me.hapyl.fight.gui;

import me.hapyl.fight.Main;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.CollectibleEntry;
import me.hapyl.fight.game.collectible.relic.Relic;
import me.hapyl.fight.game.collectible.relic.RelicHunt;
import me.hapyl.fight.game.collectible.relic.Type;
import me.hapyl.fight.game.reward.Reward;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.Arguments;
import me.hapyl.spigotutils.module.inventory.gui.PlayerDynamicGUI;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.RomanNumber;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public class RelicRewardGUI extends PlayerDynamicGUI {

    private final RelicHunt relicHunt;
    private final int PERMANENT_EXCHANGE_RATE = 5;

    public RelicRewardGUI(Player player) {
        super(player, "Relic Rewards", 6);
        relicHunt = Main.getPlugin().getCollectibles().getRelicHunt();

        openInventory();
    }

    @Override
    public void setupInventory(@Nonnull Arguments arguments) {
        final CollectibleEntry entry = PlayerDatabase.getDatabase(player).collectibleEntry;
        setArrowBack("Relic Hunt", ref -> new RelicHuntGUI(player));

        int slot = 37;
        for (Type type : Type.values()) {
            final String name = Chat.capitalize(type);
            setItem(slot, ItemBuilder.playerHeadUrl(type.getTexture()).setName(name).asIcon());

            // Tiered claims per claimed relics:
            // - Find one
            // - Find half
            // - Find all

            for (int i = 1; i <= 3; i++) {
                final List<Relic> relics = relicHunt.byType(type);
                final List<Relic> foundList = relicHunt.getFoundListByType(player, type);

                final boolean hasClaimed = entry.hasClaimed(type, i);
                final int rewardSlot = slot - (9 * i);
                final Reward reward = relicHunt.getCollectorReward(i);

                if (reward == null) {
                    setItem(rewardSlot, ItemBuilder.of(Material.BARRIER, "no reward LOL!!!!!!!").asIcon());
                    continue;
                }

                final ItemBuilder builder = reward.displayGet(
                        player,
                        ItemBuilder.of(
                                hasClaimed ? Material.MINECART : Material.CHEST_MINECART,
                                "%s Collector %s".formatted(name, RomanNumber.toRoman(i)),
                                "&8One Time Exchange"
                        ).setAmount(i).addLore()
                ).addLore();

                if (hasClaimed) {
                    builder.addLore("&aAlready claimed!");
                    setItem(rewardSlot, builder.asIcon(), ref -> {
                        Chat.sendMessage(player, "&aAlready claimed!");
                        PlayerLib.playSound(player, Sound.BLOCK_ANVIL_LAND, 1.0f);
                    });
                }
                else {
                    boolean canClaim = false;
                    final int totalRelics = relics.size();
                    final int foundRelics = foundList.size();
                    final boolean anyClaimable = totalRelics > 0 && foundRelics > 0;

                    switch (i) {
                        case 1 -> {
                            canClaim = anyClaimable;
                            builder.addSmartLore("Collect at least one %s relic to claim.".formatted(name));
                        }
                        case 2 -> {
                            canClaim = anyClaimable && foundRelics >= (totalRelics / 2);
                            builder.addSmartLore("Collect half of %s relics to claim.".formatted(name));
                        }
                        case 3 -> {
                            canClaim = anyClaimable && foundRelics >= totalRelics;
                            builder.addSmartLore("Collect all of %s relics to claim.".formatted(name));
                        }
                    }

                    builder.addLore();
                    final int finalI = i;

                    if (canClaim) {
                        setItem(rewardSlot, builder.glow().addLore("&aClick to claim!").asIcon(), ref -> {
                            reward.grantReward(player);
                            entry.setClaimed(type, finalI, true);

                            Chat.sendMessage(player, "&aClaimed!");
                            PlayerLib.playSound(player, Sound.BLOCK_CHEST_LOCKED, 0.75f);
                            PlayerLib.playSound(player, Sound.BLOCK_CHEST_CLOSE, 1.25f);

                            update();
                        });
                    }
                    else {
                        setItem(rewardSlot, builder.addLore("&cCannot claim yet!").asIcon(), ref -> {
                            Chat.sendMessage(player, "&cCannot claim yet!");
                        });
                    }
                }
            }

            slot++;
        }

        // Transmitter
        final ItemBuilder exchangeBuilder = ItemBuilder.of(Material.END_PORTAL_FRAME, "Ancient Transmitter", "&8Permanent Exchange", "")
                .addSmartLore("Exchange every &b%s&7 relics for a small reward.".formatted(PERMANENT_EXCHANGE_RATE))
                .addLore();

        final List<Relic> foundList = relicHunt.getFoundList(player);
        final int exchanged = entry.getPermanentExchangeCount();
        final int canExchange = foundList.size() - exchanged;
        final int tier = exchanged / PERMANENT_EXCHANGE_RATE + 1;
        final Reward reward = relicHunt.getExchangeReward(tier);

        exchangeBuilder.addLore("Current Exchange Tier &f&l" + RomanNumber.toRoman(tier));
        exchangeBuilder.addLore();
        exchangeBuilder.addLore("Rewards:");
        reward.display(player, exchangeBuilder);
        exchangeBuilder.addLore();

        if (canExchange < PERMANENT_EXCHANGE_RATE) {
            setItem(34, exchangeBuilder.addLore("&cCannot exchange! (%s/%s)", canExchange, PERMANENT_EXCHANGE_RATE).asIcon(), ref -> {
                Chat.sendMessage(player, "&cNot enough relics!");
                PlayerLib.playSound(player, Sound.BLOCK_ANVIL_LAND, 1.0f);
            });
        }
        else {
            setItem(34, exchangeBuilder.addLore("&aClick to exchange").asIcon(), ref -> {
                reward.grantReward(player);
                entry.incrementPermanentExchangeCount(PERMANENT_EXCHANGE_RATE);

                Chat.sendMessage(player, "&aExchanged!");
                PlayerLib.playSound(player, Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 2.0f);

                update();
            });
        }


    }

}
