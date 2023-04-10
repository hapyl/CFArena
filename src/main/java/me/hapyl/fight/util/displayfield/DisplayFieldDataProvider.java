package me.hapyl.fight.util.displayfield;

import javax.annotation.Nonnull;
import java.util.List;

public interface DisplayFieldDataProvider {

    @Nonnull
    List<DisplayFieldData> getDisplayFieldData();

}
