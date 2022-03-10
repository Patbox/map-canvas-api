package eu.pb4.mapcanvas.api.core;

import net.minecraft.block.MapColor;

import java.util.Objects;

/**
 * All colors supported by Minecraft/Map Canvas API
 */
@SuppressWarnings("unused")
public final class CanvasColor {
    protected static final CanvasColor[] BY_RENDER_COLOR = new CanvasColor[256];
    public static final CanvasColor CLEAR = new CanvasColor("clear", MapColor.CLEAR, MapColor.Brightness.LOW);
    public static final CanvasColor CLEAR_FORCE = new CanvasColor("clear_force", MapColor.CLEAR, MapColor.Brightness.NORMAL);
    public static final CanvasColor PALE_GREEN_LOWEST = new CanvasColor("pale_green_lowest", MapColor.PALE_GREEN, MapColor.Brightness.LOWEST);
    public static final CanvasColor PALE_GREEN_LOW = new CanvasColor("pale_green_low", MapColor.PALE_GREEN, MapColor.Brightness.LOW);
    public static final CanvasColor PALE_GREEN_NORMAL = new CanvasColor("pale_green_normal", MapColor.PALE_GREEN, MapColor.Brightness.NORMAL);
    public static final CanvasColor PALE_GREEN_HIGH = new CanvasColor("pale_green_high", MapColor.PALE_GREEN, MapColor.Brightness.HIGH);
    public static final CanvasColor PALE_YELLOW_LOWEST = new CanvasColor("pale_yellow_lowest", MapColor.PALE_YELLOW, MapColor.Brightness.LOWEST);
    public static final CanvasColor PALE_YELLOW_LOW = new CanvasColor("pale_yellow_low", MapColor.PALE_YELLOW, MapColor.Brightness.LOW);
    public static final CanvasColor PALE_YELLOW_NORMAL = new CanvasColor("pale_yellow_normal", MapColor.PALE_YELLOW, MapColor.Brightness.NORMAL);
    public static final CanvasColor PALE_YELLOW_HIGH = new CanvasColor("pale_yellow_high", MapColor.PALE_YELLOW, MapColor.Brightness.HIGH);
    public static final CanvasColor WHITE_GRAY_LOWEST = new CanvasColor("white_gray_lowest", MapColor.WHITE_GRAY, MapColor.Brightness.LOWEST);
    public static final CanvasColor WHITE_GRAY_LOW = new CanvasColor("white_gray_low", MapColor.WHITE_GRAY, MapColor.Brightness.LOW);
    public static final CanvasColor WHITE_GRAY_NORMAL = new CanvasColor("white_gray_normal", MapColor.WHITE_GRAY, MapColor.Brightness.NORMAL);
    public static final CanvasColor WHITE_GRAY_HIGH = new CanvasColor("white_gray_high", MapColor.WHITE_GRAY, MapColor.Brightness.HIGH);
    public static final CanvasColor BRIGHT_RED_LOWEST = new CanvasColor("bright_red_lowest", MapColor.BRIGHT_RED, MapColor.Brightness.LOWEST);
    public static final CanvasColor BRIGHT_RED_LOW = new CanvasColor("bright_red_low", MapColor.BRIGHT_RED, MapColor.Brightness.LOW);
    public static final CanvasColor BRIGHT_RED_NORMAL = new CanvasColor("bright_red_normal", MapColor.BRIGHT_RED, MapColor.Brightness.NORMAL);
    public static final CanvasColor BRIGHT_RED_HIGH = new CanvasColor("bright_red_high", MapColor.BRIGHT_RED, MapColor.Brightness.HIGH);
    public static final CanvasColor PALE_PURPLE_LOWEST = new CanvasColor("pale_purple_lowest", MapColor.PALE_PURPLE, MapColor.Brightness.LOWEST);
    public static final CanvasColor PALE_PURPLE_LOW = new CanvasColor("pale_purple_low", MapColor.PALE_PURPLE, MapColor.Brightness.LOW);
    public static final CanvasColor PALE_PURPLE_NORMAL = new CanvasColor("pale_purple_normal", MapColor.PALE_PURPLE, MapColor.Brightness.NORMAL);
    public static final CanvasColor PALE_PURPLE_HIGH = new CanvasColor("pale_purple_high", MapColor.PALE_PURPLE, MapColor.Brightness.HIGH);
    public static final CanvasColor IRON_GRAY_LOWEST = new CanvasColor("iron_gray_lowest", MapColor.IRON_GRAY, MapColor.Brightness.LOWEST);
    public static final CanvasColor IRON_GRAY_LOW = new CanvasColor("iron_gray_low", MapColor.IRON_GRAY, MapColor.Brightness.LOW);
    public static final CanvasColor IRON_GRAY_NORMAL = new CanvasColor("iron_gray_normal", MapColor.IRON_GRAY, MapColor.Brightness.NORMAL);
    public static final CanvasColor IRON_GRAY_HIGH = new CanvasColor("iron_gray_high", MapColor.IRON_GRAY, MapColor.Brightness.HIGH);
    public static final CanvasColor DARK_GREEN_LOWEST = new CanvasColor("dark_green_lowest", MapColor.DARK_GREEN, MapColor.Brightness.LOWEST);
    public static final CanvasColor DARK_GREEN_LOW = new CanvasColor("dark_green_low", MapColor.DARK_GREEN, MapColor.Brightness.LOW);
    public static final CanvasColor DARK_GREEN_NORMAL = new CanvasColor("dark_green_normal", MapColor.DARK_GREEN, MapColor.Brightness.NORMAL);
    public static final CanvasColor DARK_GREEN_HIGH = new CanvasColor("dark_green_high", MapColor.DARK_GREEN, MapColor.Brightness.HIGH);
    public static final CanvasColor WHITE_LOWEST = new CanvasColor("white_lowest", MapColor.WHITE, MapColor.Brightness.LOWEST);
    public static final CanvasColor WHITE_LOW = new CanvasColor("white_low", MapColor.WHITE, MapColor.Brightness.LOW);
    public static final CanvasColor WHITE_NORMAL = new CanvasColor("white_normal", MapColor.WHITE, MapColor.Brightness.NORMAL);
    public static final CanvasColor WHITE_HIGH = new CanvasColor("white_high", MapColor.WHITE, MapColor.Brightness.HIGH);
    public static final CanvasColor LIGHT_BLUE_GRAY_LOWEST = new CanvasColor("light_blue_gray_lowest", MapColor.LIGHT_BLUE_GRAY, MapColor.Brightness.LOWEST);
    public static final CanvasColor LIGHT_BLUE_GRAY_LOW = new CanvasColor("light_blue_gray_low", MapColor.LIGHT_BLUE_GRAY, MapColor.Brightness.LOW);
    public static final CanvasColor LIGHT_BLUE_GRAY_NORMAL = new CanvasColor("light_blue_gray_normal", MapColor.LIGHT_BLUE_GRAY, MapColor.Brightness.NORMAL);
    public static final CanvasColor LIGHT_BLUE_GRAY_HIGH = new CanvasColor("light_blue_gray_high", MapColor.LIGHT_BLUE_GRAY, MapColor.Brightness.HIGH);
    public static final CanvasColor DIRT_BROWN_LOWEST = new CanvasColor("dirt_brown_lowest", MapColor.DIRT_BROWN, MapColor.Brightness.LOWEST);
    public static final CanvasColor DIRT_BROWN_LOW = new CanvasColor("dirt_brown_low", MapColor.DIRT_BROWN, MapColor.Brightness.LOW);
    public static final CanvasColor DIRT_BROWN_NORMAL = new CanvasColor("dirt_brown_normal", MapColor.DIRT_BROWN, MapColor.Brightness.NORMAL);
    public static final CanvasColor DIRT_BROWN_HIGH = new CanvasColor("dirt_brown_high", MapColor.DIRT_BROWN, MapColor.Brightness.HIGH);
    public static final CanvasColor STONE_GRAY_LOWEST = new CanvasColor("stone_gray_lowest", MapColor.STONE_GRAY, MapColor.Brightness.LOWEST);
    public static final CanvasColor STONE_GRAY_LOW = new CanvasColor("stone_gray_low", MapColor.STONE_GRAY, MapColor.Brightness.LOW);
    public static final CanvasColor STONE_GRAY_NORMAL = new CanvasColor("stone_gray_normal", MapColor.STONE_GRAY, MapColor.Brightness.NORMAL);
    public static final CanvasColor STONE_GRAY_HIGH = new CanvasColor("stone_gray_high", MapColor.STONE_GRAY, MapColor.Brightness.HIGH);
    public static final CanvasColor WATER_BLUE_LOWEST = new CanvasColor("water_blue_lowest", MapColor.WATER_BLUE, MapColor.Brightness.LOWEST);
    public static final CanvasColor WATER_BLUE_LOW = new CanvasColor("water_blue_low", MapColor.WATER_BLUE, MapColor.Brightness.LOW);
    public static final CanvasColor WATER_BLUE_NORMAL = new CanvasColor("water_blue_normal", MapColor.WATER_BLUE, MapColor.Brightness.NORMAL);
    public static final CanvasColor WATER_BLUE_HIGH = new CanvasColor("water_blue_high", MapColor.WATER_BLUE, MapColor.Brightness.HIGH);
    public static final CanvasColor OAK_TAN_LOWEST = new CanvasColor("oak_tan_lowest", MapColor.OAK_TAN, MapColor.Brightness.LOWEST);
    public static final CanvasColor OAK_TAN_LOW = new CanvasColor("oak_tan_low", MapColor.OAK_TAN, MapColor.Brightness.LOW);
    public static final CanvasColor OAK_TAN_NORMAL = new CanvasColor("oak_tan_normal", MapColor.OAK_TAN, MapColor.Brightness.NORMAL);
    public static final CanvasColor OAK_TAN_HIGH = new CanvasColor("oak_tan_high", MapColor.OAK_TAN, MapColor.Brightness.HIGH);
    public static final CanvasColor OFF_WHITE_LOWEST = new CanvasColor("off_white_lowest", MapColor.OFF_WHITE, MapColor.Brightness.LOWEST);
    public static final CanvasColor OFF_WHITE_LOW = new CanvasColor("off_white_low", MapColor.OFF_WHITE, MapColor.Brightness.LOW);
    public static final CanvasColor OFF_WHITE_NORMAL = new CanvasColor("off_white_normal", MapColor.OFF_WHITE, MapColor.Brightness.NORMAL);
    public static final CanvasColor OFF_WHITE_HIGH = new CanvasColor("off_white_high", MapColor.OFF_WHITE, MapColor.Brightness.HIGH);
    public static final CanvasColor ORANGE_LOWEST = new CanvasColor("orange_lowest", MapColor.ORANGE, MapColor.Brightness.LOWEST);
    public static final CanvasColor ORANGE_LOW = new CanvasColor("orange_low", MapColor.ORANGE, MapColor.Brightness.LOW);
    public static final CanvasColor ORANGE_NORMAL = new CanvasColor("orange_normal", MapColor.ORANGE, MapColor.Brightness.NORMAL);
    public static final CanvasColor ORANGE_HIGH = new CanvasColor("orange_high", MapColor.ORANGE, MapColor.Brightness.HIGH);
    public static final CanvasColor MAGENTA_LOWEST = new CanvasColor("magenta_lowest", MapColor.MAGENTA, MapColor.Brightness.LOWEST);
    public static final CanvasColor MAGENTA_LOW = new CanvasColor("magenta_low", MapColor.MAGENTA, MapColor.Brightness.LOW);
    public static final CanvasColor MAGENTA_NORMAL = new CanvasColor("magenta_normal", MapColor.MAGENTA, MapColor.Brightness.NORMAL);
    public static final CanvasColor MAGENTA_HIGH = new CanvasColor("magenta_high", MapColor.MAGENTA, MapColor.Brightness.HIGH);
    public static final CanvasColor LIGHT_BLUE_LOWEST = new CanvasColor("light_blue_lowest", MapColor.LIGHT_BLUE, MapColor.Brightness.LOWEST);
    public static final CanvasColor LIGHT_BLUE_LOW = new CanvasColor("light_blue_low", MapColor.LIGHT_BLUE, MapColor.Brightness.LOW);
    public static final CanvasColor LIGHT_BLUE_NORMAL = new CanvasColor("light_blue_normal", MapColor.LIGHT_BLUE, MapColor.Brightness.NORMAL);
    public static final CanvasColor LIGHT_BLUE_HIGH = new CanvasColor("light_blue_high", MapColor.LIGHT_BLUE, MapColor.Brightness.HIGH);
    public static final CanvasColor YELLOW_LOWEST = new CanvasColor("yellow_lowest", MapColor.YELLOW, MapColor.Brightness.LOWEST);
    public static final CanvasColor YELLOW_LOW = new CanvasColor("yellow_low", MapColor.YELLOW, MapColor.Brightness.LOW);
    public static final CanvasColor YELLOW_NORMAL = new CanvasColor("yellow_normal", MapColor.YELLOW, MapColor.Brightness.NORMAL);
    public static final CanvasColor YELLOW_HIGH = new CanvasColor("yellow_high", MapColor.YELLOW, MapColor.Brightness.HIGH);
    public static final CanvasColor LIME_LOWEST = new CanvasColor("lime_lowest", MapColor.LIME, MapColor.Brightness.LOWEST);
    public static final CanvasColor LIME_LOW = new CanvasColor("lime_low", MapColor.LIME, MapColor.Brightness.LOW);
    public static final CanvasColor LIME_NORMAL = new CanvasColor("lime_normal", MapColor.LIME, MapColor.Brightness.NORMAL);
    public static final CanvasColor LIME_HIGH = new CanvasColor("lime_high", MapColor.LIME, MapColor.Brightness.HIGH);
    public static final CanvasColor PINK_LOWEST = new CanvasColor("pink_lowest", MapColor.PINK, MapColor.Brightness.LOWEST);
    public static final CanvasColor PINK_LOW = new CanvasColor("pink_low", MapColor.PINK, MapColor.Brightness.LOW);
    public static final CanvasColor PINK_NORMAL = new CanvasColor("pink_normal", MapColor.PINK, MapColor.Brightness.NORMAL);
    public static final CanvasColor PINK_HIGH = new CanvasColor("pink_high", MapColor.PINK, MapColor.Brightness.HIGH);
    public static final CanvasColor GRAY_LOWEST = new CanvasColor("gray_lowest", MapColor.GRAY, MapColor.Brightness.LOWEST);
    public static final CanvasColor GRAY_LOW = new CanvasColor("gray_low", MapColor.GRAY, MapColor.Brightness.LOW);
    public static final CanvasColor GRAY_NORMAL = new CanvasColor("gray_normal", MapColor.GRAY, MapColor.Brightness.NORMAL);
    public static final CanvasColor GRAY_HIGH = new CanvasColor("gray_high", MapColor.GRAY, MapColor.Brightness.HIGH);
    public static final CanvasColor LIGHT_GRAY_LOWEST = new CanvasColor("light_gray_lowest", MapColor.LIGHT_GRAY, MapColor.Brightness.LOWEST);
    public static final CanvasColor LIGHT_GRAY_LOW = new CanvasColor("light_gray_low", MapColor.LIGHT_GRAY, MapColor.Brightness.LOW);
    public static final CanvasColor LIGHT_GRAY_NORMAL = new CanvasColor("light_gray_normal", MapColor.LIGHT_GRAY, MapColor.Brightness.NORMAL);
    public static final CanvasColor LIGHT_GRAY_HIGH = new CanvasColor("light_gray_high", MapColor.LIGHT_GRAY, MapColor.Brightness.HIGH);
    public static final CanvasColor CYAN_LOWEST = new CanvasColor("cyan_lowest", MapColor.CYAN, MapColor.Brightness.LOWEST);
    public static final CanvasColor CYAN_LOW = new CanvasColor("cyan_low", MapColor.CYAN, MapColor.Brightness.LOW);
    public static final CanvasColor CYAN_NORMAL = new CanvasColor("cyan_normal", MapColor.CYAN, MapColor.Brightness.NORMAL);
    public static final CanvasColor CYAN_HIGH = new CanvasColor("cyan_high", MapColor.CYAN, MapColor.Brightness.HIGH);
    public static final CanvasColor PURPLE_LOWEST = new CanvasColor("purple_lowest", MapColor.PURPLE, MapColor.Brightness.LOWEST);
    public static final CanvasColor PURPLE_LOW = new CanvasColor("purple_low", MapColor.PURPLE, MapColor.Brightness.LOW);
    public static final CanvasColor PURPLE_NORMAL = new CanvasColor("purple_normal", MapColor.PURPLE, MapColor.Brightness.NORMAL);
    public static final CanvasColor PURPLE_HIGH = new CanvasColor("purple_high", MapColor.PURPLE, MapColor.Brightness.HIGH);
    public static final CanvasColor BLUE_LOWEST = new CanvasColor("blue_lowest", MapColor.BLUE, MapColor.Brightness.LOWEST);
    public static final CanvasColor BLUE_LOW = new CanvasColor("blue_low", MapColor.BLUE, MapColor.Brightness.LOW);
    public static final CanvasColor BLUE_NORMAL = new CanvasColor("blue_normal", MapColor.BLUE, MapColor.Brightness.NORMAL);
    public static final CanvasColor BLUE_HIGH = new CanvasColor("blue_high", MapColor.BLUE, MapColor.Brightness.HIGH);
    public static final CanvasColor BROWN_LOWEST = new CanvasColor("brown_lowest", MapColor.BROWN, MapColor.Brightness.LOWEST);
    public static final CanvasColor BROWN_LOW = new CanvasColor("brown_low", MapColor.BROWN, MapColor.Brightness.LOW);
    public static final CanvasColor BROWN_NORMAL = new CanvasColor("brown_normal", MapColor.BROWN, MapColor.Brightness.NORMAL);
    public static final CanvasColor BROWN_HIGH = new CanvasColor("brown_high", MapColor.BROWN, MapColor.Brightness.HIGH);
    public static final CanvasColor GREEN_LOWEST = new CanvasColor("green_lowest", MapColor.GREEN, MapColor.Brightness.LOWEST);
    public static final CanvasColor GREEN_LOW = new CanvasColor("green_low", MapColor.GREEN, MapColor.Brightness.LOW);
    public static final CanvasColor GREEN_NORMAL = new CanvasColor("green_normal", MapColor.GREEN, MapColor.Brightness.NORMAL);
    public static final CanvasColor GREEN_HIGH = new CanvasColor("green_high", MapColor.GREEN, MapColor.Brightness.HIGH);
    public static final CanvasColor RED_LOWEST = new CanvasColor("red_lowest", MapColor.RED, MapColor.Brightness.LOWEST);
    public static final CanvasColor RED_LOW = new CanvasColor("red_low", MapColor.RED, MapColor.Brightness.LOW);
    public static final CanvasColor RED_NORMAL = new CanvasColor("red_normal", MapColor.RED, MapColor.Brightness.NORMAL);
    public static final CanvasColor RED_HIGH = new CanvasColor("red_high", MapColor.RED, MapColor.Brightness.HIGH);
    public static final CanvasColor BLACK_LOWEST = new CanvasColor("black_lowest", MapColor.BLACK, MapColor.Brightness.LOWEST);
    public static final CanvasColor BLACK_LOW = new CanvasColor("black_low", MapColor.BLACK, MapColor.Brightness.LOW);
    public static final CanvasColor BLACK_NORMAL = new CanvasColor("black_normal", MapColor.BLACK, MapColor.Brightness.NORMAL);
    public static final CanvasColor BLACK_HIGH = new CanvasColor("black_high", MapColor.BLACK, MapColor.Brightness.HIGH);
    public static final CanvasColor GOLD_LOWEST = new CanvasColor("gold_lowest", MapColor.GOLD, MapColor.Brightness.LOWEST);
    public static final CanvasColor GOLD_LOW = new CanvasColor("gold_low", MapColor.GOLD, MapColor.Brightness.LOW);
    public static final CanvasColor GOLD_NORMAL = new CanvasColor("gold_normal", MapColor.GOLD, MapColor.Brightness.NORMAL);
    public static final CanvasColor GOLD_HIGH = new CanvasColor("gold_high", MapColor.GOLD, MapColor.Brightness.HIGH);
    public static final CanvasColor DIAMOND_BLUE_LOWEST = new CanvasColor("diamond_blue_lowest", MapColor.DIAMOND_BLUE, MapColor.Brightness.LOWEST);
    public static final CanvasColor DIAMOND_BLUE_LOW = new CanvasColor("diamond_blue_low", MapColor.DIAMOND_BLUE, MapColor.Brightness.LOW);
    public static final CanvasColor DIAMOND_BLUE_NORMAL = new CanvasColor("diamond_blue_normal", MapColor.DIAMOND_BLUE, MapColor.Brightness.NORMAL);
    public static final CanvasColor DIAMOND_BLUE_HIGH = new CanvasColor("diamond_blue_high", MapColor.DIAMOND_BLUE, MapColor.Brightness.HIGH);
    public static final CanvasColor LAPIS_BLUE_LOWEST = new CanvasColor("lapis_blue_lowest", MapColor.LAPIS_BLUE, MapColor.Brightness.LOWEST);
    public static final CanvasColor LAPIS_BLUE_LOW = new CanvasColor("lapis_blue_low", MapColor.LAPIS_BLUE, MapColor.Brightness.LOW);
    public static final CanvasColor LAPIS_BLUE_NORMAL = new CanvasColor("lapis_blue_normal", MapColor.LAPIS_BLUE, MapColor.Brightness.NORMAL);
    public static final CanvasColor LAPIS_BLUE_HIGH = new CanvasColor("lapis_blue_high", MapColor.LAPIS_BLUE, MapColor.Brightness.HIGH);
    public static final CanvasColor EMERALD_GREEN_LOWEST = new CanvasColor("emerald_green_lowest", MapColor.EMERALD_GREEN, MapColor.Brightness.LOWEST);
    public static final CanvasColor EMERALD_GREEN_LOW = new CanvasColor("emerald_green_low", MapColor.EMERALD_GREEN, MapColor.Brightness.LOW);
    public static final CanvasColor EMERALD_GREEN_NORMAL = new CanvasColor("emerald_green_normal", MapColor.EMERALD_GREEN, MapColor.Brightness.NORMAL);
    public static final CanvasColor EMERALD_GREEN_HIGH = new CanvasColor("emerald_green_high", MapColor.EMERALD_GREEN, MapColor.Brightness.HIGH);
    public static final CanvasColor SPRUCE_BROWN_LOWEST = new CanvasColor("spruce_brown_lowest", MapColor.SPRUCE_BROWN, MapColor.Brightness.LOWEST);
    public static final CanvasColor SPRUCE_BROWN_LOW = new CanvasColor("spruce_brown_low", MapColor.SPRUCE_BROWN, MapColor.Brightness.LOW);
    public static final CanvasColor SPRUCE_BROWN_NORMAL = new CanvasColor("spruce_brown_normal", MapColor.SPRUCE_BROWN, MapColor.Brightness.NORMAL);
    public static final CanvasColor SPRUCE_BROWN_HIGH = new CanvasColor("spruce_brown_high", MapColor.SPRUCE_BROWN, MapColor.Brightness.HIGH);
    public static final CanvasColor DARK_RED_LOWEST = new CanvasColor("dark_red_lowest", MapColor.DARK_RED, MapColor.Brightness.LOWEST);
    public static final CanvasColor DARK_RED_LOW = new CanvasColor("dark_red_low", MapColor.DARK_RED, MapColor.Brightness.LOW);
    public static final CanvasColor DARK_RED_NORMAL = new CanvasColor("dark_red_normal", MapColor.DARK_RED, MapColor.Brightness.NORMAL);
    public static final CanvasColor DARK_RED_HIGH = new CanvasColor("dark_red_high", MapColor.DARK_RED, MapColor.Brightness.HIGH);
    public static final CanvasColor TERRACOTTA_WHITE_LOWEST = new CanvasColor("terracotta_white_lowest", MapColor.TERRACOTTA_WHITE, MapColor.Brightness.LOWEST);
    public static final CanvasColor TERRACOTTA_WHITE_LOW = new CanvasColor("terracotta_white_low", MapColor.TERRACOTTA_WHITE, MapColor.Brightness.LOW);
    public static final CanvasColor TERRACOTTA_WHITE_NORMAL = new CanvasColor("terracotta_white_normal", MapColor.TERRACOTTA_WHITE, MapColor.Brightness.NORMAL);
    public static final CanvasColor TERRACOTTA_WHITE_HIGH = new CanvasColor("terracotta_white_high", MapColor.TERRACOTTA_WHITE, MapColor.Brightness.HIGH);
    public static final CanvasColor TERRACOTTA_ORANGE_LOWEST = new CanvasColor("terracotta_orange_lowest", MapColor.TERRACOTTA_ORANGE, MapColor.Brightness.LOWEST);
    public static final CanvasColor TERRACOTTA_ORANGE_LOW = new CanvasColor("terracotta_orange_low", MapColor.TERRACOTTA_ORANGE, MapColor.Brightness.LOW);
    public static final CanvasColor TERRACOTTA_ORANGE_NORMAL = new CanvasColor("terracotta_orange_normal", MapColor.TERRACOTTA_ORANGE, MapColor.Brightness.NORMAL);
    public static final CanvasColor TERRACOTTA_ORANGE_HIGH = new CanvasColor("terracotta_orange_high", MapColor.TERRACOTTA_ORANGE, MapColor.Brightness.HIGH);
    public static final CanvasColor TERRACOTTA_MAGENTA_LOWEST = new CanvasColor("terracotta_magenta_lowest", MapColor.TERRACOTTA_MAGENTA, MapColor.Brightness.LOWEST);
    public static final CanvasColor TERRACOTTA_MAGENTA_LOW = new CanvasColor("terracotta_magenta_low", MapColor.TERRACOTTA_MAGENTA, MapColor.Brightness.LOW);
    public static final CanvasColor TERRACOTTA_MAGENTA_NORMAL = new CanvasColor("terracotta_magenta_normal", MapColor.TERRACOTTA_MAGENTA, MapColor.Brightness.NORMAL);
    public static final CanvasColor TERRACOTTA_MAGENTA_HIGH = new CanvasColor("terracotta_magenta_high", MapColor.TERRACOTTA_MAGENTA, MapColor.Brightness.HIGH);
    public static final CanvasColor TERRACOTTA_LIGHT_BLUE_LOWEST = new CanvasColor("terracotta_light_blue_lowest", MapColor.TERRACOTTA_LIGHT_BLUE, MapColor.Brightness.LOWEST);
    public static final CanvasColor TERRACOTTA_LIGHT_BLUE_LOW = new CanvasColor("terracotta_light_blue_low", MapColor.TERRACOTTA_LIGHT_BLUE, MapColor.Brightness.LOW);
    public static final CanvasColor TERRACOTTA_LIGHT_BLUE_NORMAL = new CanvasColor("terracotta_light_blue_normal", MapColor.TERRACOTTA_LIGHT_BLUE, MapColor.Brightness.NORMAL);
    public static final CanvasColor TERRACOTTA_LIGHT_BLUE_HIGH = new CanvasColor("terracotta_light_blue_high", MapColor.TERRACOTTA_LIGHT_BLUE, MapColor.Brightness.HIGH);
    public static final CanvasColor TERRACOTTA_YELLOW_LOWEST = new CanvasColor("terracotta_yellow_lowest", MapColor.TERRACOTTA_YELLOW, MapColor.Brightness.LOWEST);
    public static final CanvasColor TERRACOTTA_YELLOW_LOW = new CanvasColor("terracotta_yellow_low", MapColor.TERRACOTTA_YELLOW, MapColor.Brightness.LOW);
    public static final CanvasColor TERRACOTTA_YELLOW_NORMAL = new CanvasColor("terracotta_yellow_normal", MapColor.TERRACOTTA_YELLOW, MapColor.Brightness.NORMAL);
    public static final CanvasColor TERRACOTTA_YELLOW_HIGH = new CanvasColor("terracotta_yellow_high", MapColor.TERRACOTTA_YELLOW, MapColor.Brightness.HIGH);
    public static final CanvasColor TERRACOTTA_LIME_LOWEST = new CanvasColor("terracotta_lime_lowest", MapColor.TERRACOTTA_LIME, MapColor.Brightness.LOWEST);
    public static final CanvasColor TERRACOTTA_LIME_LOW = new CanvasColor("terracotta_lime_low", MapColor.TERRACOTTA_LIME, MapColor.Brightness.LOW);
    public static final CanvasColor TERRACOTTA_LIME_NORMAL = new CanvasColor("terracotta_lime_normal", MapColor.TERRACOTTA_LIME, MapColor.Brightness.NORMAL);
    public static final CanvasColor TERRACOTTA_LIME_HIGH = new CanvasColor("terracotta_lime_high", MapColor.TERRACOTTA_LIME, MapColor.Brightness.HIGH);
    public static final CanvasColor TERRACOTTA_PINK_LOWEST = new CanvasColor("terracotta_pink_lowest", MapColor.TERRACOTTA_PINK, MapColor.Brightness.LOWEST);
    public static final CanvasColor TERRACOTTA_PINK_LOW = new CanvasColor("terracotta_pink_low", MapColor.TERRACOTTA_PINK, MapColor.Brightness.LOW);
    public static final CanvasColor TERRACOTTA_PINK_NORMAL = new CanvasColor("terracotta_pink_normal", MapColor.TERRACOTTA_PINK, MapColor.Brightness.NORMAL);
    public static final CanvasColor TERRACOTTA_PINK_HIGH = new CanvasColor("terracotta_pink_high", MapColor.TERRACOTTA_PINK, MapColor.Brightness.HIGH);
    public static final CanvasColor TERRACOTTA_GRAY_LOWEST = new CanvasColor("terracotta_gray_lowest", MapColor.TERRACOTTA_GRAY, MapColor.Brightness.LOWEST);
    public static final CanvasColor TERRACOTTA_GRAY_LOW = new CanvasColor("terracotta_gray_low", MapColor.TERRACOTTA_GRAY, MapColor.Brightness.LOW);
    public static final CanvasColor TERRACOTTA_GRAY_NORMAL = new CanvasColor("terracotta_gray_normal", MapColor.TERRACOTTA_GRAY, MapColor.Brightness.NORMAL);
    public static final CanvasColor TERRACOTTA_GRAY_HIGH = new CanvasColor("terracotta_gray_high", MapColor.TERRACOTTA_GRAY, MapColor.Brightness.HIGH);
    public static final CanvasColor TERRACOTTA_LIGHT_GRAY_LOWEST = new CanvasColor("terracotta_light_gray_lowest", MapColor.TERRACOTTA_LIGHT_GRAY, MapColor.Brightness.LOWEST);
    public static final CanvasColor TERRACOTTA_LIGHT_GRAY_LOW = new CanvasColor("terracotta_light_gray_low", MapColor.TERRACOTTA_LIGHT_GRAY, MapColor.Brightness.LOW);
    public static final CanvasColor TERRACOTTA_LIGHT_GRAY_NORMAL = new CanvasColor("terracotta_light_gray_normal", MapColor.TERRACOTTA_LIGHT_GRAY, MapColor.Brightness.NORMAL);
    public static final CanvasColor TERRACOTTA_LIGHT_GRAY_HIGH = new CanvasColor("terracotta_light_gray_high", MapColor.TERRACOTTA_LIGHT_GRAY, MapColor.Brightness.HIGH);
    public static final CanvasColor TERRACOTTA_CYAN_LOWEST = new CanvasColor("terracotta_cyan_lowest", MapColor.TERRACOTTA_CYAN, MapColor.Brightness.LOWEST);
    public static final CanvasColor TERRACOTTA_CYAN_LOW = new CanvasColor("terracotta_cyan_low", MapColor.TERRACOTTA_CYAN, MapColor.Brightness.LOW);
    public static final CanvasColor TERRACOTTA_CYAN_NORMAL = new CanvasColor("terracotta_cyan_normal", MapColor.TERRACOTTA_CYAN, MapColor.Brightness.NORMAL);
    public static final CanvasColor TERRACOTTA_CYAN_HIGH = new CanvasColor("terracotta_cyan_high", MapColor.TERRACOTTA_CYAN, MapColor.Brightness.HIGH);
    public static final CanvasColor TERRACOTTA_PURPLE_LOWEST = new CanvasColor("terracotta_purple_lowest", MapColor.TERRACOTTA_PURPLE, MapColor.Brightness.LOWEST);
    public static final CanvasColor TERRACOTTA_PURPLE_LOW = new CanvasColor("terracotta_purple_low", MapColor.TERRACOTTA_PURPLE, MapColor.Brightness.LOW);
    public static final CanvasColor TERRACOTTA_PURPLE_NORMAL = new CanvasColor("terracotta_purple_normal", MapColor.TERRACOTTA_PURPLE, MapColor.Brightness.NORMAL);
    public static final CanvasColor TERRACOTTA_PURPLE_HIGH = new CanvasColor("terracotta_purple_high", MapColor.TERRACOTTA_PURPLE, MapColor.Brightness.HIGH);
    public static final CanvasColor TERRACOTTA_BLUE_LOWEST = new CanvasColor("terracotta_blue_lowest", MapColor.TERRACOTTA_BLUE, MapColor.Brightness.LOWEST);
    public static final CanvasColor TERRACOTTA_BLUE_LOW = new CanvasColor("terracotta_blue_low", MapColor.TERRACOTTA_BLUE, MapColor.Brightness.LOW);
    public static final CanvasColor TERRACOTTA_BLUE_NORMAL = new CanvasColor("terracotta_blue_normal", MapColor.TERRACOTTA_BLUE, MapColor.Brightness.NORMAL);
    public static final CanvasColor TERRACOTTA_BLUE_HIGH = new CanvasColor("terracotta_blue_high", MapColor.TERRACOTTA_BLUE, MapColor.Brightness.HIGH);
    public static final CanvasColor TERRACOTTA_BROWN_LOWEST = new CanvasColor("terracotta_brown_lowest", MapColor.TERRACOTTA_BROWN, MapColor.Brightness.LOWEST);
    public static final CanvasColor TERRACOTTA_BROWN_LOW = new CanvasColor("terracotta_brown_low", MapColor.TERRACOTTA_BROWN, MapColor.Brightness.LOW);
    public static final CanvasColor TERRACOTTA_BROWN_NORMAL = new CanvasColor("terracotta_brown_normal", MapColor.TERRACOTTA_BROWN, MapColor.Brightness.NORMAL);
    public static final CanvasColor TERRACOTTA_BROWN_HIGH = new CanvasColor("terracotta_brown_high", MapColor.TERRACOTTA_BROWN, MapColor.Brightness.HIGH);
    public static final CanvasColor TERRACOTTA_GREEN_LOWEST = new CanvasColor("terracotta_green_lowest", MapColor.TERRACOTTA_GREEN, MapColor.Brightness.LOWEST);
    public static final CanvasColor TERRACOTTA_GREEN_LOW = new CanvasColor("terracotta_green_low", MapColor.TERRACOTTA_GREEN, MapColor.Brightness.LOW);
    public static final CanvasColor TERRACOTTA_GREEN_NORMAL = new CanvasColor("terracotta_green_normal", MapColor.TERRACOTTA_GREEN, MapColor.Brightness.NORMAL);
    public static final CanvasColor TERRACOTTA_GREEN_HIGH = new CanvasColor("terracotta_green_high", MapColor.TERRACOTTA_GREEN, MapColor.Brightness.HIGH);
    public static final CanvasColor TERRACOTTA_RED_LOWEST = new CanvasColor("terracotta_red_lowest", MapColor.TERRACOTTA_RED, MapColor.Brightness.LOWEST);
    public static final CanvasColor TERRACOTTA_RED_LOW = new CanvasColor("terracotta_red_low", MapColor.TERRACOTTA_RED, MapColor.Brightness.LOW);
    public static final CanvasColor TERRACOTTA_RED_NORMAL = new CanvasColor("terracotta_red_normal", MapColor.TERRACOTTA_RED, MapColor.Brightness.NORMAL);
    public static final CanvasColor TERRACOTTA_RED_HIGH = new CanvasColor("terracotta_red_high", MapColor.TERRACOTTA_RED, MapColor.Brightness.HIGH);
    public static final CanvasColor TERRACOTTA_BLACK_LOWEST = new CanvasColor("terracotta_black_lowest", MapColor.TERRACOTTA_BLACK, MapColor.Brightness.LOWEST);
    public static final CanvasColor TERRACOTTA_BLACK_LOW = new CanvasColor("terracotta_black_low", MapColor.TERRACOTTA_BLACK, MapColor.Brightness.LOW);
    public static final CanvasColor TERRACOTTA_BLACK_NORMAL = new CanvasColor("terracotta_black_normal", MapColor.TERRACOTTA_BLACK, MapColor.Brightness.NORMAL);
    public static final CanvasColor TERRACOTTA_BLACK_HIGH = new CanvasColor("terracotta_black_high", MapColor.TERRACOTTA_BLACK, MapColor.Brightness.HIGH);
    public static final CanvasColor DULL_RED_LOWEST = new CanvasColor("dull_red_lowest", MapColor.DULL_RED, MapColor.Brightness.LOWEST);
    public static final CanvasColor DULL_RED_LOW = new CanvasColor("dull_red_low", MapColor.DULL_RED, MapColor.Brightness.LOW);
    public static final CanvasColor DULL_RED_NORMAL = new CanvasColor("dull_red_normal", MapColor.DULL_RED, MapColor.Brightness.NORMAL);
    public static final CanvasColor DULL_RED_HIGH = new CanvasColor("dull_red_high", MapColor.DULL_RED, MapColor.Brightness.HIGH);
    public static final CanvasColor DULL_PINK_LOWEST = new CanvasColor("dull_pink_lowest", MapColor.DULL_PINK, MapColor.Brightness.LOWEST);
    public static final CanvasColor DULL_PINK_LOW = new CanvasColor("dull_pink_low", MapColor.DULL_PINK, MapColor.Brightness.LOW);
    public static final CanvasColor DULL_PINK_NORMAL = new CanvasColor("dull_pink_normal", MapColor.DULL_PINK, MapColor.Brightness.NORMAL);
    public static final CanvasColor DULL_PINK_HIGH = new CanvasColor("dull_pink_high", MapColor.DULL_PINK, MapColor.Brightness.HIGH);
    public static final CanvasColor DARK_CRIMSON_LOWEST = new CanvasColor("dark_crimson_lowest", MapColor.DARK_CRIMSON, MapColor.Brightness.LOWEST);
    public static final CanvasColor DARK_CRIMSON_LOW = new CanvasColor("dark_crimson_low", MapColor.DARK_CRIMSON, MapColor.Brightness.LOW);
    public static final CanvasColor DARK_CRIMSON_NORMAL = new CanvasColor("dark_crimson_normal", MapColor.DARK_CRIMSON, MapColor.Brightness.NORMAL);
    public static final CanvasColor DARK_CRIMSON_HIGH = new CanvasColor("dark_crimson_high", MapColor.DARK_CRIMSON, MapColor.Brightness.HIGH);
    public static final CanvasColor TEAL_LOWEST = new CanvasColor("teal_lowest", MapColor.TEAL, MapColor.Brightness.LOWEST);
    public static final CanvasColor TEAL_LOW = new CanvasColor("teal_low", MapColor.TEAL, MapColor.Brightness.LOW);
    public static final CanvasColor TEAL_NORMAL = new CanvasColor("teal_normal", MapColor.TEAL, MapColor.Brightness.NORMAL);
    public static final CanvasColor TEAL_HIGH = new CanvasColor("teal_high", MapColor.TEAL, MapColor.Brightness.HIGH);
    public static final CanvasColor DARK_AQUA_LOWEST = new CanvasColor("dark_aqua_lowest", MapColor.DARK_AQUA, MapColor.Brightness.LOWEST);
    public static final CanvasColor DARK_AQUA_LOW = new CanvasColor("dark_aqua_low", MapColor.DARK_AQUA, MapColor.Brightness.LOW);
    public static final CanvasColor DARK_AQUA_NORMAL = new CanvasColor("dark_aqua_normal", MapColor.DARK_AQUA, MapColor.Brightness.NORMAL);
    public static final CanvasColor DARK_AQUA_HIGH = new CanvasColor("dark_aqua_high", MapColor.DARK_AQUA, MapColor.Brightness.HIGH);
    public static final CanvasColor DARK_DULL_PINK_LOWEST = new CanvasColor("dark_dull_pink_lowest", MapColor.DARK_DULL_PINK, MapColor.Brightness.LOWEST);
    public static final CanvasColor DARK_DULL_PINK_LOW = new CanvasColor("dark_dull_pink_low", MapColor.DARK_DULL_PINK, MapColor.Brightness.LOW);
    public static final CanvasColor DARK_DULL_PINK_NORMAL = new CanvasColor("dark_dull_pink_normal", MapColor.DARK_DULL_PINK, MapColor.Brightness.NORMAL);
    public static final CanvasColor DARK_DULL_PINK_HIGH = new CanvasColor("dark_dull_pink_high", MapColor.DARK_DULL_PINK, MapColor.Brightness.HIGH);
    public static final CanvasColor BRIGHT_TEAL_LOWEST = new CanvasColor("bright_teal_lowest", MapColor.BRIGHT_TEAL, MapColor.Brightness.LOWEST);
    public static final CanvasColor BRIGHT_TEAL_LOW = new CanvasColor("bright_teal_low", MapColor.BRIGHT_TEAL, MapColor.Brightness.LOW);
    public static final CanvasColor BRIGHT_TEAL_NORMAL = new CanvasColor("bright_teal_normal", MapColor.BRIGHT_TEAL, MapColor.Brightness.NORMAL);
    public static final CanvasColor BRIGHT_TEAL_HIGH = new CanvasColor("bright_teal_high", MapColor.BRIGHT_TEAL, MapColor.Brightness.HIGH);
    public static final CanvasColor DEEPSLATE_GRAY_LOWEST = new CanvasColor("deepslate_gray_lowest", MapColor.DEEPSLATE_GRAY, MapColor.Brightness.LOWEST);
    public static final CanvasColor DEEPSLATE_GRAY_LOW = new CanvasColor("deepslate_gray_low", MapColor.DEEPSLATE_GRAY, MapColor.Brightness.LOW);
    public static final CanvasColor DEEPSLATE_GRAY_NORMAL = new CanvasColor("deepslate_gray_normal", MapColor.DEEPSLATE_GRAY, MapColor.Brightness.NORMAL);
    public static final CanvasColor DEEPSLATE_GRAY_HIGH = new CanvasColor("deepslate_gray_high", MapColor.DEEPSLATE_GRAY, MapColor.Brightness.HIGH);
    public static final CanvasColor RAW_IRON_PINK_LOWEST = new CanvasColor("raw_iron_pink_lowest", MapColor.RAW_IRON_PINK, MapColor.Brightness.LOWEST);
    public static final CanvasColor RAW_IRON_PINK_LOW = new CanvasColor("raw_iron_pink_low", MapColor.RAW_IRON_PINK, MapColor.Brightness.LOW);
    public static final CanvasColor RAW_IRON_PINK_NORMAL = new CanvasColor("raw_iron_pink_normal", MapColor.RAW_IRON_PINK, MapColor.Brightness.NORMAL);
    public static final CanvasColor RAW_IRON_PINK_HIGH = new CanvasColor("raw_iron_pink_high", MapColor.RAW_IRON_PINK, MapColor.Brightness.HIGH);
    public static final CanvasColor LICHEN_GREEN_LOWEST = new CanvasColor("lichen_green_lowest", MapColor.LICHEN_GREEN, MapColor.Brightness.LOWEST);
    public static final CanvasColor LICHEN_GREEN_LOW = new CanvasColor("lichen_green_low", MapColor.LICHEN_GREEN, MapColor.Brightness.LOW);
    public static final CanvasColor LICHEN_GREEN_NORMAL = new CanvasColor("lichen_green_normal", MapColor.LICHEN_GREEN, MapColor.Brightness.NORMAL);
    public static final CanvasColor LICHEN_GREEN_HIGH = new CanvasColor("lichen_green_high", MapColor.LICHEN_GREEN, MapColor.Brightness.HIGH);

