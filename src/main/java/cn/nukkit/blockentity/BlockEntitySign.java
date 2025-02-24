package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSignPost;
import cn.nukkit.event.block.SignChangeEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.ByteTag;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.TextFormat;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockEntitySign extends BlockEntitySpawnable {

    private String[] text;

    public BlockEntitySign(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Since("1.19.60-r1")
    @Override
    public void loadNBT() {
        super.loadNBT();
        text = new String[4];

        if (!namedTag.contains("Text")) {

            for (int i = 1; i <= 4; i++) {
                String key = "Text" + i;

                if (namedTag.contains(key)) {
                    String line = namedTag.getString(key);

                    this.text[i - 1] = line;

                    this.namedTag.remove(key);
                }
            }
        } else {
            String[] lines = namedTag.getString("Text").split("\n", 4);

            for (int i = 0; i < text.length; i++) {
                if (i < lines.length)
                    text[i] = lines[i];
                else
                    text[i] = "";
            }
        }

        // Check old text to sanitize
        if (text != null) {
            sanitizeText(text);
        }

        if (!this.namedTag.contains("SignTextColor") || !(this.namedTag.get("SignTextColor") instanceof IntTag)) {
            this.setColor(DyeColor.BLACK.getSignColor());
        }
        if (!this.namedTag.contains("IgnoreLighting") || !(this.namedTag.get("IgnoreLighting") instanceof ByteTag)) {
            this.setGlowing(false);
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putString("Text", String.join("\n", text));
        this.namedTag.remove("Creator");
    }

    @Override
    public boolean isBlockEntityValid() {
        Block block = getBlock();
        return block instanceof BlockSignPost;
    }

    public boolean setText(String... lines) {
        for (int i = 0; i < 4; i++) {
            if (i < lines.length)
                text[i] = lines[i];
            else
                text[i] = "";
        }

        this.namedTag.putString("Text", String.join("\n", text));
        this.spawnToAll();

        if (this.chunk != null) {
            setDirty();
        }

        return true;
    }

    public String[] getText() {
        return text;
    }

    public BlockColor getColor() {
        return new BlockColor(this.namedTag.getInt("SignTextColor"), true);
    }

    public void setColor(BlockColor color) {
        this.namedTag.putInt("SignTextColor", color.getARGB());
    }

    public boolean isGlowing() {
        return this.namedTag.getBoolean("IgnoreLighting");
    }

    public void setGlowing(boolean glowing) {
        this.namedTag.putBoolean("IgnoreLighting", glowing);
    }

    @Override
    public boolean updateCompoundTag(CompoundTag nbt, Player player) {
        if (!nbt.getString("id").equals(BlockEntity.SIGN)) {
            return false;
        }
        String[] lines = new String[4];
        Arrays.fill(lines, "");
        String[] splitLines = nbt.getString("Text").split("\n", 4);
        System.arraycopy(splitLines, 0, lines, 0, splitLines.length);

        sanitizeText(lines);

        SignChangeEvent signChangeEvent = new SignChangeEvent(this.getBlock(), player, lines);

        if (!this.namedTag.contains("Creator") || !Objects.equals(player.getUniqueId().toString(), this.namedTag.getString("Creator"))) {
            signChangeEvent.setCancelled();
        }

        if (player.getRemoveFormat()) {
            for (int i = 0; i < lines.length; i++) {
                lines[i] = TextFormat.clean(lines[i]);
            }
        }

        this.server.getPluginManager().callEvent(signChangeEvent);

        if (!signChangeEvent.isCancelled()) {
            this.setText(signChangeEvent.getLines());
            return true;
        }

        return false;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return new CompoundTag()
                .putString("id", BlockEntity.SIGN)
                .putString("Text", this.namedTag.getString("Text"))
                .putInt("SignTextColor", this.getColor().getARGB())
                .putBoolean("IgnoreLighting", this.isGlowing())
                .putBoolean("TextIgnoreLegacyBugResolved", true)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);
    }

    private static void sanitizeText(String[] lines) {
        for (int i = 0; i < lines.length; i++) {
            // Don't allow excessive text per line.
            if (lines[i] != null) {
                lines[i] = lines[i].substring(0, Math.min(255, lines[i].length()));
            }
        }
    }
}
