package me.hapyl.fight.fastaccess;

import me.hapyl.eterna.module.inventory.gui.SlotPattern;
import me.hapyl.eterna.module.inventory.gui.SmartComponent;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;

import javax.annotation.Nonnull;

public class FastAccessCategoryGUI extends StyledGUI implements IFastAccess {

    private final int slot;

    public FastAccessCategoryGUI(@Nonnull PlayerProfile profile, int slot) {
        super(profile.getPlayer(), IFastAccess.guiName(slot), Size.FOUR);

        this.slot = slot;

        update();
    }

    @Override
    public void onUpdate() {
        final SmartComponent component = newSmartComponent();

        for (Category category : Category.values()) {
            component.add(
                    category.getItem(player).asIcon(),
                    player -> new FastAccessGUI(CF.getProfile(player), slot, category)
            );
        }

        component.apply(this, SlotPattern.DEFAULT, 2);

        setDefaultSlots();
    }

    @Override
    public int slot() {
        return slot;
    }
}
