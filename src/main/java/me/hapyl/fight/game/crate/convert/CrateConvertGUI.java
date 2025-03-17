package me.hapyl.fight.game.crate.convert;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.crate.CrateLocation;
import me.hapyl.fight.game.crate.Crates;
import me.hapyl.fight.gui.CrateGUI;
import me.hapyl.fight.gui.styled.ReturnData;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledPageGUI;
import me.hapyl.fight.gui.styled.StyledTexture;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class CrateConvertGUI extends StyledPageGUI<CrateConverts> {

    private final ItemStack emptyItem = new ItemBuilder(Material.MINECART).setName("&cNothing to convert!").asIcon();
    private final CrateLocation location;

    public CrateConvertGUI(Player player, CrateLocation location) {
        super(player, "Convert Crates", Size.FOUR);
        this.location = location;

        final List<CrateConverts> convertsList = Lists.newArrayList();

        for (CrateConverts enumConvert : CrateConverts.values()) {
            final CrateConvert convert = enumConvert.getWrapped();

            if (convert.canConvert(player)) {
                convertsList.add(enumConvert);
            }
        }

        setEmptyContentsItem(emptyItem);
        setContents(convertsList);
        update();
    }

    @Nullable
    @Override
    public ReturnData getReturnData() {
        return ReturnData.of("Crates", fn -> new CrateGUI(player, location));
    }

    @Nonnull
    @Override
    public ItemStack asItem(Player player, CrateConverts content, int index, int page) {
        final CrateConvert convert = content.getWrapped();
        final Crates convertProduct = convert.getConvertProduct();
        final int convertProductAmount = convert.getConvertProductAmount();

        final ItemBuilder builder = StyledTexture.CRATE_CONVERT.toBuilder()
                                                               .setName(content.getName())
                                                               .addLore()
                                                               .addSmartLore(content.getDescription())
                                                               .addLore();

        if (convertProduct == null) {
            return builder.addLore("&cInvalid product!").asIcon();
        }

        builder.addLore("&cConsume:");
        convert.appendRequirementsScaledToItemBuilder(builder, 1);

        builder.addLore();
        builder.addLore("&aReceive:");
        builder.addLore(" &8+ %s".formatted(convertProduct.formatProduct((long) convertProductAmount)));
        builder.addLore();
        builder.addLore(Color.BUTTON + "Click to convert!");

        return builder.asIcon();
    }

    @Override
    public void onClick(@Nonnull Player player, @Nonnull CrateConverts content, int index, int page, @Nonnull ClickType clickType) {
        new CrateConvertOperationGUI(player, content, location);
    }

    @Override
    public void onUpdate() {
        setHeader(StyledTexture.CRATE_CONVERT.toBuilder()
                                             .setName("Crate Conversion")
                                             .addLore()
                                             .addSmartLore("Convert lower rarity crates into a higher ones!")
                                             .asIcon());
    }
}
