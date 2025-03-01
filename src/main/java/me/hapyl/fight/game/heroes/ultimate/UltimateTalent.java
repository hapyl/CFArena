package me.hapyl.fight.game.heroes.ultimate;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.chat.messagebuilder.Format;
import me.hapyl.eterna.module.chat.messagebuilder.Keybind;
import me.hapyl.eterna.module.chat.messagebuilder.MessageBuilder;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.Constants;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.challenge.ChallengeType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.SoundEffect;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.setting.EnumSetting;
import me.hapyl.fight.game.stats.StatType;
import me.hapyl.fight.game.talents.OverchargeUltimateTalent;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.registry.Registries;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.displayfield.DisplayFieldData;
import me.hapyl.fight.util.displayfield.DisplayFieldDataProvider;
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

    @Nonnull
    public abstract UltimateInstance newInstance(@Nonnull GamePlayer player);

    public boolean isSilent() {
        return isSilent;
    }

    public void setSilent(boolean silent) {
        isSilent = silent;
    }

    @Override
    public final Response execute(@Nonnull GamePlayer player) {
        if (hasCd(player)) {
            if (player.isSettingEnabled(EnumSetting.SHOW_COOLDOWN_MESSAGE)) {
                final int timeLeft = getCdTimeLeft(player);

                if (timeLeft >= Constants.MAX_COOLDOWN) {
                    player.sendErrorMessage("Your ultimate is on cooldown!");
                }
                else {
                    player.sendErrorMessage("Your ultimate is on cooldown for %s!".formatted(CFUtils.formatTick(timeLeft)));
                }

                player.playSound(SoundEffect.ERROR);
            }
            return null;
        }

        if (player.isUsingUltimate()) {
            player.sendErrorMessage("You are already using ultimate!");
            player.playSound(SoundEffect.ERROR);
            return null;
        }

        final UltimateInstance instance = newInstance(player);
        final Response response = instance.response();

        // Predicate fails
        if (response.isError()) {
            player.sendErrorMessage("Cannot use ultimate! " + response.getReason());
            player.playSound(SoundEffect.ERROR);
            return null;
        }

        // *=* Ultimate Successfully Executed *=* //

        startCd(player);

        player.setEnergy(0);
        player.setUsingUltimate(true);

        final int duration = getDuration();

        new GameTask() {
            private int tick;
            private boolean castFinished;

            @Override
            public void run() {
                // Force end ultimate
                if (instance.isForceEndUltimate()) {
                    doEndUltimate();
                    player.setUsingUltimate(false);
                    return;
                }

                // Stop if the player has died or aren't using ultimate anymore
                if (player.isDeadOrRespawning() || !player.isUsingUltimate()) {
                    doEndUltimate();
                    return;
                }

                // Ultimate has cast state
                if (castDuration > 0 && !castFinished) {
                    // Call onCastStart
                    if (tick == 0) {
                        instance.onCastStart();
                    }
                    else if (tick != castDuration) {
                        instance.onCastTick(tick);
                    }
                    else {
                        instance.onCastEnd();
                        castFinished = true;
                        return; // Make sure we return because we want to start from tick 0
                    }
                }
                // Else execute
                else {
                    final int actualTick = tick - castDuration;

                    if (actualTick == 0) {
                        instance.onExecute();
                    }
                    else if (actualTick != duration && duration > 0) {
                        instance.onTick(actualTick);
                    }
                    else {
                        instance.onEnd();
                        doEndUltimate();

                        // Don't end ultimate if it's manual
                        if (duration != Constants.INFINITE_DURATION) {
                            player.setUsingUltimate(false);
                        }

                        return;
                    }
                }

                ++tick;
            }

            private void doEndUltimate() {
                this.cancel();
                instance.onPlayerDied();

                // Stats
                player.getStats().addValue(StatType.ULTIMATE_USED, 1);

                // Bonds and achievements
                ChallengeType.USE_ULTIMATES.progress(player);
                Registries.getAchievements().USE_ULTIMATES.complete(player);
            }
        }.runTaskTimer(0, 1);

        // Notify
        CF.getPlayers().forEach(other -> {
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

    public UltimateTalent setCastDurationSec(float durationSec) {
        return setCastDuration((int) (durationSec * 20));
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

    protected void setManualDuration() {
        setDuration(Constants.INFINITE_DURATION);
    }

    @Nonnull
    protected static UltimateInstance error(@Nonnull String message) {
        return new UltimateInstance() {
            @Nonnull
            @Override
            public Response response() {
                return Response.error(message);
            }

            @Override
            public void onExecute() {
                throw new IllegalStateException("dont execute error instances");
            }
        };
    }

    @Nonnull
    protected static UltimateInstanceBuilder builder() {
        return new UltimateInstanceBuilder();
    }

    @Nonnull
    protected static UltimateInstance execute(@Nonnull Runnable runnable) {
        return new UltimateInstance() {
            @Override
            public void onExecute() {
                runnable.run();
            }
        };
    }

}