    protected final MapColor color;
    protected final MapColor.Brightness brightness;
    protected final byte renderColor;
    protected final int rgbColor;
    private final String name;

    private CanvasColor(String name, MapColor color, MapColor.Brightness brightness) {
        this.name = name;
        this.color = color;
        this.brightness = brightness;
        this.renderColor = color.getRenderColorByte(this.brightness);

        var bgr = color.getRenderColor(brightness);

        final int redCanvas = (bgr) & 0xFF;
        final int greenCanvas = (bgr >> 8) & 0xFF;
        final int blueCanvas = (bgr >> 16) & 0xFF;

        this.rgbColor = redCanvas << 16 | greenCanvas << 8 | blueCanvas;

        BY_RENDER_COLOR[Byte.toUnsignedInt(this.renderColor)] = this;
    }

    public static CanvasColor[] values() {
        return BY_RENDER_COLOR;
    }

    public final MapColor getColor() {
        return this.color;
    }

    public final MapColor.Brightness getBrightness() {
        return this.brightness;
    }

    public final byte getRenderColor() {
        return this.renderColor;
    }

    public final int getRgbColor() { return this.rgbColor; }

    public static CanvasColor getFromRaw(byte renderColor) {
        return BY_RENDER_COLOR[Byte.toUnsignedInt(renderColor)];
    }

    public static CanvasColor from(MapColor color, MapColor.Brightness brightness) {
        return BY_RENDER_COLOR[Byte.toUnsignedInt(color.getRenderColorByte(brightness))];
    }

    public String getName() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        CanvasColor that = (CanvasColor) o;
        return this.color.id == that.color.id && this.brightness == that.brightness;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.color.id, this.brightness);
    }

    @Override
    public String toString() {
        return "CanvasColor{" +
                "color=" + color.id +
                ", brightness=" + brightness +
                ", renderColor=" + renderColor +
                ", name='" + name + '\'' +
                '}';
    }

    static {
        for (int i = 0; i < BY_RENDER_COLOR.length; i++) {
            if (BY_RENDER_COLOR[i] == null) {
                BY_RENDER_COLOR[i] = CLEAR;
            }
        }
    }
}
