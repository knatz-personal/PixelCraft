package com.pixelcraft.util;

import java.util.HashMap;
import java.util.Map;

import com.pixelcraft.util.logging.Logger;
import com.pixelcraft.util.logging.LoggerFactory;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

/**
 * Generic utility class for working with icon fonts in JavaFX.
 * Supports any icon font by configuring the font path and Unicode mappings.
 * 
 * <p><b>To use a different icon font:</b></p>
 * <ol>
 *   <li>Place your .ttf font file in the resources folder</li>
 *   <li>Update {@link #DEFAULT_FONT_PATH} to point to your font file</li>
 *   <li>Update the Unicode constants below with values from your font's CSS/documentation</li>
 *   <li>Optionally call {@link #setFontPath(String)} before first use to override the default</li>
 * </ol>
 * 
 * <p><b>Example:</b></p>
 * <pre>{@code
 * // Use default font (configured below)
 * IconUtil.loadIconFont();
 * 
 * // Or use a custom font
 * IconUtil.setFontPath("/fonts/my-custom-icons.ttf");
 * IconUtil.loadIconFont();
 * }</pre>
 */
public class IconUtil {
    
    // ==================== CONFIGURATION SECTION ====================
    // Change these values to use a different icon font
    
    /**
     * Default path to the icon font file in resources.
     * <p><b>CHANGE THIS</b> to use a different icon font.</p>
     * <p>Examples:</p>
     * <ul>
     *   <li>Linearicons: {@code "/icons/Linearicons.ttf"}</li>
     *   <li>Themify Icons: {@code "/icons/themify.ttf"}</li>
     *   <li>Font Awesome: {@code "/fonts/fontawesome.ttf"}</li>
     *   <li>Material Icons: {@code "/fonts/MaterialIcons-Regular.ttf"}</li>
     *   <li>Material Symbols Outlined: {@code "/icons/MaterialSymbolsOutlined.ttf"}</li>
     * </ul>
     */
    private static final String DEFAULT_FONT_PATH = "/icons/MaterialSymbolsOutlined.ttf";
    
    /**
     * Default icon size in pixels.
     * <p><b>CHANGE THIS</b> to adjust the default icon size.</p>
     */
    private static final double DEFAULT_ICON_SIZE = 16.0;
    
    // ==================== ICON NAME TO UNICODE MAPPINGS ====================
    // Material Symbols Outlined icon names mapped to their Unicode values
    // Use these human-readable names instead of Unicode values
    
    private static final Map<String, String> ICON_MAP = new HashMap<>();
    
    static {
        // https://fonts.google.com/icons?selected=Material+Symbols+Outlined:save:FILL@0;wght@400;GRAD@0;opsz@24&icon.query=save&icon.size=48&icon.color=%23e3e3e3&icon.platform=web
        // File & Document icons
        ICON_MAP.put("file-empty", "\ue24d");      // description
        ICON_MAP.put("file-add", "\ue89c");        // note_add
        ICON_MAP.put("save", "\ue161");
        
        // Action icons
        ICON_MAP.put("undo", "\ue166");            // undo
        ICON_MAP.put("redo", "\ue15a");            // redo
        ICON_MAP.put("sync", "\ue627");            // sync
        ICON_MAP.put("history", "\ue889");         // history
        ICON_MAP.put("download", "\uf090");        // download
        ICON_MAP.put("upload", "\uf09b");          // upload
        
        // Navigation icons
        ICON_MAP.put("arrow-up", "\ue5c7");        // arrow_upward
        ICON_MAP.put("arrow-down", "\ue5db");      // arrow_downward
        ICON_MAP.put("arrow-left", "\ue5c4");      // arrow_back
        ICON_MAP.put("arrow-right", "\ue5c8");     // arrow_forward
        ICON_MAP.put("chevron-up", "\ue5ce");      // expand_less
        ICON_MAP.put("chevron-down", "\ue5cf");    // expand_more
        ICON_MAP.put("chevron-left", "\ue5cb");    // chevron_left
        ICON_MAP.put("chevron-right", "\ue5cc");   // chevron_right
        
        // UI icons
        ICON_MAP.put("magnifier", "\ue8b6");       // search
        ICON_MAP.put("cross", "\ue5cd");           // close
        ICON_MAP.put("menu", "\ue5d2");            // menu
        ICON_MAP.put("list", "\ue896");            // list
        
        // Common icons
        ICON_MAP.put("home", "\ue88a");            // home
        ICON_MAP.put("pencil", "\ue3c9");          // edit
        ICON_MAP.put("trash", "\ue872");           // delete
        ICON_MAP.put("heart", "\ue87d");           // favorite
        ICON_MAP.put("star", "\ue838");            // star
        ICON_MAP.put("cog", "\ue8b8");             // settings
        ICON_MAP.put("lock", "\ue897");            // lock
        ICON_MAP.put("eye", "\ue8f4");             // visibility
        ICON_MAP.put("printer", "\ue8ad");         // print
        ICON_MAP.put("camera", "\ue3af");          // photo_camera
        ICON_MAP.put("picture", "\ue3f4");         // image
        ICON_MAP.put("book", "\ue865");            // book
        ICON_MAP.put("bookmark", "\ue866");        // bookmark
        ICON_MAP.put("user", "\ue7fd");            // person
        ICON_MAP.put("users", "\ue7fb");           // group
        ICON_MAP.put("spell-check", "\ue8ce");     // spellcheck
        ICON_MAP.put("arrow_selector_tool", "\uf82f");  // arrow_selector_tool (Material Symbols)
        ICON_MAP.put("image", "\ue3f4"); 
    }
    
