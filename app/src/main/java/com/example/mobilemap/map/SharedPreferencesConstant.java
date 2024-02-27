package com.example.mobilemap.map;

/**
 * Classe avec constantes utilisiées pour les préférences de la carte
 *
 * @author J.Houdé
 */
public final class SharedPreferencesConstant {
    // Pour le stockage des préférences
    public static final String PREFS_NAME = "com.exemple.mobileMap";
    public static final String PREFS_TILE_SOURCE = "tilesource";
    public static final String PREFS_LATITUDE_STRING = "latitudeString";
    public static final String PREFS_LONGITUDE_STRING = "longitudeString";
    public static final String PREFS_ORIENTATION = "orientation";
    public static final String PREFS_ZOOM_LEVEL_DOUBLE = "zoomLevelDouble";

    // Pour le stockage le circle actuellement affiché
    public static final String CIRCLE_IS_AROUND_ME = "circleAroundMe";
    public static final String CIRCLE_LATITUDE_STRING = "circleLatitudeString";
    public static final String CIRCLE_LONGITUDE_STRING = "circleLongitudeString";
    public static final String CIRCLE_RADIUS_STRING = "cicleRadiusString";
    public static final String CIRCLE_CATEGORY_FILTER = "cicleCategoryFilter";

    // valeurs par défaut
    public static final int NOT_FOUND_ID = -1;
    public static final String EMPTY_STRING = "";
    public static final String DEFAULT_POSITION_STRING = "0.0";
    public final static float DEFAULT_ZOOM = 13.5F;
    public final static String DEFAULT_LATITUDE = "49.109523";
    public final static String DEFAULT_LONGITUDE = "6.1768191";
}
