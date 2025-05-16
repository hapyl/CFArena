package me.hapyl.fight.game.heroes.ultimate;

import me.hapyl.eterna.module.chat.messagebuilder.Format;
import me.hapyl.eterna.module.chat.messagebuilder.Keybind;
import me.hapyl.eterna.module.chat.messagebuilder.MessageBuilder;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Validate;
import me.hapyl.fight.CF;
import me.hapyl.fight.MaterialData;
import me.hapyl.fight.game.Constants;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.challenge.ChallengeType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.SoundEffect;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.setting.EnumSetting;
import me.hapyl.fight.game.stats.StatType;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.registry.Registries;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents an ultimate talent.
 */
public abstract class UltimateTalent extends Talent {
    
    protected final Hero hero;
    protected final EnumResource resource;
    protected final double cost;
    
    protected SoundEffect sound;
    protected int castDuration;
    protected double minimumCost;
    
    public UltimateTalent(@Nonnull Hero hero, @Nonnull String name, int cost) {
        this(hero, name, EnumResource.ENERGY, cost);
    }
    
    public UltimateTalent(@Nonnull Hero hero, @Nonnull String name, @Nonnull EnumResource resource, int cost) {
        super(Key.ofString(hero.getKeyAsString() + "_ultimate"), name);
        
        this.hero = hero;
        this.resource = resource;
        this.cost = cost;
        this.minimumCost = cost;
        this.sound = SoundEffect.of(Sound.ENTITY_ENDER_DRAGON_GROWL, 2.0f);
        
        setDuration(0); // Default to instance ultimates
    }
    
    @Nonnull
    public EnumResource resource() {
        return resource;
    }
    
    public double minimumCost() {
        return minimumCost;
    }
    
    public void minimumCost(double minimumCost) {
        Validate.isTrue(minimumCost > 0 && minimumCost <= cost, "minimum cost cannot be negative nor higher than the cost");
        this.minimumCost = minimumCost;
    }
    
    public double cost() {
        return cost;
    }
    
    public double consumption() {
        return cost;
    }
    
    @Nonnull
    public abstract UltimateInstance newInstance(@Nonnull GamePlayer player, boolean isFullyCharged);
    
