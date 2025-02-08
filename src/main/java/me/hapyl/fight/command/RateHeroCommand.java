package me.hapyl.fight.command;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.chat.CenterChat;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.command.SimplePlayerCommand;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.fight.Message;
import me.hapyl.fight.database.async.HeroStatsAsynchronousDocument;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.PlayerRating;
import me.hapyl.fight.game.reward.Reward;
import me.hapyl.fight.game.reward.StaticReward;
import me.hapyl.fight.game.setting.EnumSetting;
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

    private static final Map<Player, Hero> canRate = Maps.newHashMap();

    public RateHeroCommand(String name) {
        super(name);

        setCooldownTick(Tick.fromSecond(10));
    }

    @Override
    protected void execute(Player player, String[] args) {
        final Hero hero = HeroRegistry.ofStringOrNull(getArgument(args, 0).toString());
        final int rating = getArgument(args, 1).toInt();

        final PlayerRating playerRating = PlayerRating.fromInt(rating);

        if (hero == null) {
            Message.error(player, "Invalid hero!");
            return;
        }

        if (playerRating == null) {
            Message.error(player, "Invalid rating!");
            return;
        }

        final HeroStatsAsynchronousDocument stats = hero.getStats();
        final UUID uuid = player.getUniqueId();
        final boolean hasRated = stats.hasRated(uuid);
        final Hero canRateHero = RateHeroCommand.canRate.get(player);

        if (!hasRated) {
            if (canRateHero == null) {
                Message.error(player, "You cannot rate this hero yet!");
                return;
            }
        }

        if (hero != canRateHero) {
            Message.error(player, "&cThis is not the hero you were allowed to rate!");
            return;
        }

        canRate.remove(player);
        stats.setPlayerRating(uuid, playerRating);

        if (hasRated) {
            Message.success(player, "Changed {%s} rating to {%s}!".formatted(hero.getName(), playerRating.getName()));
        }
        else {
            Message.success(player, "Rated {%s} as {%s}!".formatted(hero.getName(), playerRating.getName()));
            Message.success(player, "Thank you for rating this hero, your feedback is appreciated!");

            final Reward reward = StaticReward.HERO_RATING_FIRST_TIME;

            reward.grant(player);
            reward.sendRewardMessage(player);

            Message.sound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.75f);
        }

        Message.sound(player, Sound.ENTITY_VILLAGER_YES, 1.75f);
    }

    public static void allowRatingHeroIfHasNotRatedAlready(@Nonnull Player player, @Nonnull Hero hero) {
        if (EnumSetting.SEE_HERO_RATING_MESSAGE.isDisabled(player) || hero.getStats().hasRated(player.getUniqueId())) {
            return;
        }

        canRate.put(player, hero);

        Chat.sendCenterMessage(player, "&aYou just played a game as &l%s&a!".formatted(hero.getName()));
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
                    "/ratehero %s %s".formatted(hero.getKeyAsString(), rating.toInt())
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
