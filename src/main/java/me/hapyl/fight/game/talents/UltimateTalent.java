package me.hapyl.fight.game.talents;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.chat.messagebuilder.Format;
import me.hapyl.eterna.module.chat.messagebuilder.Keybind;
import me.hapyl.eterna.module.chat.messagebuilder.MessageBuilder;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.fight.CF;
import me.hapyl.fight.annotate.PreferredReturnValue;
import me.hapyl.fight.chat.ChatChannel;
import me.hapyl.fight.game.Constants;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.challenge.ChallengeType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.SoundEffect;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.UltimateResponse;
import me.hapyl.fight.game.setting.Settings;
import me.hapyl.fight.game.stats.StatType;
import me.hapyl.fight.registry.Key;
import me.hapyl.fight.registry.Registries;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.displayfield.DisplayFieldData;
import me.hapyl.fight.util.displayfield.DisplayFieldDataProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;

/**
 * Represents an ultimate talent.
 */
public abstract class UltimateTalent extends Talent implements DisplayFieldDataProvider {

    protected final int cost;

    private final List<DisplayFieldData> dataFields;

    private Sound sound;
    private float pitch;
    private int castDuration;
    private boolean isSilent;

    public UltimateTalent(@Nonnull Hero hero, @Nonnull String name, int pointCost) {
        super(Key.ofString(hero.getKeyAsString() + "_ultimate"), name);

        this.cost = pointCost;
        this.sound = Sound.ENTITY_ENDER_DRAGON_GROWL;
        this.pitch = 2.0f;
        this.dataFields = Lists.newArrayList();
        this.isSilent = false;

        setDuration(0);
    }

    /**
     * Unleashes this hero's ultimate.
     *
     * @return the ultimate callback. The callback will be executed if, and only if the ultimate has a cast duration.
     */
    @Nonnull
    @PreferredReturnValue("UltimateCallback#OK")
    public abstract UltimateResponse useUltimate(@Nonnull GamePlayer player);

    public void setSilent(boolean silent) {
        isSilent = silent;
    }

    public boolean isSilent() {
        return isSilent;
    }

    @Override
    public final Response execute(@Nonnull GamePlayer player) {
        if (hasCd(player)) {
            if (player.isSettingEnabled(Settings.SHOW_COOLDOWN_MESSAGE)) {
                final int timeLeft = getCdTimeLeft(player);

                if (timeLeft >= Constants.MAX_COOLDOWN) {
                    player.sendMessage("&4&l※ &cYour ultimate is on cooldown!");
                }
                else {
                    player.sendMessage(
                            "&4&l※ &cYour ultimate is on cooldown for %s!".formatted(CFUtils.formatTick(timeLeft))
                    );
                }

                player.playSound(SoundEffect.ERROR);
            }
            return null;
        }

        if (player.isUsingUltimate()) {
            player.sendMessage("&4&l※ &cYou are already using ultimate!");
            player.playSound(SoundEffect.ERROR);
            return null;
        }

        final UltimateResponse response = useUltimate(player);

        // Predicate fails
        if (response.isError()) {
            player.sendMessage("&4&l※ &cCannot use ultimate! %s".formatted(response.getReason()));
            player.playSound(SoundEffect.ERROR);
            return null;
        }

        // *=* Ultimate Successfully Executed *=* //

        startCd(player);
        player.setEnergy(0);

        // Call onCastFinished
        if (castDuration > 0) {
            player.schedule(response::onCastFinished, castDuration);
        }

        final int duration = getDuration();

        // Call onUltimateEnd
        if (duration > 0) {
            player.setUsingUltimate(true);

            player.schedule(() -> {
                // Make the player is still using ultimate!
                // If not, it was cancelled manually, which is ok.
                if (!player.isUsingUltimate()) {
                    return;
                }

                player.setUsingUltimate(false);
                response.onUltimateEnd(player);
            }, duration);
        }

        // Stats
        player.getStats().addValue(StatType.ULTIMATE_USED, 1);

        // Progress bond
        ChallengeType.USE_ULTIMATES.progress(player);

        // Achievement
        Registries.getAchievements().USE_ULTIMATES.complete(player);

        // Notify
        CF.getPlayers().forEach(new Consumer<>() {
            @Override
            public void accept(GamePlayer other) {
                final String youOrPlayerName = player.equals(other) ? "You" : player.getName();

                if (isSilent) {
                    // Only show the message for teammates and the player who used the ultimate for silent ultimates
                    if (player.isSelfOrTeammate(other)) {
                        other.sendMessage("&b&oShhh... &7※ &7%s used &8&l%s&7!".formatted(
                                youOrPlayerName,
                                UltimateTalent.this.getName()
                        ));
                        other.playSound(sound, pitch);
                    }
                }
                else {
                    other.sendMessage("&b※ &b%s used &3&l%s&b!".formatted(
                            youOrPlayerName,
                            UltimateTalent.this.getName()
                    ));
                    other.playSound(sound, pitch);
                }
            }

            private Component formatUltimateMessage(GamePlayer other) {
                final TextColor colorPrimary = isSilent ? NamedTextColor.GRAY : NamedTextColor.AQUA;
                final TextColor colorSecondary = isSilent ? NamedTextColor.DARK_GRAY : NamedTextColor.DARK_AQUA;

                return Component.text().append(
                        Component.text(" ", colorPrimary, TextDecoration.BOLD),
                        Component.text(player.equals(other) ? "You" : player.getName(), colorSecondary),
                        Component.text(" used ", colorPrimary),
                        Component.text(UltimateTalent.this.getName() + "!", colorSecondary, TextDecoration.BOLD)
                ).build();
            }
        });

        return Response.OK;
    }

