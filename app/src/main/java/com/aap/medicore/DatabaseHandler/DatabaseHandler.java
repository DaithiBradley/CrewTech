package com.aap.medicore.DatabaseHandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import com.aap.medicore.Models.AssignedIncidencesModel;
import com.aap.medicore.Models.DBAdminFormModel;
import com.aap.medicore.Models.DBImagesModel;
import com.aap.medicore.Models.InboxMessage;
import com.aap.medicore.Models.QueueModel;
import com.aap.medicore.Models.SelectImagesModel;
import com.aap.medicore.Models.TabsModel;

import java.io.File;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Cool Programmer on 6/19/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 12;

    // Database Name
    private static final String DATABASE_NAME = "Medicore";

    // table name
    private static final String TABLE_ASSIGNED_INCIDENCES = "assigned_incidences";

    //columns
    private static final String KEY_ASSIGNED_INCIDENCES_ID = "assigned_incidence_id";
    private static final String KEY_ASSIGNED_INCIDENCES_JSON = "assigned_incidence_json";
    private static final String KEY_ASSIGNED_INCIDENCE_DATE = "assigned_incidence_date";
    private static final String KEY_ASSIGNED_INCIDENCE_TIME = "assigned_incidence_time";

    // table name
    private static final String TABLE_QUEUED_INCIDENCES = "queued_incidences";
    //columns
    private static final String KEY_QUEUED_INCIDENCES_ID = "queued_incidence_id";
    private static final String KEY_QUEUED_INCIDENCES_JSON = "queued_incidence_json";
    private static final String KEY_QUEUED_INCIDENCES_STATE = "queued_incidence_state";
    private static final String KEY_QUEUED_INCIDENCES_TITLE = "queued_incidence_title";
    private static final String KEY_QUEUED_INCIDENCES_MESSAGE = "queued_incidence_message";


    // table name
    private static final String TABLE_draft_INCIDENCES = "draft_incidences";
    //columns
    private static final String KEY_draft_INCIDENCES_ID = "draft_incidence_id";
    private static final String KEY_draft_INCIDENCES_JSON = "draft_incidence_json";
    private static final String KEY_draft_INCIDENCES_STATE = "draft_incidence_state";
    private static final String KEY_draft_INCIDENCES_TITLE = "draft_incidence_title";
    private static final String KEY_draft_INCIDENCES_MESSAGE = "draft_incidence_message";
    private static final String KEY_draft_Task_INCIDENCES_ID = "draft_incidence_task_id";


    // table name
    private static final String TABS_TABLE = "tabs_table";
    //columns
    private static final String KEY_TAB_TITLE = "title";
    private static final String KEY_TAB_JSON = "json";
    private static final String KEY_TAB_ID = "tab_id";
    private static final String KEY_TAB_TASK_ID = "taskId";

    // table name
    private static final String TABLE_TABS_IMAGES = "tab_saved_images";

    //columns
    private static final String KEY_TABID = "tabId";
    private static final String KEY_TAB_IMAGE_URI = "image_uri";
    private static final String KEY_TAB_IMAGE_TASKID = "taskid";
    private static final String KEY_TAB_FINAL_IMAGE = "imgfile";
    private static final String KEY_TAB_IMAGE_NAME = "img_name";


    // table name
    private static final String TABLE_QUEUED_IMAGES = "queued_images";

    //columns
    private static final String KEY_QUEUED_IMAGE_ID = "queued_image_id";
    private static final String KEY_QUEUED_IMAGE_INCIDENCE_ID = "queued_image_incidence_id";
    private static final String KEY_QUEUED_IMAGE_URI = "queued_image_uri";
    private static final String KEY_NUM_FORMS_SUBMITTED = "number_of_forms_submitted";
    private static final String KEY_QUEUED_IMAGE_NAME = "queued_image_name";

    //table name
    public static final String TABLE_ADMIN_FORMS = "admin_forms_table";

    //coloumn
    public static final String KEY_PRIMARY_ID = "primary_id";
    public static final String KEY_ADMIN_FORM_ID = "admin_form_id";
    public static final String KEY_ADMIN_FORM_FIELDS_JSON = "admin_form_fields_json";
    public static final String KEY_ADMIN_FORM_TITLE = "admin_form_title";
    public static final String KEY_ADMIN_FORM_SUB_TITLE = "admin_form_sub_title";
    public static final String KEY_ADMIN_FORM_IMAGES = "admin_form_image";
    public static final String KEY_ADMIN_FORM_TIMEOUT = "admin_form_timeout";

    //table name
    public static final String TABLE_INBOX_MESSAGES = "table_inbox_messages";

    //coloumn
    public static final String KEY_MESSAGE_ID = "message_id";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_MESSAGE_TITLE = "message_title";
    public static final String KEY_MESSAGE_CONTENT = "message_content";
    public static final String KEY_SENT_TIME = "sent_time";
    public static final String KEY_OPEN_TIME = "open_time";
    public static final String KEY_IS_OPENED = "is_opened";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
