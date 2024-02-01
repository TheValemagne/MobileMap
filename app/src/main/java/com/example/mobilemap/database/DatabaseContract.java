package com.example.mobilemap.database;


import android.net.Uri;
import android.provider.BaseColumns;

public final class DatabaseContract {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "metzMap";

    private DatabaseContract() {}
    public static final String AUTHORITY = String.format("%s.provider", DATABASE_NAME);
    private static final Uri.Builder builder;
    static
    {
        builder = new Uri.Builder();
        builder.scheme("content");
        builder.authority(AUTHORITY);
    }
    public static final Uri CONTENT_URI = builder.build();

    public static class Poi implements BaseColumns {
        private static final Uri.Builder builder;
        public static final String TABLE_NAME = "poi";
        static {
            builder = DatabaseContract.CONTENT_URI.buildUpon();
            builder.path(Poi.TABLE_NAME);
        }

        public static final Uri CONTENT_URI = builder.build();
        /**
         * column name : text
         */
        public static final String COLUMN_NAME = "name";
        /**
         * column latitude : real (double)
         */
        public static final String COLUMN_LATITUDE = "latitude";
        /**
         * column longitude : real (double)
         */
        public static final String COLUMN_LONGITUDE = "longitude";
        /**
         * column postal address : text
         */
        public static final String COLUMN_POSTAL_ADDRESS = "postalAddress";
        /**
         * column categoryId : integer (long)
         */
        public static final String COLUMN_CATEGORY_ID = "categoryId";
        /**
         * column resume : text
         */
        public static final String COLUMN_RESUME = "resume";
        public static final String[] COLUMNS = new String[] {_ID, COLUMN_NAME, COLUMN_LATITUDE,
                COLUMN_LONGITUDE, COLUMN_POSTAL_ADDRESS, COLUMN_CATEGORY_ID, COLUMN_RESUME};
    }

    public static class Category implements BaseColumns {
        private static final Uri.Builder builder;
        public static final String TABLE_NAME = "category";
        static {
            builder = DatabaseContract.CONTENT_URI.buildUpon();
            builder.path(Category.TABLE_NAME);
        }

        public static final Uri CONTENT_URI = builder.build();
        /**
         * column name : text
         */
        public static final String COLUMN_NAME = "name";
        public static final String[] COLUMNS = new String[] {_ID, COLUMN_NAME};
    }

    public static class PoiDetail implements BaseColumns {
        private static final Uri.Builder builder;
        public static final String TABLE_NAME = "poi_detail";
        static {
            builder = DatabaseContract.CONTENT_URI.buildUpon();
            builder.path(PoiDetail.TABLE_NAME);
        }

        public static final Uri CONTENT_URI = builder.build();
        /**
         * column name : text
         */
        public static final String COLUMN_SITE_NAME = "site_name";
        public static final String COLUMN_CATEGORY_NAME = "category_name";
        public static final String[] COLUMNS = new String[] {_ID, COLUMN_SITE_NAME, COLUMN_CATEGORY_NAME};
    }
}
