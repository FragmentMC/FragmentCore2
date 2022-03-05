package stanuwu.fragmentcore2.helpers;

import org.bukkit.Material;
import stanuwu.fragmentcore2.FragmentCore2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Helper {
    public static final List<Material> stackContents = Arrays.asList(
            Material.WHITE_CONCRETE_POWDER,
            Material.ORANGE_CONCRETE_POWDER,
            Material.MAGENTA_CONCRETE_POWDER,
            Material.LIGHT_BLUE_CONCRETE_POWDER,
            Material.YELLOW_CONCRETE_POWDER,
            Material.LIME_CONCRETE_POWDER,
            Material.PINK_CONCRETE_POWDER,
            Material.GRAY_CONCRETE_POWDER,
            Material.LIGHT_GRAY_CONCRETE_POWDER,
            Material.CYAN_CONCRETE_POWDER,
            Material.PURPLE_CONCRETE_POWDER,
            Material.BLUE_CONCRETE_POWDER,
            Material.BROWN_CONCRETE_POWDER,
            Material.GREEN_CONCRETE_POWDER,
            Material.RED_CONCRETE_POWDER,
            Material.BLACK_CONCRETE_POWDER,
            Material.WHITE_CONCRETE,
            Material.ORANGE_CONCRETE,
            Material.MAGENTA_CONCRETE,
            Material.LIGHT_BLUE_CONCRETE,
            Material.YELLOW_CONCRETE,
            Material.LIME_CONCRETE,
            Material.PINK_CONCRETE,
            Material.GRAY_CONCRETE,
            Material.LIGHT_GRAY_CONCRETE,
            Material.CYAN_CONCRETE,
            Material.PURPLE_CONCRETE,
            Material.BLUE_CONCRETE,
            Material.BROWN_CONCRETE,
            Material.GREEN_CONCRETE,
            Material.RED_CONCRETE,
            Material.BLACK_CONCRETE,
            Material.SAND,
            Material.RED_SAND,
            Material.GRAVEL,
            Material.ANVIL
    );

    public static final List<Material> buttonsSlow = Arrays.asList(
            Material.BIRCH_BUTTON,
            Material.ACACIA_BUTTON,
            Material.CRIMSON_BUTTON,
            Material.JUNGLE_BUTTON,
            Material.OAK_BUTTON,
            Material.DARK_OAK_BUTTON,
            Material.SPRUCE_BUTTON,
            Material.WARPED_BUTTON
    );

    public static List<Material> buttonsFast = Arrays.asList(
            Material.POLISHED_BLACKSTONE_BUTTON,
            Material.STONE_BUTTON
    );

    public static final List<Material> fallingBlocks = Arrays.asList(
            Material.WHITE_CONCRETE_POWDER,
            Material.ORANGE_CONCRETE_POWDER,
            Material.MAGENTA_CONCRETE_POWDER,
            Material.LIGHT_BLUE_CONCRETE_POWDER,
            Material.YELLOW_CONCRETE_POWDER,
            Material.LIME_CONCRETE_POWDER,
            Material.PINK_CONCRETE_POWDER,
            Material.GRAY_CONCRETE_POWDER,
            Material.LIGHT_GRAY_CONCRETE_POWDER,
            Material.CYAN_CONCRETE_POWDER,
            Material.PURPLE_CONCRETE_POWDER,
            Material.BLUE_CONCRETE_POWDER,
            Material.BROWN_CONCRETE_POWDER,
            Material.GREEN_CONCRETE_POWDER,
            Material.RED_CONCRETE_POWDER,
            Material.BLACK_CONCRETE_POWDER,
            Material.SAND,
            Material.RED_SAND,
            Material.GRAVEL,
            Material.ANVIL
    );

    public static final List<Material> extraTrackables = Arrays.asList(
        Material.TNT,
        Material.DISPENSER
    );

    public static final List<Material> waterproofBlocks = Arrays.asList(
        Material.REDSTONE_WIRE,
        Material.REDSTONE_TORCH,
        Material.REDSTONE_WALL_TORCH,
        Material.LEVER,
        Material.REPEATER,
        Material.COMPARATOR
    );


    public static String ParsePrefix(String msg) {
        return msg.replaceAll("%prefix%", getPrefix());
    }
    public static String WithPrefix(String msg) {
        return getPrefix() + msg;
    }

    public static String getPrefix() {
        return getConfigString("cosmetic", "message-prefix");
    }

    public static String getConfigString(String path, String name) {
        return FragmentCore2.config.getString("fragmentcore." + path + "." +name);
    }
}