//        String CREATE_STREAKS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_STREAKS + "(" + KEY_STREAK_ID + " INTEGER," + KEY_USER_ID + " TEXT," + KEY_STREAK_PRODUCT_ID + " TEXT," + KEY_STREAK_DAY + " TEXT," + KEY_STREAK_MONTH + " TEXT," + KEY_STREAK_YEAR + " TEXT," + KEY_STREAK_COUNT + " TEXT," + KEY_STREAK_LS_COUNT + " TEXT,PRIMARY KEY(" + KEY_STREAK_ID + "," + KEY_USER_ID + "))";

        String CREATE_TABLE_ASSIGNED_INCIDENCES = "CREATE TABLE IF NOT EXISTS "
                + TABLE_ASSIGNED_INCIDENCES
                + "(" + KEY_ASSIGNED_INCIDENCES_ID + " INTEGER PRIMARY KEY,"
                + KEY_ASSIGNED_INCIDENCES_JSON + " TEXT,"
                + KEY_ASSIGNED_INCIDENCE_DATE + " TEXT,"
                + KEY_ASSIGNED_INCIDENCE_TIME + " TEXT"
                + ")";
//
        String CREATE_TABLE_QUEUED_INCIDENCES = "CREATE TABLE IF NOT EXISTS "
                + TABLE_QUEUED_INCIDENCES
                + "(" + KEY_QUEUED_INCIDENCES_ID + " INTEGER PRIMARY KEY,"
                + KEY_QUEUED_INCIDENCES_JSON + " TEXT,"
                + KEY_QUEUED_INCIDENCES_STATE + " TEXT,"
                + KEY_QUEUED_INCIDENCES_TITLE + " TEXT,"
                + KEY_QUEUED_INCIDENCES_MESSAGE + " TEXT,"
                + KEY_NUM_FORMS_SUBMITTED + " TEXT"
                + ")";
        String CREATE_TABLE_TABS = "CREATE TABLE IF NOT EXISTS "
                + TABS_TABLE
                + "(" + KEY_TAB_ID + " INTEGER ,"
                + KEY_TAB_JSON + " TEXT,"
                + KEY_TAB_TITLE + " TEXT,"
                + KEY_TAB_TASK_ID + " INTEGER,"
                + " PRIMARY KEY(" + KEY_TAB_ID + "," + KEY_TAB_TASK_ID + "));";

        String CREATE_TABLE_TABS_IMAGES = "CREATE TABLE IF NOT EXISTS "
                + TABLE_TABS_IMAGES
                + "(" + KEY_TABID + " INTEGER ,"
                + KEY_TAB_IMAGE_URI + " TEXT,"
                + KEY_TAB_IMAGE_TASKID + " INTEGER,"
                + KEY_TAB_FINAL_IMAGE + " TEXT,"
                + KEY_TAB_IMAGE_NAME + " TEXT,"
                + " PRIMARY KEY(" + KEY_TABID + "," + KEY_TAB_IMAGE_TASKID + "," + KEY_TAB_IMAGE_NAME + "));";


        String CREATE_TABLE_draft_INCIDENCES = "CREATE TABLE IF NOT EXISTS " +
                TABLE_draft_INCIDENCES + "("
                + KEY_draft_INCIDENCES_ID + " INTEGER,"
                + KEY_draft_INCIDENCES_JSON + " TEXT,"
                + KEY_draft_INCIDENCES_STATE + " TEXT,"
                + KEY_draft_INCIDENCES_TITLE + " TEXT,"
                + KEY_draft_INCIDENCES_MESSAGE + " TEXT,"
                + KEY_draft_Task_INCIDENCES_ID + " INTEGER,"
                + " PRIMARY KEY(" + KEY_draft_INCIDENCES_ID + "," + KEY_draft_Task_INCIDENCES_ID + "));";


        String CREATE_TABLE_QUEUED_IMAGES = "CREATE TABLE IF NOT EXISTS "
                + TABLE_QUEUED_IMAGES
                + "(" + KEY_QUEUED_IMAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_QUEUED_IMAGE_INCIDENCE_ID + " TEXT,"
                + KEY_QUEUED_IMAGE_URI + " TEXT,"
                + KEY_QUEUED_IMAGE_NAME + " TEXT"
                + ")";

        String CREATE_TABLE_ADMIN_FORMS = "CREATE TABLE IF NOT EXISTS "
                + TABLE_ADMIN_FORMS
                + "(" + KEY_ADMIN_FORM_ID + " INTEGER PRIMARY KEY, "
                + KEY_ADMIN_FORM_FIELDS_JSON + " TEXT,"
                + KEY_ADMIN_FORM_TITLE + " TEXT,"
                + KEY_ADMIN_FORM_SUB_TITLE + " TEXT,"
                + KEY_ADMIN_FORM_IMAGES + " TEXT,"
                + KEY_ADMIN_FORM_TIMEOUT + " TEXT"
                + ")";

        String CREATE_TABLE_MESSAGES = "CREATE TABLE IF NOT EXISTS "
                + TABLE_INBOX_MESSAGES
                + "(" + KEY_MESSAGE_ID + " TEXT PRIMARY KEY, "
                + KEY_USERNAME + " TEXT,"
                + KEY_MESSAGE_TITLE + " TEXT,"
                + KEY_MESSAGE_CONTENT + " TEXT,"
                + KEY_SENT_TIME + " INTEGER,"
                + KEY_OPEN_TIME + " INTEGER,"
                + KEY_IS_OPENED + " INTEGER"
                + ")";

        Log.e("Tag IncidencesTable", CREATE_TABLE_ASSIGNED_INCIDENCES);
        sqLiteDatabase.execSQL(CREATE_TABLE_ASSIGNED_INCIDENCES);

        Log.e("Tag QueuedIncidences", CREATE_TABLE_QUEUED_INCIDENCES);
        sqLiteDatabase.execSQL(CREATE_TABLE_QUEUED_INCIDENCES);

        Log.e("Tag DraftIncidences", CREATE_TABLE_draft_INCIDENCES);
        sqLiteDatabase.execSQL(CREATE_TABLE_draft_INCIDENCES);


        Log.e("Tag QueuedImages=>", CREATE_TABLE_QUEUED_IMAGES);
        sqLiteDatabase.execSQL(CREATE_TABLE_QUEUED_IMAGES);
        sqLiteDatabase.execSQL(CREATE_TABLE_TABS);
        sqLiteDatabase.execSQL(CREATE_TABLE_TABS_IMAGES);
        sqLiteDatabase.execSQL(CREATE_TABLE_ADMIN_FORMS);
        sqLiteDatabase.execSQL(CREATE_TABLE_MESSAGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSIGNED_INCIDENCES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_QUEUED_INCIDENCES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_draft_INCIDENCES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_QUEUED_IMAGES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABS_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TABS_IMAGES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ADMIN_FORMS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_INBOX_MESSAGES);

        // create new tables
        onCreate(sqLiteDatabase);
    }

     public void insertMessage(InboxMessage message) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_MESSAGE_ID, message.id);
        contentValues.put(KEY_USERNAME, message.username);
        contentValues.put(KEY_MESSAGE_TITLE, message.title);
        contentValues.put(KEY_MESSAGE_CONTENT, message.content);
        contentValues.put(KEY_SENT_TIME, message.sentTime);
        contentValues.put(KEY_OPEN_TIME, message.openTime);
        contentValues.put(KEY_IS_OPENED, message.isOpened ? 1 : 0);
        try {
            database.insertOrThrow(TABLE_INBOX_MESSAGES, null, contentValues);
            Log.d("MESSAGES_TEST","MessageInserted");
        } catch (Exception e) {
            Log.d("MESSAGES_TEST","MessageInsertFail");
            e.printStackTrace();
        }
        database.close();
    }

    public void removeMessage(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_INBOX_MESSAGES + " WHERE "
                + KEY_MESSAGE_ID + " = \"" + id+"\"");
        db.close();
    }

    public void updateMessage(InboxMessage message) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_OPEN_TIME, message.openTime);
        contentValues.put(KEY_IS_OPENED, message.isOpened ? 1 : 0);
        int i = database.update(TABLE_INBOX_MESSAGES, contentValues, "message_id = ?", new String[]{message.id});
        database.close();
    }

    public ArrayList<InboxMessage> getInboxMessagesForUser(String username) {
        ArrayList<InboxMessage> list = new ArrayList<InboxMessage>();
        String selectQuery = "select * from " + TABLE_INBOX_MESSAGES + " WHERE " + KEY_USERNAME + "='" + username + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                InboxMessage model = new InboxMessage();
                model.id = cursor.getString(0);
                model.username = cursor.getString(1);
                model.title = cursor.getString(2);
                model.content = cursor.getString(3);
                model.sentTime = cursor.getLong(4);
                model.openTime = cursor.getLong(5);
                model.isOpened = cursor.getInt(6) == 1;
                list.add(model);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public InboxMessage getInboxMessage(String id) {
        ArrayList<InboxMessage> list = new ArrayList<InboxMessage>();
        String selectQuery = "select * from " + TABLE_INBOX_MESSAGES + " WHERE " + KEY_MESSAGE_ID + "='" + id + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            InboxMessage model = new InboxMessage();
            model.id = cursor.getString(0);
            model.username = cursor.getString(1);
            model.title = cursor.getString(2);
            model.content = cursor.getString(3);
            model.openTime = cursor.getLong(4);
            model.sentTime = cursor.getLong(5);
            model.isOpened = cursor.getInt(6) == 1;
            return model;
        }
        return null;
    }

    public int hasUnreadMessages(String username) {
        ArrayList<InboxMessage> list = new ArrayList<InboxMessage>();
        String selectQuery = "select * from " + TABLE_INBOX_MESSAGES + " WHERE " + KEY_USERNAME + "='" + username + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int result = 0;
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getInt(6) != 1)
                    result++;
            } while (cursor.moveToNext());
        }
        return result;
    }

    public boolean checkAndRemoveMessagesExpiredBySentTime(String username, double hoursToExpired) {
        String selectQuery = "select * from " + TABLE_INBOX_MESSAGES + " WHERE " + KEY_USERNAME + "='" + username + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<String> toRemove = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                long now = System.currentTimeMillis();
                long sent = cursor.getLong(4);
                if ((now - sent) > hoursToExpired * 3600000l) {
                    toRemove.add(cursor.getString(0));
                }
            } while (cursor.moveToNext());
        }
        if (!toRemove.isEmpty()) {
            for (String id : toRemove) {
                removeMessage(id);
            }
            return true;
        }
        return false;
    }

    public boolean checkAndRemoveMessagesExpiredByOpenTime(String username, int hoursToExpired) {
        String selectQuery = "select * from " + TABLE_INBOX_MESSAGES + " WHERE " + KEY_USERNAME + "='" + username + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<String> toRemove = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                long now = System.currentTimeMillis();
                long sent = cursor.getLong(5);
                if ((now - sent) > hoursToExpired * 3600000l) {
                    toRemove.add(cursor.getString(0));
                }
            } while (cursor.moveToNext());
        }
        if (!toRemove.isEmpty()) {
            for (String id : toRemove) {
                removeMessage(id);
            }
            return true;
        }
        return false;
    }


    public void addAdminForm(DBAdminFormModel form) {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_ADMIN_FORM_ID, form.getForm_id());
        contentValues.put(KEY_ADMIN_FORM_FIELDS_JSON, form.getFieldsJson());
        contentValues.put(KEY_ADMIN_FORM_TITLE, form.getTitle());
        contentValues.put(KEY_ADMIN_FORM_SUB_TITLE, form.getSub_title());
        contentValues.put(KEY_ADMIN_FORM_IMAGES, form.getImages());
        contentValues.put(KEY_ADMIN_FORM_TIMEOUT, form.getTimeout());
        try {
            database.insertOrThrow(TABLE_ADMIN_FORMS, null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
        database.close();
    }

    public void updateAdminForm(int formId, String json, String images) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_ADMIN_FORM_FIELDS_JSON, json);
        contentValues.put(KEY_ADMIN_FORM_IMAGES, images);
        int i = database.update(TABLE_ADMIN_FORMS, contentValues, "admin_form_id = ?", new String[]{Integer.toString(formId)});
        database.close();
    }

    public DBAdminFormModel getAdminForm(int id) {
        DBAdminFormModel formModel = new DBAdminFormModel();
        SQLiteDatabase database = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_ADMIN_FORMS + " WHERE "
                + KEY_ADMIN_FORM_ID + " = " + id;

        Cursor cursor = database.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                formModel.setForm_id(cursor.getInt(0));
                formModel.setFieldsJson(cursor.getString(1));
                formModel.setTitle(cursor.getString(2));
                formModel.setSub_title(cursor.getString(3));
                formModel.setImages(cursor.getString(4));
                formModel.setTimeout(cursor.getString(5));
                // Adding contact to list
            } while (cursor.moveToNext());
        }
        return formModel;
    }

    public void removeAdminForm(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_ADMIN_FORMS + " WHERE "
                + KEY_ADMIN_FORM_ID + " = " + id);
        db.close();
    }

    public void addIncidences(AssignedIncidencesModel model) {

        if (getJobCountFromDB(model.getId() + "") == 0) {
            SQLiteDatabase database = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_ASSIGNED_INCIDENCES_ID, model.getId());
            contentValues.put(KEY_ASSIGNED_INCIDENCES_JSON, model.getJson());
            contentValues.put(KEY_ASSIGNED_INCIDENCE_DATE, model.getDate());
            contentValues.put(KEY_ASSIGNED_INCIDENCE_TIME, model.getJobDateTime());

            database.insert(TABLE_ASSIGNED_INCIDENCES, null, contentValues);
            database.close();

        } else {
//            Toast.makeText(MainA, "Data Already exists", Toast.LENGTH_SHORT).getDialog();
            Log.e("Fav Existance", "Already Exist");
        }
    }


    public void addTabs(TabsModel model, String taskID) {

        int count = geTabsCountFromDB(taskID, model.getTab_id());
        Log.d("COUNT", "tabs count is: " + count);


        if (count == 0) {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_TAB_ID, model.getTab_id());
            contentValues.put(KEY_TAB_JSON, model.getJsonData());
            contentValues.put(KEY_TAB_TASK_ID, taskID);
            contentValues.put(KEY_TAB_TITLE, model.getTitle());

            database.insert(TABS_TABLE, null, contentValues);
            database.close();

        } else {
//            Toast.makeText(MainA, "Data Already exists", Toast.LENGTH_SHORT).getDialog();
            updateTabDetail(model, taskID);
            Log.e("Fav Existance", "Already Exist");
        }
    }

    public void updateTabDetail(TabsModel model, String taskID) {


        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_TAB_ID, model.getTab_id());
        contentValues.put(KEY_TAB_JSON, model.getJsonData());
        contentValues.put(KEY_TAB_TASK_ID, taskID);
        contentValues.put(KEY_TAB_TITLE, model.getTitle());

        database.update(TABS_TABLE, contentValues, "taskId = ? and tab_id = ?", new String[]{taskID, model.getTab_id()});


    }

    public ArrayList<TabsModel> gettabDetail(String taskId, String tabId) {


        ArrayList<TabsModel> list = new ArrayList<TabsModel>();
        // Select All Query
//        String selectQuery = "SELECT  * FROM " + TABS_TABLE + " WHERE "
//                + KEY_TAB_TASK_ID + " = " + taskId;

        String selectQuery = "select * from " + TABS_TABLE + " WHERE " + KEY_TAB_ID + "=" + tabId + " and " +
                KEY_TAB_TASK_ID + "='" + taskId + "'";


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                TabsModel model = new TabsModel();

                model.setTab_id(String.valueOf(cursor.getInt(0)));
                model.setJsonData(cursor.getString(1));
                model.setTaskId(String.valueOf(cursor.getInt(2)));
                model.setTitle(String.valueOf(cursor.getInt(3)));


                // Adding contact to list
                list.add(model);
            } while (cursor.moveToNext());
        }

        // return contact list
        return list;
    }

    // Getting All Streaks items
    public ArrayList<AssignedIncidencesModel> getAllIncidences() {


        ArrayList<AssignedIncidencesModel> list = new ArrayList<AssignedIncidencesModel>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ASSIGNED_INCIDENCES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AssignedIncidencesModel model = new AssignedIncidencesModel();

                model.setId(cursor.getInt(0));
                model.setJson(cursor.getString(1));
                model.setDate(cursor.getString(2));
                model.setJobDateTime(cursor.getString(3));
                // Adding contact to list
                list.add(model);
            } while (cursor.moveToNext());
        }

        // return contact list
        return list;
    }

    public AssignedIncidencesModel getIncidenceOnId(String id) {

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ASSIGNED_INCIDENCES + " WHERE "
                + KEY_ASSIGNED_INCIDENCES_ID + " = " + id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        AssignedIncidencesModel model = new AssignedIncidencesModel();

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                model.setId(cursor.getInt(0));
                model.setJson(cursor.getString(1));
                model.setDate(cursor.getString(2));
                model.setJobDateTime(cursor.getString(3));
                // Adding contact to list
            } while (cursor.moveToNext());
        }

        // return contact list
        return model;
    }