    private static final Logger LOG = LoggerFactory.getLogger(IconUtil.class);
    
    private static Font iconFont;
    private static boolean fontLoaded = false;
    private static String customFontPath = null;
        
    /**
     * Sets a custom font path to use instead of the default.
     * Must be called before {@link #loadIconFont()} to take effect.
     * 
     * @param fontPath the resource path to the icon font file (e.g., "/fonts/my-icons.ttf")
     */
    public static void setFontPath(String fontPath) {
        if (fontLoaded) {
            LOG.warn("Font already loaded. setFontPath() has no effect.");
            return;
        }
        customFontPath = fontPath;
    }
    
    /**
     * Gets the currently configured font path (custom or default).
     * 
     * @return the font path that will be used
     */
    public static String getFontPath() {
        return customFontPath != null ? customFontPath : DEFAULT_FONT_PATH;
    }
    
    /**
     * Loads the icon font from resources.
     * Should be called once during application initialization.
     * Uses the custom font path if set via {@link #setFontPath(String)}, 
     * otherwise uses {@link #DEFAULT_FONT_PATH}.
     * 
     * @return true if font loaded successfully, false otherwise
     */
    public static boolean loadIconFont() {
        if (fontLoaded) {
            return true;
        }
        
        String fontPath = getFontPath();
        
        try {
            var fontStream = IconUtil.class.getResourceAsStream(fontPath);
            
            if (fontStream == null) {
                LOG.error("Could not find icon font file at: " + fontPath);
                LOG.error("Make sure the font file exists in resources at the specified path.");
                return false;
            }
            
            iconFont = Font.loadFont(fontStream, DEFAULT_ICON_SIZE);
            fontLoaded = iconFont != null;
            
            if (fontLoaded) {
                LOG.info("Icon font loaded successfully from: " + fontPath);
                LOG.info("Font family: " + iconFont.getFamily());
            } else {
                LOG.error("Failed to load icon font from: " + fontPath);
            }
            
            return fontLoaded;
        } catch (Exception e) {
            LOG.error("Error loading icon font from " + fontPath + ": " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Resets the font loading state, allowing a different font to be loaded.
     * Useful for testing or switching fonts at runtime.
     */
    public static void resetFont() {
        iconFont = null;
        fontLoaded = false;
        customFontPath = null;
    }
    
    /**
     * Creates a Label with an icon using a human-readable icon name.
     * This is the preferred method for creating icons as names are more maintainable.
     * 
     * <p><b>Example:</b></p>
     * <pre>{@code
     * Label undoIcon = IconUtil.createIconByName("lnr-undo");
     * Label redoIcon = IconUtil.createIconByName("lnr-redo", 32);
     * }</pre>
     * 
     * @param iconName the icon name (e.g., "lnr-undo", "lnr-redo", "lnr-magnifier")
     * @param size the font size for the icon in pixels
     * @return a Label containing the icon
     * @throws IllegalArgumentException if the icon name is not found
     */
    public static Label createIconByName(String iconName, double size) {
        String unicode = ICON_MAP.get(iconName);
        if (unicode == null) {
            throw new IllegalArgumentException("Icon name not found: " + iconName + ". Available icons: " + ICON_MAP.keySet());
        }
        return createIcon(unicode, size);
    }
    
    /**
     * Creates a Label with an icon using a human-readable icon name with default size.
     * 
     * @param iconName the icon name (e.g., "lnr-undo", "lnr-redo", "lnr-magnifier")
     * @return a Label containing the icon
     * @throws IllegalArgumentException if the icon name is not found
     */
    public static Label createIconByName(String iconName) {
        return createIconByName(iconName, DEFAULT_ICON_SIZE);
    }
    
    /**
     * Creates a Label with an icon from the icon font using Unicode.
     * For better readability, prefer using {@link #createIconByName(String, double)} instead.
     * 
     * @param unicode the Unicode character for the icon
     * @param size the font size for the icon in pixels
     * @return a Label containing the icon
     */
    public static Label createIcon(String unicode, double size) {
        if (!fontLoaded) {
            loadIconFont();
        }
        
        Label icon = new Label(unicode);
        icon.setPadding(new javafx.geometry.Insets(0));
        icon.setAlignment(Pos.BOTTOM_LEFT);
        if (iconFont != null) {
            icon.setFont(Font.font(iconFont.getFamily(), size));
        } else {
            // Fallback to system font if icon font failed to load
            icon.setFont(Font.font("System", size));
            LOG.warn("Icon font not loaded, using system font as fallback");
        }
        
        icon.getStyleClass().add("icon-label");
        return icon;
    }
    
    /**
     * Creates a Label with an icon using the default size.
     * 
     * @param unicode the Unicode character for the icon
     * @return a Label containing the icon
     */
    
    public static Label createIcon(String unicode) {
        return createIcon(unicode, DEFAULT_ICON_SIZE);
    }    
}
