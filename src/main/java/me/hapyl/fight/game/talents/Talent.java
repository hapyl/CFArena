package me.hapyl.fight.game.talents;

import com.google.common.collect.Lists;
import me.hapyl.fight.Main;
import me.hapyl.fight.game.*;
import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.effect.storage.SlowingAuraEffect;
import me.hapyl.fight.game.stats.StatContainer;
import me.hapyl.fight.util.Nulls;
import me.hapyl.fight.util.Utils;
import me.hapyl.fight.util.displayfield.DisplayField;
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
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Base talent.
 */
public abstract class Talent extends NonnullItemStackCreatable implements GameElement, DisplayFieldProvider {

    public static final Talent NULL = null;
    public static int DYNAMIC = -1;
    private final String name;
    private final Type type;
    private final List<String> attributeDescription;

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
        if (cd <= 0) {
            point = 1;
            return;
        }

        point = Numbers.clamp(cd / 200, 1, 100);
    }

    public void setAltUsage(String altUsage) {
        this.altUsage = altUsage;
    }

    public int getDuration() {
        return duration;
    }

    public Talent setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public Talent setDurationSec(int duration) {
        return setDuration(duration * 20);
    }

    public int getPoint() {
        return point;
    }

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

        // Now using text block lore
        try {
            // fix % I fucking hate that java uses % as formatter fucking why
            description = description.replace("%", "%%");

            formatDescription();
            builderItem.addTextBlockLore(description);
        } catch (Exception e) {
            final StackTraceElement[] stackTrace = e.getStackTrace();

            builderItem.addLore("&4&lERROR FORMATTING, REPORT THIS WITH A CODE BELOW");
            builderItem.addLore("&e@" + getName());
            builderItem.addLore("&e" + stackTrace[0].toString());

            Main.getPlugin().getLogger().severe(description);
            e.printStackTrace();
        }

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
        return precondition(player, execute(player));
    }

    public final Response precondition(Player player, Response response) {
        final Response canUseRes = Utils.playerCanUseAbility(player);
        if (canUseRes.isError()) {
            return canUseRes;
        }

        if (castMessage != null) {
            Chat.sendMessage(player, castMessage);
        }

        // If error, don't progress talent
        if (response == null || response == Response.ERROR) {
            return response == null ? Response.ERROR_DEFAULT : response;
        }

        // Progress ability usage
        final StatContainer stats = GamePlayer.getPlayer(player).getStats();
        final Talents enumTalent = Talents.fromTalent(this);

        if (stats != null && !Manager.current().isDebug()) {
            stats.addAbilityUsage(enumTalent);
        }

        // Progress achievement
        Achievements.USE_TALENTS.complete(player);

        return response;
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

    public final void stopCd(Player player) {
        player.setCooldown(getItem().getType(), 0);
    }

    public final boolean hasCd(Player player) {
        return getCdTimeLeft(player) > 0L;
    }

    public final int getCdTimeLeft(Player player) {
        return player.getCooldown(this.material);
    }

    public int getCd() {
        return cd;
    }

    public Talent setCd(int cd) {
        this.cd = cd;
        return this;
    }

    public Talent setCdSec(int cd) {
        this.cd = cd * 20;
        defaultPointGeneration();
        return this;
    }

    public String getName() {
        return name;
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

    private void formatDescription() {
        // Format static
        for (StaticFormat format : StaticFormat.values()) {
            description = format.format(this);
        }

        // Format display fields
        for (Field field : getClass().getDeclaredFields()) {
            final DisplayField annotation = field.getAnnotation(DisplayField.class);

            if (annotation == null) {
                continue;
            }

            description = description.replace("{" + field.getName() + "}", DisplayFieldSerializer.formatField(field, this));
        }
    }

    private enum StaticFormat {
        NAME("{name}", t -> "&a" + t.getName()),
        DURATION("{duration}", t -> "&b" + BukkitUtils.roundTick(t.duration) + "s"),
        ;

        private final String target;
        private final Function<Talent, String> function;

        StaticFormat(String target, Function<Talent, String> function) {
            this.target = target;
            this.function = function;
        }

        public String format(Talent talent) {
            return talent.description.replace(target, function.apply(talent) + "&7");
        }
    }

    public enum Type {
        COMBAT,
        COMBAT_CHARGED,
        COMBAT_INPUT,
        PASSIVE,
        ULTIMATE
    }

}
