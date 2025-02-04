package me.hapyl.fight.command;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.util.ArgumentList;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.eterna.module.util.KeyedToString;
import me.hapyl.fight.Notifier;
import me.hapyl.fight.database.rank.PlayerRank;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.profile.PlayerTextures;

import javax.annotation.Nonnull;
import java.net.URL;

public class DumpColorCommand extends CFCommand {

    public DumpColorCommand(@Nonnull String name) {
        super(name, PlayerRank.ADMIN);
    }

    @Override
    protected void execute(@Nonnull Player player, @Nonnull ArgumentList args, @Nonnull PlayerRank rank) {
        final ItemStack item = player.getInventory().getItemInMainHand();
        final ItemMeta meta = item.getItemMeta();

        final StringBuilder command = new StringBuilder();
        final net.kyori.adventure.text.TextComponent.Builder component = Component.text();

        final Material itemType = item.getType();

        // Player head special case
        if (meta instanceof SkullMeta skullMeta) {
            final com.destroystokyo.paper.profile.PlayerProfile profile = skullMeta.getPlayerProfile();

            if (profile == null) {
                Notifier.error(player, "There is no texture applied to this player's head!");
                return;
            }

            final PlayerTextures textures = profile.getTextures();
            final URL skin = textures.getSkin();

            if (skin == null) {
                Notifier.error(player, "There isn't a skin somehow!");
                return;
            }

            final String urlString = skin.toString().replace("https://textures.minecraft.net/texture/", "");

            command.append(urlString);
            component.append(
                    Component.text("Skin Texture: ").color(NamedTextColor.DARK_GREEN),
                    Component.text(urlString).color(NamedTextColor.GREEN)
            );
        }
        // Else handle color and/or trim
        else {
            org.bukkit.Color color;
            ArmorTrim trim;

            if (meta instanceof LeatherArmorMeta colorMeta) {
                color = colorMeta.getColor();
                final int red = color.getRed();
                final int green = color.getGreen();
                final int blue = color.getBlue();

                command.append("%s, %s, %s".formatted(red, green, blue));
                component.append(
                        Component.text("Color: ").color(NamedTextColor.GREEN),
                        Component.text("⬛⬛⬛").color(TextColor.color(color.asRGB()))
                );
            }
            // if not leather armor, use material
            else {
                final String materialName = itemType.name();

                command.append("Material.%s".formatted(materialName));
                component.append(
                        Component.text("Material: ").color(NamedTextColor.DARK_GREEN),
                        Component.text(Chat.capitalize(materialName)).color(NamedTextColor.GREEN)
                );
            }

            if (meta instanceof ArmorMeta armorMeta) {
                trim = armorMeta.getTrim();

                if (trim != null) {
                    final TrimPattern pattern = trim.getPattern();
                    final TrimMaterial material = trim.getMaterial();

                    final String patternKey = BukkitUtils.getKey(pattern).getKey().toUpperCase();
                    final String materialKey = BukkitUtils.getKey(material).getKey().toUpperCase();

                    // Add commas for color/material
                    command.append(", ");
                    component.append(Component.text(", ").color(NamedTextColor.GRAY));

                    // Append trim
                    command.append("TrimPattern.%s, TrimMaterial.%s".formatted(patternKey, materialKey));
                    component.append(
                            Component.text("Trim: ").color(NamedTextColor.AQUA),
                            Component.text("%s, %s".formatted(
                                             KeyedToString.of(trim.getPattern())
                                                          .stripMinecraft()
                                                          .capitalize()
                                                          .toString(),
                                             KeyedToString.of(trim.getMaterial())
                                                          .stripMinecraft()
                                                          .capitalize()
                                                          .toString()
                                     ))
                                     .color(NamedTextColor.DARK_AQUA)
                    );
                }
            }
        }

        component.append(Component.text(" "))
                 .append(Component.text("COPY")
                                  .color(NamedTextColor.GOLD)
                                  .decorate(TextDecoration.BOLD, TextDecoration.UNDERLINED)
                                  .hoverEvent(Component.text("Click to copy!").color(NamedTextColor.YELLOW))
                                  .clickEvent(ClickEvent.suggestCommand(command.toString()))
                 );

        player.sendMessage(component);
    }
}