    @Override
    public final @Nullable Response execute(@Nonnull GamePlayer player) {
        if (hasCooldown(player)) {
            if (player.isSettingEnabled(EnumSetting.SHOW_COOLDOWN_MESSAGE)) {
                final int timeLeft = getCooldownTimeLeft(player);
                
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
        
        final double energy = player.getEnergy();
        
        final UltimateInstance instance = newInstance(player, energy >= cost);
        final Response response = instance.response();
        
        // Predicate fails
        if (response.isError()) {
            player.sendErrorMessage("Cannot use ultimate! " + response.getReason());
            player.playSound(SoundEffect.ERROR);
            return null;
        }
        
        // *=* Ultimate Successfully Executed *=* //
        startCooldown(player);
        
        player.decrementEnergy(consumption(), null);
        player.setUsingUltimate(true);
        
        final int duration = getDuration();
        
        new GameTask() { // Do not make this Ticking task because we need tick control
            private boolean hasFinishedCasting;
            private int tick;
            
            @Override
            public void run() {
                // Force end ultimate
                if (instance.isForceEndUltimate() || !player.isUsingUltimate()) {
                    doEndUltimate(true);
                    return;
                }
                
                // Stop if the player has died or aren't using ultimate anymore
                if (player.isDeadOrRespawning()) {
                    doEndUltimate(true);
                    instance.onPlayerDied(player);
                    return;
                }
                
                if (castDuration > 0 && !hasFinishedCasting) {
                    if (tick == 0) {
                        instance.onCastStart();
                    }
                    
                    instance.onCastTick(tick);
                    
                    if (tick == castDuration) {
                        hasFinishedCasting = true;
                        instance.onCastEnd();
                        return; // Return because we need the tick to start at 0, not 1
                    }
                }
                else {
                    final int actualTick = tick - castDuration;
                    
                    if (actualTick == 0) {
                        instance.onExecute();
                    }
                    
                    if (duration > 0) {
                        instance.onTick(actualTick);
                    }
                    
                    if (actualTick == duration) {
                        instance.onEnd();
                        doEndUltimate(false);
                        
                        // Don't end ultimate if it's manual
                        if (duration != Constants.INFINITE_DURATION) {
                            player.setUsingUltimate(false);
                        }
                    }
                }
                
                // Increment tick
                tick++;
            }
            
            private void doEndUltimate(boolean markAsEnded) {
                this.cancel();
                
                // Stats
                player.getStats().addValue(StatType.ULTIMATE_USED, 1);
                
                // Bonds and achievements
                ChallengeType.USE_ULTIMATES.progress(player);
                Registries.achievements().USE_ULTIMATES.complete(player);
                
                if (markAsEnded) {
                    player.setUsingUltimate(false);
                }
            }
        }.runTaskTimer(0, 1);
        
        // Notify
        CF.getPlayers().forEach(other -> {
            final String youOrPlayerName = player.equals(other) ? "You" : player.getName();
            
            // Todo -> reimpl silent ultimates
            
            other.sendMessage("&b\uD83C\uDF1F &b%s used &3&l%s&b!".formatted(
                    youOrPlayerName,
                    UltimateTalent.this.getName()
            ));
            
            other.playSound(sound);
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
        this.sound = new SoundEffect(sound, pitch);
        return this;
    }
    
    @Nonnull
    public SoundEffect getSound() {
        return sound;
    }
    
    @Override
    public UltimateTalent setMaterial(@Nonnull Material material) {
        super.setMaterial(material);
        return this;
    }
    
    @Override
    public UltimateTalent setMaterial(@Nonnull MaterialData material) {
        super.setMaterial(material);
        return this;
    }
    
    @Override
    public UltimateTalent setTexture(@Nonnull String texture64) {
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
        setCooldown((int) ((cost / divide) * 20));
        return this;
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
        return "Ultimate";
    }
    
    /**
     * Checks whether the ultimate can be executed by pressing the button.
     * <p>
     * This check is only used to check if the ultimate can be cast, not to show the <lit>"Ultimate Charged"</lit>!
     * The aforementioned message is handled in {@link #atIncrement(GamePlayer, double, double)} method.
     * </p>
     *
     * @param player - The player to check.
     */
    public boolean canUseUltimate(@Nonnull GamePlayer player) {
        return player.getEnergy() >= minimumCost;
    }
    
    public void atIncrement(@Nonnull GamePlayer player, double previousEnergy, double energy) {
        if (CFUtils.wasPreviousLowerThanValueAndCurrentIsHigherOrEqualsToValue(previousEnergy, energy, minimumCost)
                || CFUtils.wasPreviousLowerThanValueAndCurrentIsHigherOrEqualsToValue(previousEnergy, energy, cost)) {
            final boolean isFullyCharged = energy >= cost;
            
            sendChargedMessage(player, "charged!");
            player.sendSubtitle(resource.getColor().backingColor + "&lᴜʟᴛɪᴍᴀᴛᴇ ᴄʜᴀʀɢᴇᴅ!", 5, 15, 5);
            
            // Play different sound based on charge level
            if (isFullyCharged) {
                player.playSound(Sound.BLOCK_CONDUIT_DEACTIVATE, 2.0f);
            }
            else {
                player.playSound(Sound.BLOCK_VAULT_OPEN_SHUTTER, 0.75f);
            }
        }
    }
    
    @Nonnull
    public String toString(@Nonnull GamePlayer player, double energy) {
        return toString(player, DisplayColor.PRIMARY, energy);
    }
    
    @Nonnull
    public String toString(@Nonnull GamePlayer player, @Nonnull DisplayColor displayColor, double energy) {
        final ChatColor color = ultimateColor(DisplayColor.PRIMARY);
        final String prefix = resource.getPrefixColoredBukkit();
        
        // Currently in use
        if (player.isUsingUltimate()) {
            final long durationLeftMillis = player.getUltimateDurationTimeLeft();
            
            return color + "&lIN USE &8(%s) %s".formatted(CFUtils.formatTick(Tick.fromMinute(durationLeftMillis)), prefix);
        }
        
        final String cargeString = getChargeString(displayColor, energy);
        
        // If on cooldown, show the desaturated percentage with cooldown left
        if (hasCooldown(player)) {
            return "&8&l%s %s(%s) %s".formatted(cargeString, color, CFUtils.formatTick(getCooldownTimeLeft(player)), prefix);
        }
        
        return cargeString + " %s".formatted(prefix);
    }
    
    @Nonnull
    public String getChargeString(@Nonnull DisplayColor displayColor, double energy) {
        final double percent = energy / cost;
        final ChatColor color = ultimateColor(displayColor);
        final String chargePercent = ultimateColor(DisplayColor.PRIMARY) + "&l%.0f%%".formatted(percent * 100);
        
        // If the minimum cost differs, show charged with actual percentage
        if (minimumCost != cost && energy >= minimumCost && energy < cost) {
            return color + "&lCHARGED! " + chargePercent;
        }
        
        return percent >= 1.0 ? color + "&lCHARGED!" : chargePercent;
    }
    
    @Nonnull
    public ChatColor ultimateColor(@Nonnull DisplayColor displayColor) {
        return displayColor == DisplayColor.PRIMARY ? ChatColor.AQUA : ChatColor.DARK_AQUA;
    }
    
    @Override
    public void juice(@Nonnull ItemBuilder builder) {
        builder.glow();
    }
    
    @Override
    public void juiceDetails(@Nonnull ItemBuilder builder) {
        builder.addLore("Ultimate Cost: &f&l%s %s".formatted(cost, resource.getPrefix()));
        
        if (minimumCost != cost) {
            builder.addLore("Minimum Cost: &f&l%s %s".formatted(minimumCost, resource.getPrefix()));
        }
        
        builder.addLore("Cast Duration: &f&l%s".formatted((castDuration == 0 ? "Instant" : CFUtils.formatTick(castDuration))));
    }
    
    protected void sendChargedMessage(GamePlayer player, String string) {
        final MessageBuilder builder = new MessageBuilder();
        
        builder.append("&b&l\uD83C\uDF1F &3Your ultimate has %s&3 Press ".formatted(string));
        builder.append(Keybind.SWAP_HANDS).color(ChatColor.GOLD).format(Format.BOLD);
        builder.append("&3 to use it!");
        
        builder.send(player.getEntity());
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
    
    public enum DisplayColor {
        PRIMARY,
        SECONDARY
    }
}
