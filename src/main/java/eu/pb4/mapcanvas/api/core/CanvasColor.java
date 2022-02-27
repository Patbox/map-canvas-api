package eu.pb4.mapcanvas.api.core;

import net.minecraft.block.MapColor;

/**
 * All colors supported by Minecraft/Map Canvas API
 */
@SuppressWarnings("unused")
public final class CanvasColor {
    protected static final CanvasColor[] BY_RENDER_COLOR = new CanvasColor[256];
    public static final CanvasColor CLEAR = new CanvasColor(MapColor.CLEAR, MapColor.Brightness.LOW);
    public static final CanvasColor CLEAR_FORCE = new CanvasColor(MapColor.CLEAR, MapColor.Brightness.NORMAL);
    public static final CanvasColor PALE_GREEN_LOWEST = new CanvasColor(MapColor.PALE_GREEN, MapColor.Brightness.LOWEST);
    public static final CanvasColor PALE_GREEN_LOW = new CanvasColor(MapColor.PALE_GREEN, MapColor.Brightness.LOW);
    public static final CanvasColor PALE_GREEN_NORMAL = new CanvasColor(MapColor.PALE_GREEN, MapColor.Brightness.NORMAL);
    public static final CanvasColor PALE_GREEN_HIGH = new CanvasColor(MapColor.PALE_GREEN, MapColor.Brightness.HIGH);
    public static final CanvasColor PALE_YELLOW_LOWEST = new CanvasColor(MapColor.PALE_YELLOW, MapColor.Brightness.LOWEST);
    public static final CanvasColor PALE_YELLOW_LOW = new CanvasColor(MapColor.PALE_YELLOW, MapColor.Brightness.LOW);
    public static final CanvasColor PALE_YELLOW_NORMAL = new CanvasColor(MapColor.PALE_YELLOW, MapColor.Brightness.NORMAL);
    public static final CanvasColor PALE_YELLOW_HIGH = new CanvasColor(MapColor.PALE_YELLOW, MapColor.Brightness.HIGH);
    public static final CanvasColor WHITE_GRAY_LOWEST = new CanvasColor(MapColor.WHITE_GRAY, MapColor.Brightness.LOWEST);
    public static final CanvasColor WHITE_GRAY_LOW = new CanvasColor(MapColor.WHITE_GRAY, MapColor.Brightness.LOW);
    public static final CanvasColor WHITE_GRAY_NORMAL = new CanvasColor(MapColor.WHITE_GRAY, MapColor.Brightness.NORMAL);
    public static final CanvasColor WHITE_GRAY_HIGH = new CanvasColor(MapColor.WHITE_GRAY, MapColor.Brightness.HIGH);
    public static final CanvasColor BRIGHT_RED_LOWEST = new CanvasColor(MapColor.BRIGHT_RED, MapColor.Brightness.LOWEST);
    public static final CanvasColor BRIGHT_RED_LOW = new CanvasColor(MapColor.BRIGHT_RED, MapColor.Brightness.LOW);
    public static final CanvasColor BRIGHT_RED_NORMAL = new CanvasColor(MapColor.BRIGHT_RED, MapColor.Brightness.NORMAL);
    public static final CanvasColor BRIGHT_RED_HIGH = new CanvasColor(MapColor.BRIGHT_RED, MapColor.Brightness.HIGH);
    public static final CanvasColor PALE_PURPLE_LOWEST = new CanvasColor(MapColor.PALE_PURPLE, MapColor.Brightness.LOWEST);
    public static final CanvasColor PALE_PURPLE_LOW = new CanvasColor(MapColor.PALE_PURPLE, MapColor.Brightness.LOW);
    public static final CanvasColor PALE_PURPLE_NORMAL = new CanvasColor(MapColor.PALE_PURPLE, MapColor.Brightness.NORMAL);
    public static final CanvasColor PALE_PURPLE_HIGH = new CanvasColor(MapColor.PALE_PURPLE, MapColor.Brightness.HIGH);
    public static final CanvasColor IRON_GRAY_LOWEST = new CanvasColor(MapColor.IRON_GRAY, MapColor.Brightness.LOWEST);
    public static final CanvasColor IRON_GRAY_LOW = new CanvasColor(MapColor.IRON_GRAY, MapColor.Brightness.LOW);
    public static final CanvasColor IRON_GRAY_NORMAL = new CanvasColor(MapColor.IRON_GRAY, MapColor.Brightness.NORMAL);
    public static final CanvasColor IRON_GRAY_HIGH = new CanvasColor(MapColor.IRON_GRAY, MapColor.Brightness.HIGH);
    public static final CanvasColor DARK_GREEN_LOWEST = new CanvasColor(MapColor.DARK_GREEN, MapColor.Brightness.LOWEST);
    public static final CanvasColor DARK_GREEN_LOW = new CanvasColor(MapColor.DARK_GREEN, MapColor.Brightness.LOW);
    public static final CanvasColor DARK_GREEN_NORMAL = new CanvasColor(MapColor.DARK_GREEN, MapColor.Brightness.NORMAL);
    public static final CanvasColor DARK_GREEN_HIGH = new CanvasColor(MapColor.DARK_GREEN, MapColor.Brightness.HIGH);
    public static final CanvasColor WHITE_LOWEST = new CanvasColor(MapColor.WHITE, MapColor.Brightness.LOWEST);
    public static final CanvasColor WHITE_LOW = new CanvasColor(MapColor.WHITE, MapColor.Brightness.LOW);
    public static final CanvasColor WHITE_NORMAL = new CanvasColor(MapColor.WHITE, MapColor.Brightness.NORMAL);
    public static final CanvasColor WHITE_HIGH = new CanvasColor(MapColor.WHITE, MapColor.Brightness.HIGH);
    public static final CanvasColor LIGHT_BLUE_GRAY_LOWEST = new CanvasColor(MapColor.LIGHT_BLUE_GRAY, MapColor.Brightness.LOWEST);
    public static final CanvasColor LIGHT_BLUE_GRAY_LOW = new CanvasColor(MapColor.LIGHT_BLUE_GRAY, MapColor.Brightness.LOW);
    public static final CanvasColor LIGHT_BLUE_GRAY_NORMAL = new CanvasColor(MapColor.LIGHT_BLUE_GRAY, MapColor.Brightness.NORMAL);
    public static final CanvasColor LIGHT_BLUE_GRAY_HIGH = new CanvasColor(MapColor.LIGHT_BLUE_GRAY, MapColor.Brightness.HIGH);
    public static final CanvasColor DIRT_BROWN_LOWEST = new CanvasColor(MapColor.DIRT_BROWN, MapColor.Brightness.LOWEST);
    public static final CanvasColor DIRT_BROWN_LOW = new CanvasColor(MapColor.DIRT_BROWN, MapColor.Brightness.LOW);
    public static final CanvasColor DIRT_BROWN_NORMAL = new CanvasColor(MapColor.DIRT_BROWN, MapColor.Brightness.NORMAL);
    public static final CanvasColor DIRT_BROWN_HIGH = new CanvasColor(MapColor.DIRT_BROWN, MapColor.Brightness.HIGH);
    public static final CanvasColor STONE_GRAY_LOWEST = new CanvasColor(MapColor.STONE_GRAY, MapColor.Brightness.LOWEST);
    public static final CanvasColor STONE_GRAY_LOW = new CanvasColor(MapColor.STONE_GRAY, MapColor.Brightness.LOW);
    public static final CanvasColor STONE_GRAY_NORMAL = new CanvasColor(MapColor.STONE_GRAY, MapColor.Brightness.NORMAL);
    public static final CanvasColor STONE_GRAY_HIGH = new CanvasColor(MapColor.STONE_GRAY, MapColor.Brightness.HIGH);
    public static final CanvasColor WATER_BLUE_LOWEST = new CanvasColor(MapColor.WATER_BLUE, MapColor.Brightness.LOWEST);
    public static final CanvasColor WATER_BLUE_LOW = new CanvasColor(MapColor.WATER_BLUE, MapColor.Brightness.LOW);
    public static final CanvasColor WATER_BLUE_NORMAL = new CanvasColor(MapColor.WATER_BLUE, MapColor.Brightness.NORMAL);
    public static final CanvasColor WATER_BLUE_HIGH = new CanvasColor(MapColor.WATER_BLUE, MapColor.Brightness.HIGH);
    public static final CanvasColor OAK_TAN_LOWEST = new CanvasColor(MapColor.OAK_TAN, MapColor.Brightness.LOWEST);
    public static final CanvasColor OAK_TAN_LOW = new CanvasColor(MapColor.OAK_TAN, MapColor.Brightness.LOW);
    public static final CanvasColor OAK_TAN_NORMAL = new CanvasColor(MapColor.OAK_TAN, MapColor.Brightness.NORMAL);
    public static final CanvasColor OAK_TAN_HIGH = new CanvasColor(MapColor.OAK_TAN, MapColor.Brightness.HIGH);
    public static final CanvasColor OFF_WHITE_LOWEST = new CanvasColor(MapColor.OFF_WHITE, MapColor.Brightness.LOWEST);
    public static final CanvasColor OFF_WHITE_LOW = new CanvasColor(MapColor.OFF_WHITE, MapColor.Brightness.LOW);
    public static final CanvasColor OFF_WHITE_NORMAL = new CanvasColor(MapColor.OFF_WHITE, MapColor.Brightness.NORMAL);
    public static final CanvasColor OFF_WHITE_HIGH = new CanvasColor(MapColor.OFF_WHITE, MapColor.Brightness.HIGH);
    public static final CanvasColor ORANGE_LOWEST = new CanvasColor(MapColor.ORANGE, MapColor.Brightness.LOWEST);
    public static final CanvasColor ORANGE_LOW = new CanvasColor(MapColor.ORANGE, MapColor.Brightness.LOW);
    public static final CanvasColor ORANGE_NORMAL = new CanvasColor(MapColor.ORANGE, MapColor.Brightness.NORMAL);
    public static final CanvasColor ORANGE_HIGH = new CanvasColor(MapColor.ORANGE, MapColor.Brightness.HIGH);
    public static final CanvasColor MAGENTA_LOWEST = new CanvasColor(MapColor.MAGENTA, MapColor.Brightness.LOWEST);
    public static final CanvasColor MAGENTA_LOW = new CanvasColor(MapColor.MAGENTA, MapColor.Brightness.LOW);
    public static final CanvasColor MAGENTA_NORMAL = new CanvasColor(MapColor.MAGENTA, MapColor.Brightness.NORMAL);
    public static final CanvasColor MAGENTA_HIGH = new CanvasColor(MapColor.MAGENTA, MapColor.Brightness.HIGH);
    public static final CanvasColor LIGHT_BLUE_LOWEST = new CanvasColor(MapColor.LIGHT_BLUE, MapColor.Brightness.LOWEST);
    public static final CanvasColor LIGHT_BLUE_LOW = new CanvasColor(MapColor.LIGHT_BLUE, MapColor.Brightness.LOW);
    public static final CanvasColor LIGHT_BLUE_NORMAL = new CanvasColor(MapColor.LIGHT_BLUE, MapColor.Brightness.NORMAL);
    public static final CanvasColor LIGHT_BLUE_HIGH = new CanvasColor(MapColor.LIGHT_BLUE, MapColor.Brightness.HIGH);
    public static final CanvasColor YELLOW_LOWEST = new CanvasColor(MapColor.YELLOW, MapColor.Brightness.LOWEST);
    public static final CanvasColor YELLOW_LOW = new CanvasColor(MapColor.YELLOW, MapColor.Brightness.LOW);
    public static final CanvasColor YELLOW_NORMAL = new CanvasColor(MapColor.YELLOW, MapColor.Brightness.NORMAL);
    public static final CanvasColor YELLOW_HIGH = new CanvasColor(MapColor.YELLOW, MapColor.Brightness.HIGH);
    public static final CanvasColor LIME_LOWEST = new CanvasColor(MapColor.LIME, MapColor.Brightness.LOWEST);
    public static final CanvasColor LIME_LOW = new CanvasColor(MapColor.LIME, MapColor.Brightness.LOW);
    public static final CanvasColor LIME_NORMAL = new CanvasColor(MapColor.LIME, MapColor.Brightness.NORMAL);
    public static final CanvasColor LIME_HIGH = new CanvasColor(MapColor.LIME, MapColor.Brightness.HIGH);
    public static final CanvasColor PINK_LOWEST = new CanvasColor(MapColor.PINK, MapColor.Brightness.LOWEST);
    public static final CanvasColor PINK_LOW = new CanvasColor(MapColor.PINK, MapColor.Brightness.LOW);
    public static final CanvasColor PINK_NORMAL = new CanvasColor(MapColor.PINK, MapColor.Brightness.NORMAL);
    public static final CanvasColor PINK_HIGH = new CanvasColor(MapColor.PINK, MapColor.Brightness.HIGH);
    public static final CanvasColor GRAY_LOWEST = new CanvasColor(MapColor.GRAY, MapColor.Brightness.LOWEST);
    public static final CanvasColor GRAY_LOW = new CanvasColor(MapColor.GRAY, MapColor.Brightness.LOW);
    public static final CanvasColor GRAY_NORMAL = new CanvasColor(MapColor.GRAY, MapColor.Brightness.NORMAL);
    public static final CanvasColor GRAY_HIGH = new CanvasColor(MapColor.GRAY, MapColor.Brightness.HIGH);
    public static final CanvasColor LIGHT_GRAY_LOWEST = new CanvasColor(MapColor.LIGHT_GRAY, MapColor.Brightness.LOWEST);
    public static final CanvasColor LIGHT_GRAY_LOW = new CanvasColor(MapColor.LIGHT_GRAY, MapColor.Brightness.LOW);
    public static final CanvasColor LIGHT_GRAY_NORMAL = new CanvasColor(MapColor.LIGHT_GRAY, MapColor.Brightness.NORMAL);
    public static final CanvasColor LIGHT_GRAY_HIGH = new CanvasColor(MapColor.LIGHT_GRAY, MapColor.Brightness.HIGH);
    public static final CanvasColor CYAN_LOWEST = new CanvasColor(MapColor.CYAN, MapColor.Brightness.LOWEST);
    public static final CanvasColor CYAN_LOW = new CanvasColor(MapColor.CYAN, MapColor.Brightness.LOW);
    public static final CanvasColor CYAN_NORMAL = new CanvasColor(MapColor.CYAN, MapColor.Brightness.NORMAL);
    public static final CanvasColor CYAN_HIGH = new CanvasColor(MapColor.CYAN, MapColor.Brightness.HIGH);
    public static final CanvasColor PURPLE_LOWEST = new CanvasColor(MapColor.PURPLE, MapColor.Brightness.LOWEST);
    public static final CanvasColor PURPLE_LOW = new CanvasColor(MapColor.PURPLE, MapColor.Brightness.LOW);
    public static final CanvasColor PURPLE_NORMAL = new CanvasColor(MapColor.PURPLE, MapColor.Brightness.NORMAL);
    public static final CanvasColor PURPLE_HIGH = new CanvasColor(MapColor.PURPLE, MapColor.Brightness.HIGH);
    public static final CanvasColor BLUE_LOWEST = new CanvasColor(MapColor.BLUE, MapColor.Brightness.LOWEST);
    public static final CanvasColor BLUE_LOW = new CanvasColor(MapColor.BLUE, MapColor.Brightness.LOW);
    public static final CanvasColor BLUE_NORMAL = new CanvasColor(MapColor.BLUE, MapColor.Brightness.NORMAL);
    public static final CanvasColor BLUE_HIGH = new CanvasColor(MapColor.BLUE, MapColor.Brightness.HIGH);
    public static final CanvasColor BROWN_LOWEST = new CanvasColor(MapColor.BROWN, MapColor.Brightness.LOWEST);
    public static final CanvasColor BROWN_LOW = new CanvasColor(MapColor.BROWN, MapColor.Brightness.LOW);
    public static final CanvasColor BROWN_NORMAL = new CanvasColor(MapColor.BROWN, MapColor.Brightness.NORMAL);
    public static final CanvasColor BROWN_HIGH = new CanvasColor(MapColor.BROWN, MapColor.Brightness.HIGH);
    public static final CanvasColor GREEN_LOWEST = new CanvasColor(MapColor.GREEN, MapColor.Brightness.LOWEST);
    public static final CanvasColor GREEN_LOW = new CanvasColor(MapColor.GREEN, MapColor.Brightness.LOW);
    public static final CanvasColor GREEN_NORMAL = new CanvasColor(MapColor.GREEN, MapColor.Brightness.NORMAL);
    public static final CanvasColor GREEN_HIGH = new CanvasColor(MapColor.GREEN, MapColor.Brightness.HIGH);
    public static final CanvasColor RED_LOWEST = new CanvasColor(MapColor.RED, MapColor.Brightness.LOWEST);
    public static final CanvasColor RED_LOW = new CanvasColor(MapColor.RED, MapColor.Brightness.LOW);
    public static final CanvasColor RED_NORMAL = new CanvasColor(MapColor.RED, MapColor.Brightness.NORMAL);
    public static final CanvasColor RED_HIGH = new CanvasColor(MapColor.RED, MapColor.Brightness.HIGH);
    public static final CanvasColor BLACK_LOWEST = new CanvasColor(MapColor.BLACK, MapColor.Brightness.LOWEST);
    public static final CanvasColor BLACK_LOW = new CanvasColor(MapColor.BLACK, MapColor.Brightness.LOW);
    public static final CanvasColor BLACK_NORMAL = new CanvasColor(MapColor.BLACK, MapColor.Brightness.NORMAL);
    public static final CanvasColor BLACK_HIGH = new CanvasColor(MapColor.BLACK, MapColor.Brightness.HIGH);
    public static final CanvasColor GOLD_LOWEST = new CanvasColor(MapColor.GOLD, MapColor.Brightness.LOWEST);
    public static final CanvasColor GOLD_LOW = new CanvasColor(MapColor.GOLD, MapColor.Brightness.LOW);
    public static final CanvasColor GOLD_NORMAL = new CanvasColor(MapColor.GOLD, MapColor.Brightness.NORMAL);
    public static final CanvasColor GOLD_HIGH = new CanvasColor(MapColor.GOLD, MapColor.Brightness.HIGH);
    public static final CanvasColor DIAMOND_BLUE_LOWEST = new CanvasColor(MapColor.DIAMOND_BLUE, MapColor.Brightness.LOWEST);
    public static final CanvasColor DIAMOND_BLUE_LOW = new CanvasColor(MapColor.DIAMOND_BLUE, MapColor.Brightness.LOW);
    public static final CanvasColor DIAMOND_BLUE_NORMAL = new CanvasColor(MapColor.DIAMOND_BLUE, MapColor.Brightness.NORMAL);
    public static final CanvasColor DIAMOND_BLUE_HIGH = new CanvasColor(MapColor.DIAMOND_BLUE, MapColor.Brightness.HIGH);
    public static final CanvasColor LAPIS_BLUE_LOWEST = new CanvasColor(MapColor.LAPIS_BLUE, MapColor.Brightness.LOWEST);
    public static final CanvasColor LAPIS_BLUE_LOW = new CanvasColor(MapColor.LAPIS_BLUE, MapColor.Brightness.LOW);
    public static final CanvasColor LAPIS_BLUE_NORMAL = new CanvasColor(MapColor.LAPIS_BLUE, MapColor.Brightness.NORMAL);
    public static final CanvasColor LAPIS_BLUE_HIGH = new CanvasColor(MapColor.LAPIS_BLUE, MapColor.Brightness.HIGH);
    public static final CanvasColor EMERALD_GREEN_LOWEST = new CanvasColor(MapColor.EMERALD_GREEN, MapColor.Brightness.LOWEST);
    public static final CanvasColor EMERALD_GREEN_LOW = new CanvasColor(MapColor.EMERALD_GREEN, MapColor.Brightness.LOW);
    public static final CanvasColor EMERALD_GREEN_NORMAL = new CanvasColor(MapColor.EMERALD_GREEN, MapColor.Brightness.NORMAL);
    public static final CanvasColor EMERALD_GREEN_HIGH = new CanvasColor(MapColor.EMERALD_GREEN, MapColor.Brightness.HIGH);
    public static final CanvasColor SPRUCE_BROWN_LOWEST = new CanvasColor(MapColor.SPRUCE_BROWN, MapColor.Brightness.LOWEST);
    public static final CanvasColor SPRUCE_BROWN_LOW = new CanvasColor(MapColor.SPRUCE_BROWN, MapColor.Brightness.LOW);
    public static final CanvasColor SPRUCE_BROWN_NORMAL = new CanvasColor(MapColor.SPRUCE_BROWN, MapColor.Brightness.NORMAL);
    public static final CanvasColor SPRUCE_BROWN_HIGH = new CanvasColor(MapColor.SPRUCE_BROWN, MapColor.Brightness.HIGH);
    public static final CanvasColor DARK_RED_LOWEST = new CanvasColor(MapColor.DARK_RED, MapColor.Brightness.LOWEST);
    public static final CanvasColor DARK_RED_LOW = new CanvasColor(MapColor.DARK_RED, MapColor.Brightness.LOW);
    public static final CanvasColor DARK_RED_NORMAL = new CanvasColor(MapColor.DARK_RED, MapColor.Brightness.NORMAL);
    public static final CanvasColor DARK_RED_HIGH = new CanvasColor(MapColor.DARK_RED, MapColor.Brightness.HIGH);
    public static final CanvasColor TERRACOTTA_WHITE_LOWEST = new CanvasColor(MapColor.TERRACOTTA_WHITE, MapColor.Brightness.LOWEST);
    public static final CanvasColor TERRACOTTA_WHITE_LOW = new CanvasColor(MapColor.TERRACOTTA_WHITE, MapColor.Brightness.LOW);
    public static final CanvasColor TERRACOTTA_WHITE_NORMAL = new CanvasColor(MapColor.TERRACOTTA_WHITE, MapColor.Brightness.NORMAL);
    public static final CanvasColor TERRACOTTA_WHITE_HIGH = new CanvasColor(MapColor.TERRACOTTA_WHITE, MapColor.Brightness.HIGH);
    public static final CanvasColor TERRACOTTA_ORANGE_LOWEST = new CanvasColor(MapColor.TERRACOTTA_ORANGE, MapColor.Brightness.LOWEST);
    public static final CanvasColor TERRACOTTA_ORANGE_LOW = new CanvasColor(MapColor.TERRACOTTA_ORANGE, MapColor.Brightness.LOW);
    public static final CanvasColor TERRACOTTA_ORANGE_NORMAL = new CanvasColor(MapColor.TERRACOTTA_ORANGE, MapColor.Brightness.NORMAL);
    public static final CanvasColor TERRACOTTA_ORANGE_HIGH = new CanvasColor(MapColor.TERRACOTTA_ORANGE, MapColor.Brightness.HIGH);
    public static final CanvasColor TERRACOTTA_MAGENTA_LOWEST = new CanvasColor(MapColor.TERRACOTTA_MAGENTA, MapColor.Brightness.LOWEST);
    public static final CanvasColor TERRACOTTA_MAGENTA_LOW = new CanvasColor(MapColor.TERRACOTTA_MAGENTA, MapColor.Brightness.LOW);
    public static final CanvasColor TERRACOTTA_MAGENTA_NORMAL = new CanvasColor(MapColor.TERRACOTTA_MAGENTA, MapColor.Brightness.NORMAL);
    public static final CanvasColor TERRACOTTA_MAGENTA_HIGH = new CanvasColor(MapColor.TERRACOTTA_MAGENTA, MapColor.Brightness.HIGH);
    public static final CanvasColor TERRACOTTA_LIGHT_BLUE_LOWEST = new CanvasColor(MapColor.TERRACOTTA_LIGHT_BLUE, MapColor.Brightness.LOWEST);
    public static final CanvasColor TERRACOTTA_LIGHT_BLUE_LOW = new CanvasColor(MapColor.TERRACOTTA_LIGHT_BLUE, MapColor.Brightness.LOW);
    public static final CanvasColor TERRACOTTA_LIGHT_BLUE_NORMAL = new CanvasColor(MapColor.TERRACOTTA_LIGHT_BLUE, MapColor.Brightness.NORMAL);
    public static final CanvasColor TERRACOTTA_LIGHT_BLUE_HIGH = new CanvasColor(MapColor.TERRACOTTA_LIGHT_BLUE, MapColor.Brightness.HIGH);
    public static final CanvasColor TERRACOTTA_YELLOW_LOWEST = new CanvasColor(MapColor.TERRACOTTA_YELLOW, MapColor.Brightness.LOWEST);
    public static final CanvasColor TERRACOTTA_YELLOW_LOW = new CanvasColor(MapColor.TERRACOTTA_YELLOW, MapColor.Brightness.LOW);
    public static final CanvasColor TERRACOTTA_YELLOW_NORMAL = new CanvasColor(MapColor.TERRACOTTA_YELLOW, MapColor.Brightness.NORMAL);
    public static final CanvasColor TERRACOTTA_YELLOW_HIGH = new CanvasColor(MapColor.TERRACOTTA_YELLOW, MapColor.Brightness.HIGH);
    public static final CanvasColor TERRACOTTA_LIME_LOWEST = new CanvasColor(MapColor.TERRACOTTA_LIME, MapColor.Brightness.LOWEST);
    public static final CanvasColor TERRACOTTA_LIME_LOW = new CanvasColor(MapColor.TERRACOTTA_LIME, MapColor.Brightness.LOW);
    public static final CanvasColor TERRACOTTA_LIME_NORMAL = new CanvasColor(MapColor.TERRACOTTA_LIME, MapColor.Brightness.NORMAL);
    public static final CanvasColor TERRACOTTA_LIME_HIGH = new CanvasColor(MapColor.TERRACOTTA_LIME, MapColor.Brightness.HIGH);
    public static final CanvasColor TERRACOTTA_PINK_LOWEST = new CanvasColor(MapColor.TERRACOTTA_PINK, MapColor.Brightness.LOWEST);
    public static final CanvasColor TERRACOTTA_PINK_LOW = new CanvasColor(MapColor.TERRACOTTA_PINK, MapColor.Brightness.LOW);
    public static final CanvasColor TERRACOTTA_PINK_NORMAL = new CanvasColor(MapColor.TERRACOTTA_PINK, MapColor.Brightness.NORMAL);
    public static final CanvasColor TERRACOTTA_PINK_HIGH = new CanvasColor(MapColor.TERRACOTTA_PINK, MapColor.Brightness.HIGH);
    public static final CanvasColor TERRACOTTA_GRAY_LOWEST = new CanvasColor(MapColor.TERRACOTTA_GRAY, MapColor.Brightness.LOWEST);
    public static final CanvasColor TERRACOTTA_GRAY_LOW = new CanvasColor(MapColor.TERRACOTTA_GRAY, MapColor.Brightness.LOW);
    public static final CanvasColor TERRACOTTA_GRAY_NORMAL = new CanvasColor(MapColor.TERRACOTTA_GRAY, MapColor.Brightness.NORMAL);
    public static final CanvasColor TERRACOTTA_GRAY_HIGH = new CanvasColor(MapColor.TERRACOTTA_GRAY, MapColor.Brightness.HIGH);
    public static final CanvasColor TERRACOTTA_LIGHT_GRAY_LOWEST = new CanvasColor(MapColor.TERRACOTTA_LIGHT_GRAY, MapColor.Brightness.LOWEST);
    public static final CanvasColor TERRACOTTA_LIGHT_GRAY_LOW = new CanvasColor(MapColor.TERRACOTTA_LIGHT_GRAY, MapColor.Brightness.LOW);
    public static final CanvasColor TERRACOTTA_LIGHT_GRAY_NORMAL = new CanvasColor(MapColor.TERRACOTTA_LIGHT_GRAY, MapColor.Brightness.NORMAL);
    public static final CanvasColor TERRACOTTA_LIGHT_GRAY_HIGH = new CanvasColor(MapColor.TERRACOTTA_LIGHT_GRAY, MapColor.Brightness.HIGH);
    public static final CanvasColor TERRACOTTA_CYAN_LOWEST = new CanvasColor(MapColor.TERRACOTTA_CYAN, MapColor.Brightness.LOWEST);
    public static final CanvasColor TERRACOTTA_CYAN_LOW = new CanvasColor(MapColor.TERRACOTTA_CYAN, MapColor.Brightness.LOW);
    public static final CanvasColor TERRACOTTA_CYAN_NORMAL = new CanvasColor(MapColor.TERRACOTTA_CYAN, MapColor.Brightness.NORMAL);
    public static final CanvasColor TERRACOTTA_CYAN_HIGH = new CanvasColor(MapColor.TERRACOTTA_CYAN, MapColor.Brightness.HIGH);
    public static final CanvasColor TERRACOTTA_PURPLE_LOWEST = new CanvasColor(MapColor.TERRACOTTA_PURPLE, MapColor.Brightness.LOWEST);
    public static final CanvasColor TERRACOTTA_PURPLE_LOW = new CanvasColor(MapColor.TERRACOTTA_PURPLE, MapColor.Brightness.LOW);
    public static final CanvasColor TERRACOTTA_PURPLE_NORMAL = new CanvasColor(MapColor.TERRACOTTA_PURPLE, MapColor.Brightness.NORMAL);
    public static final CanvasColor TERRACOTTA_PURPLE_HIGH = new CanvasColor(MapColor.TERRACOTTA_PURPLE, MapColor.Brightness.HIGH);
    public static final CanvasColor TERRACOTTA_BLUE_LOWEST = new CanvasColor(MapColor.TERRACOTTA_BLUE, MapColor.Brightness.LOWEST);
    public static final CanvasColor TERRACOTTA_BLUE_LOW = new CanvasColor(MapColor.TERRACOTTA_BLUE, MapColor.Brightness.LOW);
    public static final CanvasColor TERRACOTTA_BLUE_NORMAL = new CanvasColor(MapColor.TERRACOTTA_BLUE, MapColor.Brightness.NORMAL);
    public static final CanvasColor TERRACOTTA_BLUE_HIGH = new CanvasColor(MapColor.TERRACOTTA_BLUE, MapColor.Brightness.HIGH);
    public static final CanvasColor TERRACOTTA_BROWN_LOWEST = new CanvasColor(MapColor.TERRACOTTA_BROWN, MapColor.Brightness.LOWEST);
    public static final CanvasColor TERRACOTTA_BROWN_LOW = new CanvasColor(MapColor.TERRACOTTA_BROWN, MapColor.Brightness.LOW);
    public static final CanvasColor TERRACOTTA_BROWN_NORMAL = new CanvasColor(MapColor.TERRACOTTA_BROWN, MapColor.Brightness.NORMAL);
    public static final CanvasColor TERRACOTTA_BROWN_HIGH = new CanvasColor(MapColor.TERRACOTTA_BROWN, MapColor.Brightness.HIGH);
    public static final CanvasColor TERRACOTTA_GREEN_LOWEST = new CanvasColor(MapColor.TERRACOTTA_GREEN, MapColor.Brightness.LOWEST);
    public static final CanvasColor TERRACOTTA_GREEN_LOW = new CanvasColor(MapColor.TERRACOTTA_GREEN, MapColor.Brightness.LOW);
    public static final CanvasColor TERRACOTTA_GREEN_NORMAL = new CanvasColor(MapColor.TERRACOTTA_GREEN, MapColor.Brightness.NORMAL);
    public static final CanvasColor TERRACOTTA_GREEN_HIGH = new CanvasColor(MapColor.TERRACOTTA_GREEN, MapColor.Brightness.HIGH);
    public static final CanvasColor TERRACOTTA_RED_LOWEST = new CanvasColor(MapColor.TERRACOTTA_RED, MapColor.Brightness.LOWEST);
    public static final CanvasColor TERRACOTTA_RED_LOW = new CanvasColor(MapColor.TERRACOTTA_RED, MapColor.Brightness.LOW);
    public static final CanvasColor TERRACOTTA_RED_NORMAL = new CanvasColor(MapColor.TERRACOTTA_RED, MapColor.Brightness.NORMAL);
    public static final CanvasColor TERRACOTTA_RED_HIGH = new CanvasColor(MapColor.TERRACOTTA_RED, MapColor.Brightness.HIGH);
    public static final CanvasColor TERRACOTTA_BLACK_LOWEST = new CanvasColor(MapColor.TERRACOTTA_BLACK, MapColor.Brightness.LOWEST);
    public static final CanvasColor TERRACOTTA_BLACK_LOW = new CanvasColor(MapColor.TERRACOTTA_BLACK, MapColor.Brightness.LOW);
    public static final CanvasColor TERRACOTTA_BLACK_NORMAL = new CanvasColor(MapColor.TERRACOTTA_BLACK, MapColor.Brightness.NORMAL);
    public static final CanvasColor TERRACOTTA_BLACK_HIGH = new CanvasColor(MapColor.TERRACOTTA_BLACK, MapColor.Brightness.HIGH);
    public static final CanvasColor DULL_RED_LOWEST = new CanvasColor(MapColor.DULL_RED, MapColor.Brightness.LOWEST);
    public static final CanvasColor DULL_RED_LOW = new CanvasColor(MapColor.DULL_RED, MapColor.Brightness.LOW);
    public static final CanvasColor DULL_RED_NORMAL = new CanvasColor(MapColor.DULL_RED, MapColor.Brightness.NORMAL);
    public static final CanvasColor DULL_RED_HIGH = new CanvasColor(MapColor.DULL_RED, MapColor.Brightness.HIGH);
    public static final CanvasColor DULL_PINK_LOWEST = new CanvasColor(MapColor.DULL_PINK, MapColor.Brightness.LOWEST);
    public static final CanvasColor DULL_PINK_LOW = new CanvasColor(MapColor.DULL_PINK, MapColor.Brightness.LOW);
    public static final CanvasColor DULL_PINK_NORMAL = new CanvasColor(MapColor.DULL_PINK, MapColor.Brightness.NORMAL);
    public static final CanvasColor DULL_PINK_HIGH = new CanvasColor(MapColor.DULL_PINK, MapColor.Brightness.HIGH);
    public static final CanvasColor DARK_CRIMSON_LOWEST = new CanvasColor(MapColor.DARK_CRIMSON, MapColor.Brightness.LOWEST);
    public static final CanvasColor DARK_CRIMSON_LOW = new CanvasColor(MapColor.DARK_CRIMSON, MapColor.Brightness.LOW);
    public static final CanvasColor DARK_CRIMSON_NORMAL = new CanvasColor(MapColor.DARK_CRIMSON, MapColor.Brightness.NORMAL);
    public static final CanvasColor DARK_CRIMSON_HIGH = new CanvasColor(MapColor.DARK_CRIMSON, MapColor.Brightness.HIGH);
    public static final CanvasColor TEAL_LOWEST = new CanvasColor(MapColor.TEAL, MapColor.Brightness.LOWEST);
    public static final CanvasColor TEAL_LOW = new CanvasColor(MapColor.TEAL, MapColor.Brightness.LOW);
    public static final CanvasColor TEAL_NORMAL = new CanvasColor(MapColor.TEAL, MapColor.Brightness.NORMAL);
    public static final CanvasColor TEAL_HIGH = new CanvasColor(MapColor.TEAL, MapColor.Brightness.HIGH);
    public static final CanvasColor DARK_AQUA_LOWEST = new CanvasColor(MapColor.DARK_AQUA, MapColor.Brightness.LOWEST);
    public static final CanvasColor DARK_AQUA_LOW = new CanvasColor(MapColor.DARK_AQUA, MapColor.Brightness.LOW);
    public static final CanvasColor DARK_AQUA_NORMAL = new CanvasColor(MapColor.DARK_AQUA, MapColor.Brightness.NORMAL);
    public static final CanvasColor DARK_AQUA_HIGH = new CanvasColor(MapColor.DARK_AQUA, MapColor.Brightness.HIGH);
    public static final CanvasColor DARK_DULL_PINK_LOWEST = new CanvasColor(MapColor.DARK_DULL_PINK, MapColor.Brightness.LOWEST);
    public static final CanvasColor DARK_DULL_PINK_LOW = new CanvasColor(MapColor.DARK_DULL_PINK, MapColor.Brightness.LOW);
    public static final CanvasColor DARK_DULL_PINK_NORMAL = new CanvasColor(MapColor.DARK_DULL_PINK, MapColor.Brightness.NORMAL);
    public static final CanvasColor DARK_DULL_PINK_HIGH = new CanvasColor(MapColor.DARK_DULL_PINK, MapColor.Brightness.HIGH);
    public static final CanvasColor BRIGHT_TEAL_LOWEST = new CanvasColor(MapColor.BRIGHT_TEAL, MapColor.Brightness.LOWEST);
    public static final CanvasColor BRIGHT_TEAL_LOW = new CanvasColor(MapColor.BRIGHT_TEAL, MapColor.Brightness.LOW);
    public static final CanvasColor BRIGHT_TEAL_NORMAL = new CanvasColor(MapColor.BRIGHT_TEAL, MapColor.Brightness.NORMAL);
    public static final CanvasColor BRIGHT_TEAL_HIGH = new CanvasColor(MapColor.BRIGHT_TEAL, MapColor.Brightness.HIGH);
    public static final CanvasColor DEEPSLATE_GRAY_LOWEST = new CanvasColor(MapColor.DEEPSLATE_GRAY, MapColor.Brightness.LOWEST);
    public static final CanvasColor DEEPSLATE_GRAY_LOW = new CanvasColor(MapColor.DEEPSLATE_GRAY, MapColor.Brightness.LOW);
    public static final CanvasColor DEEPSLATE_GRAY_NORMAL = new CanvasColor(MapColor.DEEPSLATE_GRAY, MapColor.Brightness.NORMAL);
    public static final CanvasColor DEEPSLATE_GRAY_HIGH = new CanvasColor(MapColor.DEEPSLATE_GRAY, MapColor.Brightness.HIGH);
    public static final CanvasColor RAW_IRON_PINK_LOWEST = new CanvasColor(MapColor.RAW_IRON_PINK, MapColor.Brightness.LOWEST);
    public static final CanvasColor RAW_IRON_PINK_LOW = new CanvasColor(MapColor.RAW_IRON_PINK, MapColor.Brightness.LOW);
    public static final CanvasColor RAW_IRON_PINK_NORMAL = new CanvasColor(MapColor.RAW_IRON_PINK, MapColor.Brightness.NORMAL);
    public static final CanvasColor RAW_IRON_PINK_HIGH = new CanvasColor(MapColor.RAW_IRON_PINK, MapColor.Brightness.HIGH);
    public static final CanvasColor LICHEN_GREEN_LOWEST = new CanvasColor(MapColor.LICHEN_GREEN, MapColor.Brightness.LOWEST);
    public static final CanvasColor LICHEN_GREEN_LOW = new CanvasColor(MapColor.LICHEN_GREEN, MapColor.Brightness.LOW);
    public static final CanvasColor LICHEN_GREEN_NORMAL = new CanvasColor(MapColor.LICHEN_GREEN, MapColor.Brightness.NORMAL);
    public static final CanvasColor LICHEN_GREEN_HIGH = new CanvasColor(MapColor.LICHEN_GREEN, MapColor.Brightness.HIGH);