//    public void deleteStreaks(String orderId, String day, String month, String year) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL("DELETE FROM " + TABLE_STREAKS + " WHERE " + KEY_STREAK_PRODUCT_ID + "='" + orderId + "'" + " AND " + KEY_STREAK_DAY + "='" + day + "'" + " AND " + KEY_STREAK_MONTH + "='" + month + "'" + " AND " + KEY_STREAK_YEAR + "='" + year + "'");
//        db.close();
//    }

    public int getJobCountFromDB(String assignedIncidenceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = null;
        try {
//            Log.e("Check duplicate","running");
            String query = "select count(*) from " + TABLE_ASSIGNED_INCIDENCES + " WHERE " + KEY_ASSIGNED_INCIDENCES_ID + "='" + assignedIncidenceId + "'";
            Log.e("streakCount", "running");
            c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                return c.getInt(0);
            }
            return 0;
        } finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    public int getUploadingQueueCountFromDB(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = null;
        try {
//            Log.e("Check duplicate","running");
            String query = "select count(*) from " + TABLE_QUEUED_INCIDENCES + " WHERE " + KEY_QUEUED_INCIDENCES_ID + "='" + id + "'";
            Log.e("streakCount", "running");
            c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                return c.getInt(0);
            }
            return 0;
        } finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }


    public int getUploadingDraftCountFromDB(String id, String taskID) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = null;
        try {
//            Log.e("Check duplicate","running");
            String query = "select count(*) from " + TABLE_draft_INCIDENCES + " WHERE " + KEY_draft_INCIDENCES_ID + "=" + id + " and " +
                    KEY_draft_Task_INCIDENCES_ID + "='" + taskID + "'";
            Log.e("streakCount", "running");
            c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                return c.getInt(0);
            }
            return 0;
        } finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    public int geTabsCountFromDB(String taskID, String formID) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = null;
        try {
//            Log.e("Check duplicate","running");
//            String query = "select count(*) from " + TABS_TABLE + " WHERE "  +
//                    KEY_TAB_TASK_ID + "='"+ taskID  + "'";

            String query = "select count(*) from " + TABS_TABLE + " WHERE " + KEY_TAB_TASK_ID + "=" + taskID + " and " +
                    KEY_TAB_ID + "='" + formID + "'";
            Log.e("streakCount", "running");
            c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                return c.getInt(0);
            }
            return 0;
        } finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    public void updateIncidences(AssignedIncidencesModel model) {

        Log.e("updating index", model.getId() + " , " + model.getJson());

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_ASSIGNED_INCIDENCES_ID, model.getId());
        contentValues.put(KEY_ASSIGNED_INCIDENCES_JSON, model.getJson());

        database.update(TABLE_ASSIGNED_INCIDENCES, contentValues, KEY_ASSIGNED_INCIDENCES_ID + " = ? ", new String[]{model.getId() + ""});
    }

    public void deleteAssignedIncidencesTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_ASSIGNED_INCIDENCES);
        db.close();
    }

    public void deleteAssignedIncidenceOnIndidenceId(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_ASSIGNED_INCIDENCES + " WHERE "
                + KEY_ASSIGNED_INCIDENCES_ID + " = " + id);
        db.close();
    }

    public void deleteQueuedImagestable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_QUEUED_IMAGES);
        db.close();
    }

    public void deleteQueuedIncidencesTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_QUEUED_INCIDENCES);
        db.close();
    }

    public void deleteQueuedIncidenceOnIndidenceId(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_QUEUED_INCIDENCES + " WHERE "
                + KEY_QUEUED_INCIDENCES_ID + " = " + id);
        db.close();
    }

    public void deleteTabsImagesIncidenceOnIndidenceId(String id, String taskID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "delete from " + TABLE_TABS_IMAGES + " WHERE " + KEY_TABID + " = " + id + " and " +
                KEY_TAB_IMAGE_TASKID + " = " + taskID;
        db.execSQL(selectQuery);
        db.close();
    }

    public void deleteTabsData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABS_TABLE);
        db.close();
    }

    public void deleteTabsImagesData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_TABS_IMAGES);
        db.close();
    }

    public void addQueueIncidenceOnIncidenceID(QueueModel model) {
        if (getUploadingQueueCountFromDB(model.getId()) == 0) {
            SQLiteDatabase database = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_QUEUED_INCIDENCES_ID, model.getId());
            contentValues.put(KEY_QUEUED_INCIDENCES_JSON, model.getJson());
            contentValues.put(KEY_QUEUED_INCIDENCES_STATE, model.getState());
            contentValues.put(KEY_QUEUED_INCIDENCES_TITLE, model.getTitle());
            contentValues.put(KEY_QUEUED_INCIDENCES_MESSAGE, model.getMessage());
            contentValues.put(KEY_NUM_FORMS_SUBMITTED, model.getNumSubmitted());

            database.insert(TABLE_QUEUED_INCIDENCES, null, contentValues);
            database.close();

        } else {
//            Toast.makeText(MainA, "Data Already exists", Toast.LENGTH_SHORT).getDialog();
            Log.e("Fav Existance", "Already Exist");
        }
    }


    public void deleteDraftIncidencesTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_draft_INCIDENCES);
        db.close();
    }

    public void deleteDraftIncidenceOnIndidenceId(String id, String taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_draft_INCIDENCES + " WHERE "
                + KEY_draft_INCIDENCES_ID + " = " + id + " AND "
                + KEY_draft_Task_INCIDENCES_ID + " = " + taskId);
        db.close();
    }

    public void addDraftIncidenceOnIncidenceID(QueueModel model) {
        Log.d("ID", "Model id for this task is: " + model.getId());
        Log.d("ID", "Model id for this task is: " + model.getTaskID());


        int count = getUploadingDraftCountFromDB(model.getId(), model.getTaskID());
        if (count == 0) {
            SQLiteDatabase database = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_draft_INCIDENCES_ID, model.getId());
            contentValues.put(KEY_draft_INCIDENCES_JSON, model.getJson());
            contentValues.put(KEY_draft_INCIDENCES_STATE, model.getState());
            contentValues.put(KEY_draft_INCIDENCES_TITLE, model.getTitle());
            contentValues.put(KEY_draft_INCIDENCES_MESSAGE, model.getMessage());
            contentValues.put(KEY_draft_Task_INCIDENCES_ID, model.getTaskID());

            database.insert(TABLE_draft_INCIDENCES, null, contentValues);
            database.close();

        } else {
//            Toast.makeText(MainA, "Data Already exists", Toast.LENGTH_SHORT).getDialog();
            Log.e("Fav Existance", "Already Existtttttttt");

            updateDraftIncidenceStateOnRunId(model);
        }
    }


    public ArrayList<QueueModel> getAllQueuedIncidences() {

        ArrayList<QueueModel> list = new ArrayList<QueueModel>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_QUEUED_INCIDENCES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                QueueModel model = new QueueModel();

                model.setId(cursor.getString(0));
                model.setJson(cursor.getString(1));
                model.setState(cursor.getString(2));
                model.setTitle(cursor.getString(3));
                model.setMessage(cursor.getString(4));
                model.setNumSubmitted(cursor.getString(5));
                //Adding contact to list
                list.add(model);
            } while (cursor.moveToNext());
        }

        // return contact list
        return list;
    }

    public QueueModel getQueueIncidenceStateOnIncidenceID(String id) {
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_QUEUED_INCIDENCES + " WHERE "
                + KEY_QUEUED_INCIDENCES_ID + " = " + id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        QueueModel model = new QueueModel();

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                model.setId(cursor.getString(0));
                model.setJson(cursor.getString(1));
                model.setState(cursor.getString(2));
                model.setTitle(cursor.getString(3));
                model.setMessage(cursor.getString(4));
                model.setNumSubmitted(cursor.getString(5));

                // Adding contact to list
            } while (cursor.moveToNext());
        }

        // return contact list
        return model;
    }


    public QueueModel getDraftIncidenceStateOnIncidenceID(String id, String taskID) {
        // Select All Query

        String selectQuery = "SELECT  * FROM  " + TABLE_draft_INCIDENCES + " WHERE " + KEY_draft_INCIDENCES_ID + " = " + id + " and " +
                KEY_draft_Task_INCIDENCES_ID + " = " + taskID;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        QueueModel model = new QueueModel();

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                model.setId(cursor.getString(0));
                model.setJson(cursor.getString(1));
                model.setState(cursor.getString(2));
                model.setTitle(cursor.getString(3));
                model.setMessage(cursor.getString(4));

                // Adding contact to list
            } while (cursor.moveToNext());
        }

        // return contact list
        return model;
    }

    public void updateQueuedIncidenceStateOnRunId(QueueModel model) {

        Log.e("updating index", model.getId() + " , " + model.getJson());

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_QUEUED_INCIDENCES_ID, model.getId());
        contentValues.put(KEY_QUEUED_INCIDENCES_JSON, model.getJson());
        contentValues.put(KEY_QUEUED_INCIDENCES_STATE, model.getState());
        contentValues.put(KEY_QUEUED_INCIDENCES_TITLE, model.getTitle());
        contentValues.put(KEY_QUEUED_INCIDENCES_MESSAGE, model.getMessage());
        contentValues.put(KEY_NUM_FORMS_SUBMITTED, model.getNumSubmitted());


        database.update(TABLE_QUEUED_INCIDENCES, contentValues, KEY_QUEUED_INCIDENCES_ID + " = ? ", new String[]{model.getId() + ""});
    }


    public void updateDraftIncidenceStateOnRunId(QueueModel model) {

        Log.e("updating index", model.getId() + " , " + model.getJson());

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_draft_INCIDENCES_ID, model.getId());
        contentValues.put(KEY_draft_INCIDENCES_JSON, model.getJson());
        contentValues.put(KEY_draft_INCIDENCES_STATE, model.getState());
        contentValues.put(KEY_draft_INCIDENCES_TITLE, model.getTitle());
        contentValues.put(KEY_draft_INCIDENCES_MESSAGE, model.getMessage());
        contentValues.put(KEY_draft_Task_INCIDENCES_ID, model.getTaskID());


        database.update(TABLE_draft_INCIDENCES, contentValues, KEY_draft_INCIDENCES_ID + " = ? ", new String[]{model.getId() + ""});
    }


    public void addImagesOnIncidenceID(DBImagesModel model) {
        Log.d("IMAGES", "task id is:  " + model.getIncidenceId());
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
//            contentValues.put(KEY_QUEUED_INCIDENCES_ID, model.getId());
        contentValues.put(KEY_QUEUED_IMAGE_INCIDENCE_ID, model.getIncidenceId());
        contentValues.put(KEY_QUEUED_IMAGE_URI, model.getTempUri() + "");
        contentValues.put(KEY_QUEUED_IMAGE_NAME, model.getName());
        database.insert(TABLE_QUEUED_IMAGES, null, contentValues);
        database.close();


    }

    public void updateImagesOnTaskSections(SelectImagesModel model) {


        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_TABID, model.getFormId());
        contentValues.put(KEY_TAB_IMAGE_URI, model.getTempUri() + "");
        contentValues.put(KEY_TAB_IMAGE_TASKID, model.getTaskId() + "");
        contentValues.put(KEY_TAB_FINAL_IMAGE, model.getFinalImage() + "");
        contentValues.put(KEY_TAB_IMAGE_NAME, model.getName() + "");


        database.update(TABLE_TABS_IMAGES, contentValues, KEY_TABID + " = ? ", new String[]{model.getFormId() + ""});
    }

    public int getImagesCountOnTask(String id, String taskID) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = null;
        try {
//            Log.e("Check duplicate","running");
            String query = "select count(*) from " + TABLE_TABS_IMAGES + " WHERE " + KEY_TABID + "=" + id + " and " +
                    KEY_TAB_IMAGE_TASKID + "='" + taskID + "'";
            Log.e("streakCount", "running");
            c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                return c.getInt(0);
            }
            return 0;
        } finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }


    public ArrayList<DBImagesModel> getAllImagesOnImageId(String id) {
        Log.d("IMAGES", "task id is:  " + id);

        ArrayList<DBImagesModel> list = new ArrayList<DBImagesModel>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_QUEUED_IMAGES + " WHERE "
                + KEY_QUEUED_IMAGE_INCIDENCE_ID + " = " + id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DBImagesModel model = new DBImagesModel();

                model.setId(cursor.getInt(0));
                model.setIncidenceId(cursor.getString(1));
                model.setTempUri(Uri.parse(cursor.getString(2)));
                model.setName(cursor.getString(3));
                // Adding contact to list
                list.add(model);
            } while (cursor.moveToNext());
        }

        // return contact list
        return list;
    }


    public void addImagesOnTabIncidenceID(List<SelectImagesModel> modelList) {
        Log.d("SIZE", "List size is: " + modelList.size());

        for (SelectImagesModel model : modelList) {
            int count = getImagesCountOnTask(model.getTaskId(), model.getTaskId());
            if (count == 0) {
                SQLiteDatabase database = this.getWritableDatabase();
                Log.d("SIZE", "form id is: " + model.getFormId());
                Log.d("SIZE", "task id is: " + model.getTaskId());


                ContentValues contentValues = new ContentValues();
                contentValues.put(KEY_TABID, model.getFormId());
                contentValues.put(KEY_TAB_IMAGE_URI, model.getTempUri() + "");
                contentValues.put(KEY_TAB_IMAGE_TASKID, model.getTaskId() + "");
                contentValues.put(KEY_TAB_FINAL_IMAGE, model.getFinalImage() + "");
                contentValues.put(KEY_TAB_IMAGE_NAME, model.getName() + "");

                database.insert(TABLE_TABS_IMAGES, null, contentValues);
                database.close();
            } else {
                updateImagesOnTaskSections(model);
            }
        }

    }

    public ArrayList<SelectImagesModel> getAllTAbImagesOnImageId(String id, String taskID) {
        Log.d("IMAGES", "tab id is:  " + id);
        Log.d("IMAGES", "task id is:  " + taskID);

        ArrayList<SelectImagesModel> list = new ArrayList<SelectImagesModel>();
        // Select All Query
//        String selectQuery = "SELECT  * FROM " + TABLE_TABS_IMAGES + " WHERE "
//                + KEY_QUEUED_IMAGE_INCIDENCE_ID + " = " + id;

        String selectQuery = "SELECT  * FROM  " + TABLE_TABS_IMAGES + " WHERE " + KEY_TABID + " = " + id + " and " +
                KEY_TAB_IMAGE_TASKID + " = " + taskID;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                SelectImagesModel model = new SelectImagesModel();

                model.setFormId(String.valueOf(cursor.getInt(0)));
                model.setTempUri(Uri.parse(cursor.getString(1)));
                model.setTaskId(cursor.getString(2));
                model.setFinalImage(new File(cursor.getString(3)));
                model.setName(cursor.getString(4));

                // Adding contact to list
                list.add(model);
            } while (cursor.moveToNext());
        }

        // return contact list
        return list;
    }


    public void deleteAdminFormsTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_ADMIN_FORMS);
        db.close();
    }
}