    @Override
    public UltimateTalent setType(@Nonnull TalentType type) {
        super.setType(type);
        return this;
    }

    public UltimateTalent setDurationSec(float duration) {
        return setDuration((int) (duration * 20));
    }

    public int getDuration() {
        return super.getDuration() + castDuration; // include cast duration
    }

    public UltimateTalent setDuration(int duration) {
        super.setDuration(duration);
        return this;
    }

    public UltimateTalent setSound(Sound sound, float pitch) {
        setSound(sound);
        setPitch(pitch);
        return this;
    }

    public float getPitch() {
        return pitch;
    }

    public UltimateTalent setPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    public Sound getSound() {
        return sound;
    }

    public UltimateTalent setSound(Sound sound) {
        this.sound = sound;
        return this;
    }

    /**
     * Gets the minimum cost to use this ultimate.
     *
     * @return the minimum cost to use this ultimate.
     * @see OverchargeUltimateTalent
     */
    public int getMinCost() {
        return cost;
    }

    public int getCost() {
        return cost;
    }

    public UltimateTalent setItem(Material material) {
        super.setItem(material);
        return this;
    }

    @Override
    public UltimateTalent setItem(Material material, Consumer<ItemBuilder> function) {
        super.setItem(material, function);
        return this;
    }

    @Override
    public UltimateTalent setTexture(String texture64) {
        super.setTexture(texture64);
        return this;
    }

    @Override
    public UltimateTalent setCooldown(int cd) {
        super.setCooldown(cd);
        return this;
    }

    @Override
    public UltimateTalent setCooldownSec(float cd) {
        super.setCooldownSec(cd);
        return this;
    }

    public UltimateTalent defaultCdFromCost() {
        return setCdFromCost(2);
    }

    public UltimateTalent setCdFromCost(int divide) {
        setCooldown((getCost() / divide) * 20);
        return this;
    }

    @Nonnull
    @Override
    public List<DisplayFieldData> getDisplayFieldData() {
        return dataFields;
    }

    public int getCastDuration() {
        return castDuration;
    }

    public UltimateTalent setCastDuration(int duration) {
        this.castDuration = duration;
        return this;
    }

    @Nonnull
    @Override
    public String getTalentClassType() {
        return isSilent ? "Silent Ultimate" : "Ultimate";
    }

    public boolean canUseUltimate(@Nonnull GamePlayer player) {
        return player.getEnergy() >= cost;
    }

    public void atEnergy(@Nonnull GamePlayer player, double previousEnergy, double energy) {
        if (previousEnergy < cost && energy >= cost) {
            sendChargedMessage(player, "charged!");

            player.sendTitle("&3※&b&l※&3※", "&b&lULTIMATE CHARGED!", 5, 15, 5);
            player.playSound(Sound.BLOCK_CONDUIT_DEACTIVATE, 2.0f);
        }
    }

    protected void sendChargedMessage(GamePlayer player, String string) {
        final MessageBuilder builder = new MessageBuilder();

        builder.append("&b&l※ &bYour ultimate has %s&b Press ".formatted(string));
        builder.append(Keybind.SWAP_HANDS).color(ChatColor.YELLOW).format(Format.BOLD);
        builder.append("&b to use it!");

        builder.send(player.getPlayer());
    }

}
