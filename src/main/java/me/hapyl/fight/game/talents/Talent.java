package me.hapyl.fight.game.talents;

import me.hapyl.fight.game.GameElement;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.util.Function;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.annotate.Super;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class Talent implements GameElement {

    public static final Talent NULL = null;

    private ItemStack item;
    private Material material;
    private String texture;

    private final String name;
    private final Type type;
    private final List<String> extraInfo; // adds extra information after cooldowns

    private String castMessage;
    private String description;
    private Function<ItemBuilder> itemFunction;
    private int cd;

    private boolean autoAdd;

    public Talent(String name) {
        this(name, "", Type.COMBAT);
    }

    public Talent(String name, String description) {
        this(name, description, Type.COMBAT);
    }

    public Talent(String name, String description, Type type) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.material = Material.BEDROCK;
        this.autoAdd = true;
        this.extraInfo = new ArrayList<>();
    }

    public Talent(String name, String description, Material material) {
        this(name, description);
        this.setItem(material);
    }

    public void addExtraInfo(String info) {
        addExtraInfo(info, new Object[] {});
    }

    public void addExtraInfo(String info, Object... objects) {
        extraInfo.add(ChatColor.GREEN + String.format(info, objects));
    }

    public void setCastMessage(String castMessage) {
        this.castMessage = castMessage;
    }

    public String getCastMessage() {
        return castMessage;
    }

    public void setInfo(String info) {
        this.description = info;
    }

    public void setInfo(String info, Object... replacements) {
        this.description = info.formatted(replacements);
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

    public ItemStack getItem() {
        if (this.item == null) {
            this.createItem();
        }
        return this.item;
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
    }

    public void onDeath(Player player) {

    }

    private void formatDescription() {
        replace("{name}", "&a%s", this.getName());
        if (this instanceof UltimateTalent ultimate) {
            replace("{duration}", "&b%ss", BukkitUtils.roundTick(ultimate.getDuration()));
        }
    }

    private void replace(String old, String newStr, Object... formatNew) {
        description = description.replace(old, newStr.formatted(formatNew) + "&7");
    }

    private void createItem() {
        formatDescription();

        final ItemBuilder builder = new ItemBuilder(this.material)
                .setName("&a" + this.name)
                .addLore("&8%s %s", Chat.capitalize(this.type), this.type == Type.ULTIMATE ? "" : "Talent")
                .addLore()
                .addSmartLore(this.description, 35);

        if (texture != null && this.material == Material.PLAYER_HEAD) {
            builder.setHeadTexture(texture);
        }

        if (itemFunction != null) {
            itemFunction.execute(builder);
        }

        if (!this.isAutoAdd()) {
            builder.addLore("");
            builder.addSmartLore("This talent is not given when the game starts, but there is a way to use it.", "&8&o", 35);
        }

        if (addExtraSpace()) {
            builder.addLore();
        }

        if (this.cd > 0) {
            builder.addLore("&aCooldown%s: &l%ss".formatted(
                    this instanceof ChargedTalent ? " between charges" : "",
                    BukkitUtils.roundTick(this.cd)
            ));
        }
        else if (this.cd <= -1) {
            builder.addLore("&aCooldown: &lDynamic");
        }

        if (!extraInfo.isEmpty()) {
            for (final String s : extraInfo) {
                builder.addLore(s);
            }
        }

        if (this instanceof ChargedTalent charge) {
            final int maxCharges = charge.getMaxCharges();
            final int rechargeTime = charge.getRechargeTime();

            builder.addLore("&aMax Charges: &l%s", maxCharges);
            if (rechargeTime >= 0) {
                builder.addLore("&aRecharge Time: &l%ss", BukkitUtils.roundTick(rechargeTime));
            }
        }

        else if (this instanceof UltimateTalent ult) {
            //if (this.cd == 0) {
            //	builder.addLore();
            //}

            builder.addLore("&aUltimate Cost: &l%s ※", ult.getCost());
            if (ult.getDuration() >= 0) {
                builder.addLore("&aUltimate Duration: &l%ss", BukkitUtils.roundTick(ult.getDuration()));
            }
            builder.glow();
            // ※
        }

        builder.hideFlags();
        this.item = builder.toItemStack();
    }

    private boolean addExtraSpace() {
        return (this.cd != 0) || this instanceof ChargedTalent || this instanceof UltimateTalent || !extraInfo.isEmpty();
    }

    public Talent setItem(String headTexture) {
        this.setItem(Material.PLAYER_HEAD);
        this.texture = headTexture;
        return this;
    }

    public Talent setItem(Material material) {
        this.material = material;
        return this;
    }

    public Talent setItem(Material material, Function<ItemBuilder> function) {
        this.setItem(material);
        this.itemFunction = function;
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
        return response == null ? Response.ERROR_DEFAULT : response;
    }

    public final void startCd(Player player, int customCd) {
        player.setCooldown(this.getItem().getType(), customCd);
    }

    public final void startCd(Player player) {
        if (this.cd <= 0) {
            return;
        }
        player.setCooldown(this.getItem().getType(), this.cd);
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
        return this;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public enum Type {
        PASSIVE,
        COMBAT,
        COMBAT_CHARGED,
        ULTIMATE
    }
}
