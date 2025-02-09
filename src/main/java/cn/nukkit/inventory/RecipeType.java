package cn.nukkit.inventory;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

public enum RecipeType {
    SHAPELESS(0),
    SHAPED(1),
    FURNACE(2),
    FURNACE_DATA(3),
    MULTI(4),
    SHULKER_BOX(5),
    SHAPELESS_CHEMISTRY(6),
    SHAPED_CHEMISTRY(7),
    SMITHING_TRANSFORM(8),
    @PowerNukkitOnly BLAST_FURNACE(2),
    @PowerNukkitOnly BLAST_FURNACE_DATA(3),
    @PowerNukkitOnly SMOKER(2),
    @PowerNukkitOnly SMOKER_DATA(3),
    @PowerNukkitOnly CAMPFIRE(2),
    @PowerNukkitOnly CAMPFIRE_DATA(3),
    @PowerNukkitOnly STONECUTTER(0),
    @PowerNukkitOnly CARTOGRAPHY(0),
    @PowerNukkitOnly REPAIR(-1),
    @PowerNukkitOnly @Since("1.4.0.0-PN") @Deprecated(since = "1.19.63-r2")
    @DeprecationDetails(since = "1.19.63-r2", reason = "Use SMITHING_TRANSFORM instead", replaceWith = "SMITHING_TRANSFORM")
    SMITHING(8),
    @PowerNukkitXOnly @Since("1.19.50-r3") MOD_PROCESS(0) // For mods
    ;

    @PowerNukkitOnly public final int networkType;

    RecipeType(int networkType) {
        this.networkType = networkType;
    }
}
