package me.hapyl.fight.game.talents;

import com.google.common.collect.Lists;
import me.hapyl.fight.game.*;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.effect.storage.SlowingAuraEffect;
import me.hapyl.fight.util.Function;
import me.hapyl.fight.util.Nulls;
import me.hapyl.fight.util.Utils;
import me.hapyl.fight.util.displayfield.DisplayFieldProvider;
import me.hapyl.fight.util.displayfield.DisplayFieldSerializer;
import me.hapyl.spigotutils.module.annotate.Super;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.math.Numbers;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class Talent extends NonnullItemStackCreatable implements GameElement, DisplayFieldProvider {

    public static final Talent NULL = null;

    private ItemStack itemStats;
    private Material material;
    private String texture;
    private String texture64;

    private final String name;
    private final Type type;
    private final List<String> description;
    private final List<String> attributeDescription;

    private String castMessage;
    private String altUsage;
    private Function<ItemBuilder> itemFunction;
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
        this.description = Lists.newArrayList();
        this.attributeDescription = Lists.newArrayList();

        if (!description.isEmpty()) {
            addDescription(description);
        }

        this.type = type;
        this.material = Material.BEDROCK;
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

    @Deprecated(forRemoval = true)
    public void addExtraInfo(String info) {
        addExtraInfo(info, new Object[] {});
    }

    @Deprecated(forRemoval = true)
    public void addExtraInfo(String info, Object... objects) {
        description.add(ChatColor.GREEN + String.format(info, objects));
    }

    public void setCastMessage(String castMessage) {
        this.castMessage = castMessage;
    }

    public String getCastMessage() {
        return castMessage;
    }

    public void addDescription() {
        addDescription("");
    }

    public void addAttributeDescription(String key, Object value) {
        this.attributeDescription.add(DisplayFieldSerializer.DEFAULT_FORMATTER.format(key, String.valueOf(value)));
    }

    public void addDescription(String description) {
        this.description.add(description);
    }

    public void addDescription(String description, Object... format) {
        this.description.add(description.formatted(format));
    }

    // This removes existing description!
    public void setDescription(String description, Object... format) {
        this.description.clear();
        this.description.add(description.formatted(format));
    }

    public void setAutoAdd(boolean autoAdd) {
        this.autoAdd = autoAdd;
    }

    public boolean isAutoAdd() {
        return autoAdd;
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

        // Add head texture if item is a player head
        if (material == Material.PLAYER_HEAD) {
            if (texture != null) {
                builderItem.setHeadTexture(texture);
            }
            else if (texture64 != null) {
                builderItem.setHeadTextureUrl(texture64);
            }
        }

        // Execute functions is present
        Nulls.runIfNotNull(itemFunction, function -> function.execute(builderItem));

        // Description and Attributes
        for (String string : description) {
            if (string.isEmpty() || string.isBlank()) {
                builderItem.addLore();
                continue;
            }

            final List<String> strings = ItemBuilder.splitString(null, string, 35);

            for (int i = 0; i < strings.size(); i++) {
                final String lore = strings.get(i);
                final String lastColor = i > 0 ? getLastColor(strings.get(i - 1)) : "";
                final String formatted = formatDescription(lore);

                builderItem.addLore(lastColor + formatted);
            }
        }

        // Is ability has alternative usage, tell how to use it
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

        // Cooldown first
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

    private String getLastColor(String input) {
        final int lastCharIndex = input.lastIndexOf(ChatColor.COLOR_CHAR);

        if (lastCharIndex == -1 || (lastCharIndex + 1 > input.length())) {
            return "";
        }

        final char colorChar = input.charAt(lastCharIndex + 1);
        return "&" + colorChar;
    }

    private String formatDescription(String description) {
        return description
                .replace("{name}", "&a" + getName() + "&7")
                .replace("{duration}", "&b" + BukkitUtils.roundTick(duration) + "s&7");
    }

    public Talent setItem(String headTexture) {
        this.setItem(Material.PLAYER_HEAD);
        this.texture = headTexture;
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

    public Talent setItem(Material material, Function<ItemBuilder> function) {
        setItem(material);
        itemFunction = function;

        return this;
    }

    public abstract Response execute(Player player);

    @Super
    @Nonnull
    public final Response execute0(Player player) {
        final Response canUseRes = Utils.playerCanUseAbility(player);
        if (canUseRes.isError()) {
            return canUseRes;
        }

        if (castMessage != null) {
            Chat.sendMessage(player, castMessage);
        }

        final Response response = execute(player);

        // Progress ability usage
        final StatContainer stats = GamePlayer.getPlayer(player).getStats();

        if ((response != null && !response.isError()) && stats != null) {
            stats.addAbilityUsage(Talents.fromTalent(this));
        }

        return response == null ? Response.ERROR_DEFAULT : response;
    }

    public final void startCd(Player player, int cooldown) {
        if (cooldown <= 0) {
            return;
        }

        // If player has slowing aura, modify cooldown
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

    public final void startCd(Player player) {
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

    public static int DYNAMIC = -1;

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

    public String getDescription() {
        if (description.isEmpty()) {
            return "";
        }

        return description.get(0);
    }

    public enum Type {
        PASSIVE,
        COMBAT,
        COMBAT_CHARGED,
        ULTIMATE
    }

    public Talent getHandle() {
        return this;
    }

}
