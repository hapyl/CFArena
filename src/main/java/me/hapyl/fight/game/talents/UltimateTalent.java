package me.hapyl.fight.game.talents;

import com.google.common.collect.Lists;
import me.hapyl.fight.CF;
import me.hapyl.fight.annotate.PreferredReturnValue;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.challenge.ChallengeType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.SoundEffect;
import me.hapyl.fight.game.heroes.UltimateResponse;
import me.hapyl.fight.game.setting.Settings;
import me.hapyl.fight.game.stats.StatType;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.displayfield.DisplayFieldData;
import me.hapyl.fight.util.displayfield.DisplayFieldDataProvider;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;

/**
 * Represents an ultimate talent.
 */
public abstract class UltimateTalent extends Talent implements DisplayFieldDataProvider {

    public static final UltimateTalent UNFINISHED_ULTIMATE = new UltimateTalent("Unfinished Ultimate", 12345) {
        @Nonnull
        @Override
        public UltimateResponse useUltimate(@Nonnull GamePlayer player) {
            return UltimateResponse.error("This ultimate is now finished!");
        }
    };

    private final List<DisplayFieldData> dataFields;

    private final int cost;
    private Sound sound;
    private float pitch;
    private int castDuration;

    public UltimateTalent(String name, int pointCost) {
        this(name, "", pointCost);
    }

    public UltimateTalent(String name, String info, int pointCost) {
        super(name, info, TalentType.DAMAGE);

        this.cost = pointCost;
        this.sound = Sound.ENTITY_ENDER_DRAGON_GROWL;
        this.pitch = 2.0f;
        this.dataFields = Lists.newArrayList();

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

    @Override
    public final Response execute(@Nonnull GamePlayer player) {
        if (hasCd(player)) {
            if (player.isSettingEnabled(Settings.SHOW_COOLDOWN_MESSAGE)) {
                player.sendMessage(
                        "&4&l※ &cYour ultimate is on cooldown for %s!",
                        CFUtils.decimalFormatTick(getCdTimeLeft(player))
                );
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
            player.sendMessage(
                    "&4&l※ &cCannot use ultimate! %s",
                    response.getReason()
            );
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
        Achievements.USE_ULTIMATES.complete(player);

        // Notify
        CF.getPlayers().forEach(other -> {
            other.sendMessage("&b&l※ &b%s used &3&l%s&b!".formatted((player.equals(other) ? "You" : player.getName()), getName()));
            other.playSound(sound, pitch);
        });

        return null;
    }

    @Override
    public UltimateTalent setType(@Nonnull TalentType type) {
        super.setType(type);
        return this;
    }

    public UltimateTalent setDurationSec(int duration) {
        return setDuration(duration * 20);
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
    public UltimateTalent setCooldownSec(int cd) {
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
        return "Ultimate";
    }

}