    protected final MapColor color;
    protected final MapColor.Brightness brightness;
    protected final byte renderColor;
    protected final int rgbColor;

    private CanvasColor(MapColor color, MapColor.Brightness brightness) {
        this.color = color;
        this.brightness = brightness;
        this.renderColor = color.getRenderColorByte(this.brightness);
        this.rgbColor = color.getRenderColor(brightness);

        BY_RENDER_COLOR[Byte.toUnsignedInt(this.renderColor)] = this;
    }

    public static CanvasColor[] values() {
        return BY_RENDER_COLOR;
    }

    public MapColor getColor() {
        return this.color;
    }

    public MapColor.Brightness getBrightness() {
        return this.brightness;
    }

    public byte getRenderColor() {
        return this.renderColor;
    }

    public int getRgbColor() { return this.rgbColor; }

    public static CanvasColor getFromRaw(byte renderColor) {
        return BY_RENDER_COLOR[Byte.toUnsignedInt(renderColor)];
    }

    public static CanvasColor from(MapColor color, MapColor.Brightness brightness) {
        return BY_RENDER_COLOR[Byte.toUnsignedInt(color.getRenderColorByte(brightness))];
    }

    static {
        for (int i = 0; i < BY_RENDER_COLOR.length; i++) {
            if (BY_RENDER_COLOR[i] == null) {
                BY_RENDER_COLOR[i] = CLEAR;
            }
        }
    }
}
