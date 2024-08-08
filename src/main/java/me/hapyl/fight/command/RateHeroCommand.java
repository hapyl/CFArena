package me.hapyl.fight.command;

import com.google.common.collect.Maps;
import me.hapyl.fight.database.collection.HeroStatsCollection;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.PlayerRating;
import me.hapyl.fight.game.reward.Reward;
import me.hapyl.fight.game.reward.Rewards;
import me.hapyl.fight.game.setting.Settings;
import me.hapyl.fight.ux.Notifier;
import me.hapyl.eterna.module.chat.CenterChat;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.command.SimplePlayerCommand;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.player.PlayerLib;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;

public class RateHeroCommand extends SimplePlayerCommand {

    private static final Map<Player, Heroes> canRate = Maps.newHashMap();

    public RateHeroCommand(String name) {
        super(name);

        setCooldownTick(Tick.fromSecond(10));
    }

    @Override
    protected void execute(Player player, String[] args) {
        final Heroes hero = getArgument(args, 0).toEnum(Heroes.class);
        final int rating = getArgument(args, 1).toInt();

        final PlayerRating playerRating = PlayerRating.fromInt(rating);

        if (hero == null) {
            Notifier.error(player, "Invalid hero!");
            return;
        }

        if (playerRating == null) {
            Notifier.error(player, "Invalid rating!");
            return;
        }

        final HeroStatsCollection stats = hero.getStats();
        final UUID uuid = player.getUniqueId();
        final boolean hasRated = stats.hasRated(uuid);
        final Heroes canRateHero = RateHeroCommand.canRate.get(player);

        if (!hasRated) {
            if (canRateHero == null) {
                Notifier.error(player, "You cannot rate this hero yet!");
                return;
            }
        }

        if (hero != canRateHero) {
            Notifier.error(player, "&cThis is not the hero you are allowed to rate!");
            return;
        }

        canRate.remove(player);
        stats.setPlayerRating(uuid, playerRating);

        if (hasRated) {
            Notifier.success(player, "Changed {} rating to {}!", hero.getName(), playerRating.getName());
        }
        else {
            Notifier.success(player, "Rated {} as {}!", hero.getName(), playerRating.getName());
            Notifier.success(player, "Thank you for rating this hero, your feedback is appreciated!");

            final Reward reward = Rewards.HERO_RATING_FIRST_TIME.getReward();

            reward.grant(player);
            reward.displayChat(player);

            PlayerLib.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.75f);
        }

        PlayerLib.playSound(player, Sound.ENTITY_VILLAGER_YES, 1.75f);
    }

    public static void allowRatingHeroIfHasNotRatedAlready(@Nonnull Player player, @Nonnull Heroes heroes) {
        if (Settings.SEE_HERO_RATING_MESSAGE.isDisabled(player) || heroes.getStats().hasRated(player.getUniqueId())) {
            return;
        }

        canRate.put(player, heroes);

        Chat.sendCenterMessage(player, "&aYou just played a game as &l%s&a!".formatted(heroes.getName()));
        Chat.sendCenterMessage(player, "&7Would you like to rate your experience?");

        final ComponentBuilder builder = new ComponentBuilder("           ");

        for (PlayerRating rating : PlayerRating.values()) {
            final TextComponent component = new TextComponent(Chat.format(rating.getName()));

            component.setHoverEvent(new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    new Text(Chat.format("&7Click to rate " + rating.getName() + "!"))
            ));

            component.setClickEvent(new ClickEvent(
                    ClickEvent.Action.RUN_COMMAND,
                    "/ratehero %s %s".formatted(heroes.name(), rating.toInt())
            ));

            builder.append(component).append("  ");
        }

        Chat.sendMessage(player, "");
        player.spigot().sendMessage(builder.create());
        Chat.sendMessage(player, "");
        Chat.sendClickableHoverableMessage(
                player,
                "/setting see_hero_rating_message",
                "&7Click to toggle!",
                CenterChat.makeString("&e&nDon't show these messages.")
        );
        Chat.sendMessage(player, "");

        PlayerLib.villagerYes(player);
    }
}
