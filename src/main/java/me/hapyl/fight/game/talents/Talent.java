package me.hapyl.fight.game.talents;

import com.google.common.collect.Lists;
import me.hapyl.fight.Main;
import me.hapyl.fight.annotate.ExecuteOrder;
import me.hapyl.fight.game.*;
import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.effect.storage.SlowingAuraEffect;
import me.hapyl.fight.game.stats.StatContainer;
import me.hapyl.fight.util.Nulls;
import me.hapyl.fight.util.displayfield.DisplayFieldProvider;
import me.hapyl.fight.util.displayfield.DisplayFieldSerializer;
import me.hapyl.spigotutils.module.annotate.Super;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.math.Numbers;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

/**
 * Base talent.
 */
public abstract class Talent extends NonNullItemCreator
        implements GameElement, DisplayFieldProvider, Nameable, Timed, Cooldown {

    public static final Talent NULL = null;
    public static int DYNAMIC = -1;
    private final Type type;
    private final List<String> attributeDescription;
    private String name;
    @Nonnull
    private String description;

    private ItemStack itemStats;
    private int startAmount;
    private Material material;
    private String texture64;
    private String castMessage;
    private String altUsage;
    private Consumer<ItemBuilder> itemFunction;
    private int point;
    private int cd;
    private int duration;
    private boolean autoAdd;

    public Talent(@Nonnull String name) {
        this(name, "", Type.COMBAT);
    }

    public Talent(@Nonnull String name, @Nonnull String description) {
        this(name, description, Type.COMBAT);
    }

    public Talent(@Nonnull String name, @Nonnull String description, @Nonnull Material material) {
        this(name, description);
        setItem(material);
    }

    public Talent(@Nonnull String name, @Nonnull String description, @Nonnull Type type) {
        this.name = name;
        this.description = description;
        this.attributeDescription = Lists.newArrayList();
        this.type = type;
        this.material = Material.BEDROCK;
        this.startAmount = 1;
        this.altUsage = "This talent is not given when the game starts, but there is a way to use it.";
        this.autoAdd = true;
        this.point = 1;
        this.duration = 0;
    }

    // defaults to 1 point per 10 seconds of cooldown
    public void defaultPointGeneration() {
        point = calcPointGeneration(cd);
    }

    public void setAltUsage(String altUsage) {
        this.altUsage = altUsage;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public Talent setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    @Override
    public Talent setDurationSec(int duration) {
        return setDuration(duration * 20);
    }

    public int getPoint() {
        return point;
    }

    @ExecuteOrder(after = { "setCooldown(int)", "setCooldownSec(int)" })
    public void setPoint(int point) {
        this.point = point;
    }

    public String getCastMessage() {
        return castMessage;
    }

    public void setCastMessage(String castMessage) {
        this.castMessage = castMessage;
    }

    public void addAttributeDescription(String key, Object value) {
        this.attributeDescription.add(DisplayFieldSerializer.DEFAULT_FORMATTER.format(key, String.valueOf(value)));
    }

    /**
     * Adds an empty description.
     */
    public void addDescription() {
        addDescription("\n");
    }

    /**
     * Adds description to existing one.
     *
     * @param description - Description.
     * @param format      - Formatters if needed.
     */
    public void addDescription(@Nonnull String description, @Nullable Object... format) {
        this.description += description.formatted(format);
    }

    /**
     * Adds description to existing one, then, appends a new line.
     *
     * @param description - Description.
     * @param format      - Formatters if needed.
     */
    public void addNlDescription(@Nonnull String description, @Nullable Object... format) {
        addDescription(description, format);
        this.description += "\n";
    }

    /**
     * Replaces the existing description with provided.
     *
     * @param description - New description.
     * @param format      - Formatters if needed.
     */
    public void setDescription(@Nonnull String description, Object... format) {
        this.description = description.formatted(format);
    }

    public boolean isAutoAdd() {
        return autoAdd;
    }

    public void setAutoAdd(boolean autoAdd) {
        this.autoAdd = autoAdd;
    }

    public Material getMaterial() {
        return material;
    }

    public ItemStack getItemAttributes() {
        if (itemStats == null) {
            createItem();
        }

        return itemStats;
    }

    /**
     * Called once on game start.
     */
    @Override
    public void onStart() {
    }

    /**
     * Called once on game stop.
     */
    @Override
    public void onStop() {
    }

    /**
     * Called every time player dies.
     *
     * @param player - Player that died.
     */
    public void onDeath(Player player) {
    }

    public void displayDuration(Player player, int duration, byte b) {
        // TODO: 010, Mar 10, 2023 -> this
    }

    @Override
    public void appendLore(@Nonnull ItemBuilder builder) {
    }

    public void createItem() {
        final ItemBuilder builderItem = ItemBuilder.of(material)
                .setName(name)
                .addLore("&8%s %s", Chat.capitalize(type), type == Type.ULTIMATE ? "" : "Talent")
                .addLore();

        builderItem.setAmount(startAmount);

        // Add head texture if an item is a player head
        if (material == Material.PLAYER_HEAD) {
            if (texture64 != null) {
                builderItem.setHeadTextureUrl(texture64);
            }
        }

        // Execute functions is present
        Nulls.runIfNotNull(itemFunction, function -> function.accept(builderItem));

        // Append lore
        // Now using text block lore
        try { // fixme: try-catch for debugging, remove on prod
            // fix % I fucking hate that java uses % as formatter fucking why
            description = description.replace("%", "%%");
            description = TalentFormat.formatTalent(description, this);

            builderItem.addTextBlockLore(description);
        } catch (Exception e) {
            final StackTraceElement[] stackTrace = e.getStackTrace();

            builderItem.addLore("&4&lERROR FORMATTING, REPORT THIS WITH A CODE BELOW");
            builderItem.addLore("&e@" + getName());
            builderItem.addLore("&e" + stackTrace[0].toString());

            Main.getPlugin().getLogger().severe(description);
            e.printStackTrace();
        }

        appendLore(builderItem);

        // Is ability having alternative usage, tell how to use it
        if (!autoAdd) {
            builderItem.addLore("");
            builderItem.addSmartLore(altUsage, "&8&o", 35);
        }

        if (this instanceof UltimateTalent) {
            builderItem.glow();
        }

        // Attributes
        super.setItem(builderItem.asIcon());

        final ItemBuilder builderAttributes = new ItemBuilder(getItemUnsafe());

        builderAttributes.removeLore();
        builderAttributes.setName(name).addLore("&8Attributes").addLore();

        // Cooldown a
        if (cd > 0) {
            builderAttributes.addLore("Cooldown%s: &f&l%ss".formatted(
                    this instanceof ChargedTalent ? " between charges" : "",
                    BukkitUtils.roundTick(this.cd)
            ));
        }
        else if (cd <= -1) {
            builderAttributes.addLore("Cooldown: &f&lDynamic");
        }

        // Duration
        if (duration > 0) {
            builderAttributes.addLore("Duration: &f&l%ss", BukkitUtils.roundTick(duration));
        }

        // Points generation
        if (point > 0 && !(this instanceof PassiveTalent || this instanceof UltimateTalent)) {
            builderAttributes.addLore("Point%s Generation: &f&l%s", point == 1 ? "" : "s", point);
        }

        // Display Fields
        DisplayFieldSerializer.serialize(builderAttributes, this);

        // Extra fields
        if (!attributeDescription.isEmpty()) {
            for (String string : attributeDescription) {
                builderAttributes.addLore(string);
            }
        }

        // Recharge time
        if (this instanceof ChargedTalent charge) {
            final int maxCharges = charge.getMaxCharges();
            final int rechargeTime = charge.getRechargeTime();

            builderAttributes.addLore("Max Charges: &f&l%s", maxCharges);
            if (rechargeTime >= 0) {
                builderAttributes.addLore("Recharge Time: &f&l%ss", BukkitUtils.roundTick(rechargeTime));
            }
        }
        else if (this instanceof UltimateTalent ult) {
            builderAttributes.addLore("Ultimate Cost: &f&l%s ※", ult.getCost());
            builderAttributes.glow();
            // ※
        }

        itemStats = builderAttributes.asIcon();
    }

    public int getStartAmount() {
        return startAmount;
    }

    public Talent setStartAmount(int startAmount) {
        this.startAmount = Numbers.clamp(startAmount, 1, 64);
        return this;
    }

    public Talent setTexture(String texture64) {
        this.setItem(Material.PLAYER_HEAD);
        this.texture64 = texture64;
        return this;
    }

    public Talent setItem(Material material) {
        this.material = material;
        return this;
    }

    public Talent setItem(Material material, Consumer<ItemBuilder> function) {
        setItem(material);
        itemFunction = function;

        return this;
    }

    public abstract Response execute(Player player);

    @Super
    @Nonnull
    public final Response execute0(Player player) {
        final Response precondition = preconditionTalent(player);

        if (precondition.isError()) {
            return precondition;
        }

        final Response response = execute(player);

        if (castMessage != null) {
            Chat.sendMessage(player, castMessage);
        }

        // If error, don't progress talent
        if (response == null || response == Response.ERROR) {
            return response == null ? Response.ERROR_DEFAULT : response;
        }

        postProcessTalent(player);

        return response;
    }

    // Performs post-process for a talent, such as storing stats, progressing achievements etc.
    public final void postProcessTalent(Player player) {
        // Progress ability usage
        final StatContainer stats = GamePlayer.getPlayer(player).getStats();
        final Talents enumTalent = Talents.fromTalent(this);

        if (stats != null && !Manager.current().isDebug()) {
            stats.addAbilityUsage(enumTalent);
        }

        // Progress achievement
        Achievements.USE_TALENTS.complete(player);
    }

    public final void startCd(Player player, int cooldown) {
        if (cooldown <= 0) {
            return;
        }

        // If a player has slowing aura, modify cooldown
        if (GamePlayer.getPlayer(player).hasEffect(GameEffectType.SLOWING_AURA)) {
            cooldown *= ((SlowingAuraEffect) GameEffectType.SLOWING_AURA.getGameEffect()).COOLDOWN_MODIFIER;
        }

        // Don't start CD if in debug
        if (Manager.current().isDebug()) {
            // TODO: 031, Mar 31, 2023 -> Create different debug states: -c for cooldown, -u for ult etc
            //return;
        }

        player.setCooldown(material, cooldown);
    }

    public void startCd(Player player) {
        startCd(player, cd);
    }

    public final void stopCd(@Nonnull Player player) {
        player.setCooldown(getItem().getType(), 0);
    }

    public final boolean hasCd(Player player) {
        return getCdTimeLeft(player) > 0L;
    }

    public final int getCdTimeLeft(Player player) {
        return player.getCooldown(this.material);
    }

    @Override
    public int getCooldown() {
        return cd;
    }

    @Override
    @ExecuteOrder(before = { "setPoint(int)" })
    public Talent setCooldown(int cd) {
        this.cd = cd;
        defaultPointGeneration();
        return this;
    }

    @Override
    @ExecuteOrder(before = { "setPoint(int)" })
    public Talent setCooldownSec(int cd) {
        return setCooldown(cd * 20);
    }

    @Override
    @Nonnull
    public String getName() {
        return name;
    }

    @Override
    public void setName(@Nonnull String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    @Nonnull
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "%s{%s}".formatted(getClass().getName(), name);
    }

    private String formatIfPossible(@Nonnull String toFormat, @Nullable Object... format) {
        if (format == null || format.length == 0) {
            return toFormat;
        }

        return toFormat.formatted(format);
    }

    public static int calcPointGeneration(int cd) {
        if (cd <= 0) {
            return 1;
        }

        return Numbers.clamp(cd / 200, 1, 100);
    }

    // Precondition player talent (and weapon) if it can be used.
    // Returns the error with a name of the blocking talent or OK.
    public static Response preconditionTalent(Player player) {
        final IGamePlayer gamePlayer = GamePlayer.getPlayer(player);

        for (GameEffectType type : GameEffectType.values()) {
            final GameEffect gameEffect = type.getGameEffect();

            if (gameEffect.isTalentBlocking() && gamePlayer.hasEffect(type)) {
                return Response.error(gameEffect.getName());
            }
        }

        if (Manager.current().isGameInProgress()) {
            final State state = Manager.current().getCurrentGame().getGameState();
            if (state != State.IN_GAME) {
                return Response.error("The game is not yet started!");
            }
        }

        return Response.OK;
    }

    public enum Type {
        /**
         * Normal talent.
         * Most heroes use this.
         */
        COMBAT,
        /**
         * Talent with multiple charges.
         */
        COMBAT_CHARGED,
        /**
         * Talent that requires input to execute.
         */
        COMBAT_INPUT,
        /**
         * Passive talent.
         */
        PASSIVE,
        /**
         * Ultimate.
         * Yes, ultimates are considered talents.
         */
        ULTIMATE
    }
}
