package com.uya.drawyerlayout; /**
 * Created by root on 14/03/18.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Created by root on 08/03/18.
 */

public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME="dbkamus";
    public static final String INDONESIA = "indonesia";
    public static final String KAILI = "kaili";
    private Context Currentcontext;
    private Context CurrentBaseContext;

    public DatabaseHelper(Context context, Context baseContext ) {
        super(context, DATABASE_NAME, null, 1);
        Currentcontext = context;
        CurrentBaseContext = baseContext;
    }

    public void createTable(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS kamus");
        db.execSQL("CREATE TABLE if not exists kamus (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + "indonesia TEXT, kaili TEXT);");

    }

    public void urutData(SQLiteDatabase db) {
        Cursor kamusCursor;
        kamusCursor = db.rawQuery("SELECT * FROM kamus ORDER BY indonesia ASC", null);
        String[][] array = new String[kamusCursor.getCount()][3]; // Dynamic string array
        int i = 0;
        if (kamusCursor.moveToFirst()) {
            for (; !kamusCursor.isAfterLast(); kamusCursor.moveToNext()) {
                array[i][0] = kamusCursor.getString(0);
                array[i][1] = kamusCursor.getString(1);
                array[i][2] = kamusCursor.getString(2);
                i++;
            }
        }else{
            Toast.makeText(CurrentBaseContext, "Data Tidak Ada",
                    Toast.LENGTH_SHORT).show();
        }


        kamusCursor = db.rawQuery("SELECT * FROM kamus", null);
        ContentValues values = new ContentValues(2);
        i = 0;
        if (kamusCursor.moveToFirst()) {
            for (; !kamusCursor.isAfterLast(); kamusCursor.moveToNext()) {
                values.put("indonesia", array[i][1]);
                values.put("kaili", array[i][2]);
                String[] args = {String.valueOf(kamusCursor.getString(0))};
                db.update("kamus", values, "_id=?", args);
                i++;

            }

            kamusCursor.requery();
            Toast.makeText(CurrentBaseContext, "Urut Data Berhasil", Toast.LENGTH_SHORT).show();
        }
    }


    public void generateData(SQLiteDatabase db){
        String csvFile = "/home/subhandp/KAMUS_ANDROID/dataKamus.csv";

        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        Log.i("CSVTEST","MULAI");
        ContentValues cv = new ContentValues();
        try {

            //br = new BufferedReader(new FileReader(csvFile));

            InputStream is = Currentcontext.getResources().openRawResource(R.raw.datakamus);
            br = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8")));

            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] dataKamus = line.split(cvsSplitBy);

                cv.put(INDONESIA, dataKamus[0].toLowerCase());
                cv.put(KAILI, dataKamus[1].toLowerCase());
                db.insert("kamus", null, cv);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("CSVTEST","ERROR OPEN");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("CSVTEST","ERROR OPEN");
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("CSVTEST","ERROR OPEN");
                }
            }
        }

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createTable(sqLiteDatabase);
        generateData(sqLiteDatabase);
        urutData(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        createTable(sqLiteDatabase);
        generateData(sqLiteDatabase);
        urutData(sqLiteDatabase);
    }
}

