package info.androidhive.recyclerview;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by midhun on 5/13/17.
 */

public class PlaceDBHelper extends SQLiteOpenHelper{

    private Context mContext;
    public static final String DAtABASE_NAME = "placeName.db";
    private static final int DATABASE_VERSION = 1;

    private static OnDatabaseChangedListener mOnDatabaseChangedListener;

    public static abstract class PlaceDBHelperItem implements BaseColumns{

        public static final String TABLE_NAME = "placeName";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_PLACENAME = "pname";
        public static final String COLUMN_NAME_IMAGEPATH = "imagePath";

    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String REAL_TYPE = " REAL";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + PlaceDBHelperItem.TABLE_NAME + "("+
                    PlaceDBHelperItem._ID + INT_TYPE+" PRIMARY KEY" + COMMA_SEP +
                    PlaceDBHelperItem.COLUMN_NAME_PLACENAME + TEXT_TYPE + COMMA_SEP +
                    PlaceDBHelperItem.COLUMN_NAME_IMAGEPATH + TEXT_TYPE + COMMA_SEP +
                    PlaceDBHelperItem.COLUMN_NAME_LATITUDE + REAL_TYPE + COMMA_SEP +
                    PlaceDBHelperItem.COLUMN_NAME_LONGITUDE + REAL_TYPE + ");";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + PlaceDBHelperItem.TABLE_NAME;



    public PlaceDBHelper(Context context) {
        super(context,DAtABASE_NAME,null,DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);

    }

    public static void setOnDatabaseChangedListener(OnDatabaseChangedListener listener) {
        mOnDatabaseChangedListener = listener;
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public int getCount(){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {PlaceDBHelperItem._ID};
        Cursor c = db.query(PlaceDBHelperItem.TABLE_NAME,projection,null,null,null,null,null);
        int count = c.getCount();
        return count;
    }

    public Context getContext(){ return mContext;}

    public Place getItemAt(int position){

        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                PlaceDBHelperItem._ID,
                PlaceDBHelperItem.COLUMN_NAME_PLACENAME,
                PlaceDBHelperItem.COLUMN_NAME_IMAGEPATH,
                PlaceDBHelperItem.COLUMN_NAME_LATITUDE,
                PlaceDBHelperItem.COLUMN_NAME_LONGITUDE
        };

        Cursor c = db.query(PlaceDBHelperItem.TABLE_NAME,projection,null,null,null,null,null);
        if(c.moveToPosition(position)){
            Place p = new Place();
            p.setId(c.getInt(c.getColumnIndex(PlaceDBHelperItem._ID)));
            p.setName(c.getString(c.getColumnIndex(PlaceDBHelperItem.COLUMN_NAME_PLACENAME)));
            p.setLatitude(c.getDouble(c.getColumnIndex(PlaceDBHelperItem.COLUMN_NAME_LATITUDE)));
            p.setLongitude(c.getDouble(c.getColumnIndex(PlaceDBHelperItem.COLUMN_NAME_LONGITUDE)));
            p.setImagePath(c.getString(c.getColumnIndex(PlaceDBHelperItem.COLUMN_NAME_IMAGEPATH)));
            c.close();
            return p;
        }
        return null;

    }

    public void removeItemWithId(int id){
        Log.v("DB id",""+(id-1));
        SQLiteDatabase db = getWritableDatabase();
        String[] whereArgs = {String.valueOf(id)};
        db.delete(PlaceDBHelperItem.TABLE_NAME,PlaceDBHelperItem._ID+"=?", whereArgs);
    }

    public long addPlace(String placeName,double latitude,double longitude, String imagePath){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(PlaceDBHelperItem.COLUMN_NAME_PLACENAME,placeName);
        cv.put(PlaceDBHelperItem.COLUMN_NAME_LATITUDE,latitude);
        cv.put(PlaceDBHelperItem.COLUMN_NAME_LONGITUDE,longitude);
        cv.put(PlaceDBHelperItem.COLUMN_NAME_IMAGEPATH,imagePath);
        long rowID = db.insert(PlaceDBHelperItem.TABLE_NAME,null,cv);

        if(mOnDatabaseChangedListener !=null){
            mOnDatabaseChangedListener.onNewDatabaseEntryAdded();
        }

        return rowID;

    }

    public void renameItem(Place place, String newName){

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(PlaceDBHelperItem.COLUMN_NAME_PLACENAME,newName);
        db.update(PlaceDBHelperItem.TABLE_NAME,cv,PlaceDBHelperItem._ID+ "=" + place.getId(),null);
        if(mOnDatabaseChangedListener!=null){
            mOnDatabaseChangedListener.onDatabaseEntryRenamed();
        }
    }

    public void changeImage(Place place, String newImagePath){

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(PlaceDBHelperItem.COLUMN_NAME_IMAGEPATH,newImagePath);
        db.update(PlaceDBHelperItem.TABLE_NAME,cv,PlaceDBHelperItem._ID+ "=" + place.getId(),null);
        if(mOnDatabaseChangedListener!=null){
            mOnDatabaseChangedListener.onDatabaseImageChanged();
        }
    }
}
