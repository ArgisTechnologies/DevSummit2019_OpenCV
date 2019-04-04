package com.logicpd.papapill.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.logicpd.papapill.data.BinDetection;
import com.logicpd.papapill.data.Contact;
import com.logicpd.papapill.data.DaySchedule;
import com.logicpd.papapill.data.DispenseTime;
import com.logicpd.papapill.data.Medication;
import com.logicpd.papapill.data.NotificationSetting;
import com.logicpd.papapill.data.ScheduleItem;
import com.logicpd.papapill.data.User;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Helper class for accessing database and handling CRUD operations
 *
 * @author alankilloren
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper mInstance;

    //singleton method
    public static DatabaseHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseHelper(context);
        }
        return mInstance;
    }

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "papapill.db";

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_CONTACTS = "contacts";
    private static final String TABLE_SYSTEM = "system";
    private static final String TABLE_NOTIFICATION_SETTINGS = "notification_settings";
    private static final String TABLE_MEDICATIONS = "medications";
    private static final String TABLE_DISPENSE_TIMES = "dispense_times";
    private static final String TABLE_SCHEDULE = "schedule";
    private static final String TABLE_BINS = "bins";

    // Common column names
    private static final String KEY_ID = "id";

    // Table users - column names
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PIN = "pin";
    private static final String KEY_PATIENTNAME = "patientname";
    private static final String KEY_AUDIO_VOLUME = "audiovolume";
    private static final String KEY_FONT_SIZE = "fontsize";
    private static final String KEY_SCREEN_BRIGHTNESS = "screenbrightness";
    private static final String KEY_VOICE = "voice";
    private static final String KEY_THEME = "theme";
    private static final String KEY_RECOVERY_CONTACT_ID = "recoverycontactid";
    private static final String KEY_USER_NUMBER = "usernumber";

    //Table contacts - column names
    private static final String KEY_USERID = "userid";
    private static final String KEY_CONTACT_NAME = "name";
    private static final String KEY_CONTACT_TEXT_NUMBER = "textnumber";
    private static final String KEY_CONTACT_VOICE_NUMBER = "voicenumber";
    private static final String KEY_CONTACT_EMAIL = "email";
    private static final String KEY_CONTACT_CATEGORY = "category";
    private static final String KEY_IS_CONTACT_SELECTED = "selected";
    private static final String KEY_IS_TEXT_SELECTED = "istextselected";
    private static final String KEY_IS_VOICE_SELECTED = "isvoiceselected";
    private static final String KEY_IS_EMAIL_SELECTED = "isemailselected";
    private static final String KEY_RELATIONSHIP = "relationship";

    //Table notification_settings - column names
    private static final String KEY_NOTIFICATION_SETTING_ID = "notification_setting_id";
    private static final String KEY_CONTACT_ID = "contactid";
    private static final String KEY_NOTIFICATION_SETTING_NAME = "settingname";

    //Table system - column names
    private static final String KEY_SYSTEM_KEY = "systemkey";
    private static final String KEY_SERIAL_NUMBER = "serialnumber";

    //Table medications - column names
    private static final String KEY_MEDICATION_NAME = "medication_name";
    private static final String KEY_MEDICATION_NICKNAME = "medication_nickname";
    private static final String KEY_STRENGTH_VALUE = "strength_value";
    private static final String KEY_STRENGTH_MEASUREMENT = "strength_measurement";
    private static final String KEY_DOSAGE_INSTRUCTIONS = "dosage_instructions";
    private static final String KEY_TIME_BETWEEN_DOSES = "time_between_doses";
    private static final String KEY_MAX_UNTIS_PER_DAY = "max_units_per_day";
    private static final String KEY_MAX_NUMBER_PER_DOSE = "max_number_per_dose";
    private static final String KEY_MEDICATION_QUANTITY = "medication_quantity";
    private static final String KEY_USE_BY_DATE = "use_by_date";
    private static final String KEY_FILL_DATE = "fill_date";
    private static final String KEY_MEDICATION_LOCATION = "medication_location";
    private static final String KEY_MEDICATION_LOCATION_NAME = "medication_location_name";
    private static final String KEY_IS_MEDICATION_PAUSED = "is_paused";
    private static final String KEY_MEDICATION_SCHEDULE_TYPE = "medication_schedule_type";

    //Table dispense_times - column names
    private static final String KEY_DISPENSE_NAME = "dispense_name";
    private static final String KEY_DISPENSE_TIME = "dispense_time";
    private static final String KEY_IS_ACTIVE = "is_active";

    //Table schedule - column names
    private static final String KEY_MEDICATION_ID = "medication_id";
    private static final String KEY_DISPENSE_TIME_ID = "dispense_time_id";
    private static final String KEY_DISPENSE_AMOUNT = "dispense_amount";
    private static final String KEY_SCHEDULE_TYPE = "schedule_type";
    private static final String KEY_SCHEDULE_DATE = "schedule_date";
    private static final String KEY_SCHEDULE_DAY = "schedule_day";

    //Table bins - column names
    private static final String KEY_BIN_DETECT_IMAGE = "detect_image";
    private static final String KEY_BIN_PILL_COUNT = "pill_count";
    private static final String KEY_BIN_UPPER_LEFT_X = "upper_left_x";
    private static final String KEY_BIN_UPPER_LEFT_Y = "upper_left_y";
    private static final String KEY_BIN_LOWER_RIGHT_X = "lower_right_x";
    private static final String KEY_BIN_LOWER_RIGHT_Y = "lower_right_y";
    private static final String KEY_BIN_PILL_TEMPLATE = "pill_template";
    private static final String KEY_BIN_RUN_ANALYSIS = "run_analysis";
    private static final String KEY_BIN_FILL_LEVEL = "fill_level";

    //create methods
    private static final String CREATE_TABLE_BINS = "CREATE TABLE "
            + TABLE_BINS + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_BIN_DETECT_IMAGE + " BLOB,"
            + KEY_BIN_PILL_COUNT + " INTEGER,"
            + KEY_BIN_UPPER_LEFT_X + " INTEGER,"
            + KEY_BIN_UPPER_LEFT_Y + " INTEGER,"
            + KEY_BIN_LOWER_RIGHT_X + " INTEGER,"
            + KEY_BIN_LOWER_RIGHT_Y + " INTEGER,"
            + KEY_BIN_RUN_ANALYSIS + " INTEGER,"
            + KEY_BIN_FILL_LEVEL + " INTEGER,"
            + KEY_BIN_PILL_TEMPLATE + " TEXT)";

    private static final String CREATE_TABLE_SCHEDULE = "CREATE TABLE "
            + TABLE_SCHEDULE + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_USERID + " INTEGER,"
            + KEY_MEDICATION_ID + " INTEGER,"
            + KEY_DISPENSE_TIME_ID + " INTEGER,"
            + KEY_DISPENSE_AMOUNT + " INTEGER,"
            + KEY_SCHEDULE_DATE + " TEXT,"
            + KEY_SCHEDULE_DAY + " TEXT,"
            + KEY_SCHEDULE_TYPE + " INTEGER)";

    private static final String CREATE_TABLE_DISPENSE_TIMES = "CREATE TABLE "
            + TABLE_DISPENSE_TIMES + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_USERID + " INTEGER,"
            + KEY_DISPENSE_NAME + " TEXT,"
            + KEY_IS_ACTIVE + " INTEGER,"
            + KEY_DISPENSE_TIME + " TEXT)";

    private static final String CREATE_TABLE_MEDICATIONS = "CREATE TABLE "
            + TABLE_MEDICATIONS + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_MEDICATION_NAME + " TEXT,"
            + KEY_MEDICATION_NICKNAME + " TEXT,"
            + KEY_USERID + " INTEGER,"
            + KEY_STRENGTH_VALUE + " INTEGER,"
            + KEY_STRENGTH_MEASUREMENT + " TEXT,"
            + KEY_DOSAGE_INSTRUCTIONS + " TEXT,"
            + KEY_TIME_BETWEEN_DOSES + " INTEGER,"
            + KEY_MAX_UNTIS_PER_DAY + " INTEGER,"
            + KEY_MAX_NUMBER_PER_DOSE + " INTEGER,"
            + KEY_MEDICATION_QUANTITY + " INTEGER,"
            + KEY_USE_BY_DATE + " TEXT,"
            + KEY_FILL_DATE + " TEXT,"
            + KEY_MEDICATION_LOCATION + " INTEGER,"
            + KEY_MEDICATION_LOCATION_NAME + " TEXT,"
            + KEY_IS_MEDICATION_PAUSED + " INTEGER,"
            + KEY_MEDICATION_SCHEDULE_TYPE + " INTEGER,"
            + KEY_PATIENTNAME + " TEXT)";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE "
            + TABLE_USERS + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_USERNAME + " TEXT,"
            + KEY_PATIENTNAME + " TEXT,"
            + KEY_AUDIO_VOLUME + " INTEGER,"
            + KEY_FONT_SIZE + " INTEGER,"
            + KEY_SCREEN_BRIGHTNESS + " INTEGER,"
            + KEY_VOICE + " INTEGER,"
            + KEY_THEME + " INTEGER,"
            + KEY_RECOVERY_CONTACT_ID + " INTEGER,"
            + KEY_USER_NUMBER + " TEXT,"
            + KEY_PIN + " TEXT)";

    private static final String CREATE_TABLE_CONTACTS = "CREATE TABLE "
            + TABLE_CONTACTS + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_USERID + " INTEGER,"
            + KEY_CONTACT_NAME + " TEXT,"
            + KEY_CONTACT_TEXT_NUMBER + " TEXT,"
            + KEY_CONTACT_VOICE_NUMBER + " TEXT,"
            + KEY_CONTACT_EMAIL + " TEXT,"
            + KEY_CONTACT_CATEGORY + " TEXT,"
            + KEY_RELATIONSHIP + " INTEGER,"
            + KEY_IS_CONTACT_SELECTED + " INTEGER,"
            + KEY_IS_TEXT_SELECTED + " INTEGER,"
            + KEY_IS_VOICE_SELECTED + " INTEGER,"
            + KEY_IS_EMAIL_SELECTED + " INTEGER)";

    private static final String CREATE_TABLE_NOTIFICATION_SETTINGS = "CREATE TABLE "
            + TABLE_NOTIFICATION_SETTINGS + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_NOTIFICATION_SETTING_ID + " INTEGER,"
            + KEY_USERID + " INTEGER,"
            + KEY_CONTACT_ID + " INTEGER,"
            + KEY_NOTIFICATION_SETTING_NAME + " TEXT,"
            + KEY_IS_TEXT_SELECTED + " INTEGER,"
            + KEY_IS_VOICE_SELECTED + " INTEGER,"
            + KEY_IS_EMAIL_SELECTED + " INTEGER)";

    private static final String CREATE_TABLE_SYSTEM = "CREATE TABLE "
            + TABLE_SYSTEM + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_SERIAL_NUMBER + " TEXT,"
            + KEY_SYSTEM_KEY + " TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_CONTACTS);
        db.execSQL(CREATE_TABLE_SYSTEM);
        db.execSQL(CREATE_TABLE_NOTIFICATION_SETTINGS);
        db.execSQL(CREATE_TABLE_MEDICATIONS);
        db.execSQL(CREATE_TABLE_DISPENSE_TIMES);
        db.execSQL(CREATE_TABLE_SCHEDULE);
        db.execSQL(CREATE_TABLE_BINS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SYSTEM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION_SETTINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DISPENSE_TIMES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BINS);
        onCreate(db);
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        this.onUpgrade(db, 0, 1);
    }

    /**
     * Method for adding a Contact
     *
     * @param contact Contact object to add
     * @return integer of record ID
     */
    public int addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERID, contact.getUserid());
        values.put(KEY_CONTACT_NAME, contact.getName());
        values.put(KEY_CONTACT_TEXT_NUMBER, contact.getTextNumber());
        values.put(KEY_CONTACT_VOICE_NUMBER, contact.getVoiceNumber());
        values.put(KEY_CONTACT_EMAIL, contact.getEmail());
        values.put(KEY_CONTACT_CATEGORY, contact.getCategory());
        values.put(KEY_IS_CONTACT_SELECTED, contact.isSelected());
        values.put(KEY_IS_TEXT_SELECTED, contact.isTextNumberSelected());
        values.put(KEY_IS_VOICE_SELECTED, contact.isVoiceNumberSelected());
        values.put(KEY_IS_EMAIL_SELECTED, contact.isEmailSelected());
        values.put(KEY_RELATIONSHIP, contact.getRelationship());

        // insert row
        int returnVal = (int) db.insert(TABLE_CONTACTS, null, values);
        db.close();
        return returnVal;
    }

    /**
     * Retrieves the current system key
     *
     * @return String key
     */
    public String getSystemKey() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_SYSTEM;
        String result = null;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null) {
            c.moveToFirst();
            if (c.getCount() > 0) {
                result = c.getString(c.getColumnIndex(KEY_SYSTEM_KEY));
            }
            c.close();
        }
        db.close();
        return result;
    }

    /**
     * Adds a system key
     *
     * @param key String
     * @return integer of record ID
     */
    public int addSystemKey(String key) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SYSTEM_KEY, key);

        // insert row
        int returnVal = (int) db.insert(TABLE_SYSTEM, null, values);
        db.close();
        return returnVal;
    }

    /**
     * Drop all rows(for testing)
     *
     * @return number of records removed
     */
    public void deleteSystemKeys() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM TABLE_SYSTEM");
            db.close();
        } catch (Exception ex) {

        }
    }

    /**
     * Updates a system key
     *
     * @param id  integer record ID
     * @param key new String key
     * @return integer of rows updated
     */
    public int updateSystemKey(int id, String key) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SYSTEM_KEY, key);

        // updating row
        return db.update(TABLE_SYSTEM, values, KEY_ID + " = ?"
                , new String[]{String.valueOf(id)});
    }

    //TODO need to determine when this gets called
    public int updateSerialNumber(int id, String sn) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SERIAL_NUMBER, sn);

        // updating row
        return db.update(TABLE_SYSTEM, values, KEY_ID + " = ?"
                , new String[]{String.valueOf(id)});
    }

    /**
     * Removes a contact
     *
     * @param contact Contact object to remove
     * @return integer of removed rows
     */
    public int deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        int returnVal = db.delete(TABLE_CONTACTS, KEY_ID + " = ?", new String[]{String.valueOf(contact.getId())});
        db.close();
        return returnVal;
    }

    public int deleteContactsForUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        int returnVal = db.delete(TABLE_CONTACTS, KEY_USERID + " = ?", new String[]{String.valueOf(user.getId())});
        db.close();
        return returnVal;
    }

    /**
     * Returns a list of contacts associated with a user
     *
     * @param user User object
     * @return list of contacts
     */
    public List<Contact> getContactsForUser(User user) {
        List<Contact> contactList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CONTACTS + " WHERE " + KEY_USERID + "=" + user.getId() + " ORDER BY " + KEY_ID + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                contact.setUserid(c.getInt(c.getColumnIndex(KEY_USERID)));
                contact.setName(c.getString(c.getColumnIndex(KEY_CONTACT_NAME)));
                contact.setTextNumber(c.getString(c.getColumnIndex(KEY_CONTACT_TEXT_NUMBER)));
                contact.setVoiceNumber(c.getString(c.getColumnIndex(KEY_CONTACT_VOICE_NUMBER)));
                contact.setEmail(c.getString(c.getColumnIndex(KEY_CONTACT_EMAIL)));
                contact.setCategory(c.getString(c.getColumnIndex(KEY_CONTACT_CATEGORY)));
                contact.setSelected(c.getInt(c.getColumnIndex(KEY_IS_CONTACT_SELECTED)) > 0);
                contact.setTextNumberSelected(c.getInt(c.getColumnIndex(KEY_IS_TEXT_SELECTED)) > 0);
                contact.setVoiceNumberSelected(c.getInt(c.getColumnIndex(KEY_IS_VOICE_SELECTED)) > 0);
                contact.setEmailSelected(c.getInt(c.getColumnIndex(KEY_IS_EMAIL_SELECTED)) > 0);
                contact.setRelationship(c.getInt(c.getColumnIndex(KEY_RELATIONSHIP)));

                // adding to list
                contactList.add(contact);
            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return contactList;
    }

    /**
     * Adds a user
     *
     * @param user User object to add
     * @return integer of record ID
     */
    public int addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, user.getUsername());
        values.put(KEY_PIN, user.getPin());
        values.put(KEY_PATIENTNAME, user.getPatientname());
        values.put(KEY_AUDIO_VOLUME, user.getAudioVolume());
        values.put(KEY_FONT_SIZE, user.getFontSize());
        values.put(KEY_SCREEN_BRIGHTNESS, user.getScreenBrightness());
        values.put(KEY_VOICE, user.getVoice());
        values.put(KEY_THEME, user.getTheme());
        values.put(KEY_RECOVERY_CONTACT_ID, user.getRecoveryContactId());
        values.put(KEY_USER_NUMBER, user.getUserNumber());

        // insert row
        int returnVal = (int) db.insert(TABLE_USERS, null, values);
        db.close();
        return returnVal;
    }

    /**
     * Removes a user
     *
     * @param user User object to remove
     * @return integer of rows deleted
     */
    public int deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        int returnVal = db.delete(TABLE_USERS, KEY_ID + " = ?", new String[]{String.valueOf(user.getId())});
        db.close();
        return returnVal;
    }

    /**
     * Retrieves a list of users
     *
     * @return list of users
     */
    public List<User> getUsers() {
        List<User> userList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_USERS + " ORDER BY " + KEY_ID + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                User user = new User();
                user.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                user.setUsername(c.getString(c.getColumnIndex(KEY_USERNAME)));
                user.setPin(c.getString(c.getColumnIndex(KEY_PIN)));
                user.setPatientname(c.getString(c.getColumnIndex(KEY_PATIENTNAME)));
                user.setAudioVolume(c.getInt(c.getColumnIndex(KEY_AUDIO_VOLUME)));
                user.setFontSize(c.getInt(c.getColumnIndex(KEY_FONT_SIZE)));
                user.setScreenBrightness(c.getInt(c.getColumnIndex(KEY_SCREEN_BRIGHTNESS)));
                user.setVoice(c.getInt(c.getColumnIndex(KEY_VOICE)));
                user.setTheme(c.getInt(c.getColumnIndex(KEY_THEME)));
                user.setRecoveryContactId(c.getInt(c.getColumnIndex(KEY_RECOVERY_CONTACT_ID)));
                user.setUserNumber(c.getString(c.getColumnIndex(KEY_USER_NUMBER)));

                // adding to list
                userList.add(user);
            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return userList;
    }

    /**
     * Retrieves a single user
     *
     * @param userId integer ID of user
     * @return User object
     */
    public User getUserByID(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_USERS + " WHERE " + KEY_ID + " = " + userId;
        Cursor c = db.rawQuery(selectQuery, null);
        User user = null;
        if (c != null) {
            c.moveToFirst();

            user = new User();
            user.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            user.setUsername(c.getString(c.getColumnIndex(KEY_USERNAME)));
            user.setPin(c.getString(c.getColumnIndex(KEY_PIN)));
            user.setPatientname(c.getString(c.getColumnIndex(KEY_PATIENTNAME)));
            user.setAudioVolume(c.getInt(c.getColumnIndex(KEY_AUDIO_VOLUME)));
            user.setFontSize(c.getInt(c.getColumnIndex(KEY_FONT_SIZE)));
            user.setScreenBrightness(c.getInt(c.getColumnIndex(KEY_SCREEN_BRIGHTNESS)));
            user.setVoice(c.getInt(c.getColumnIndex(KEY_VOICE)));
            user.setTheme(c.getInt(c.getColumnIndex(KEY_THEME)));
            user.setRecoveryContactId(c.getInt(c.getColumnIndex(KEY_RECOVERY_CONTACT_ID)));
            user.setUserNumber(c.getString(c.getColumnIndex(KEY_USER_NUMBER)));

            c.close();
        }
        db.close();
        return user;
    }

    /**
     * Retrieves a user by username
     *
     * @param username String
     * @return User object
     */
    public User getUserByName(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_USERS + " WHERE " + KEY_USERNAME + " = '" + username + "'";
        Cursor c = db.rawQuery(selectQuery, null);
        User user = null;
        if (c != null) {
            c.moveToFirst();

            user = new User();
            user.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            user.setUsername(c.getString(c.getColumnIndex(KEY_USERNAME)));
            user.setPin(c.getString(c.getColumnIndex(KEY_PIN)));
            user.setPatientname(c.getString(c.getColumnIndex(KEY_PATIENTNAME)));
            user.setAudioVolume(c.getInt(c.getColumnIndex(KEY_AUDIO_VOLUME)));
            user.setFontSize(c.getInt(c.getColumnIndex(KEY_FONT_SIZE)));
            user.setScreenBrightness(c.getInt(c.getColumnIndex(KEY_SCREEN_BRIGHTNESS)));
            user.setVoice(c.getInt(c.getColumnIndex(KEY_VOICE)));
            user.setTheme(c.getInt(c.getColumnIndex(KEY_THEME)));
            user.setRecoveryContactId(c.getInt(c.getColumnIndex(KEY_RECOVERY_CONTACT_ID)));
            user.setUserNumber(c.getString(c.getColumnIndex(KEY_USER_NUMBER)));

            c.close();
        }
        db.close();
        return user;
    }

    /**
     * Updates a user record
     *
     * @param user User to update
     * @return integer of rows updated
     */
    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, user.getId());
        values.put(KEY_USERNAME, user.getUsername());
        values.put(KEY_PIN, user.getPin());
        values.put(KEY_PATIENTNAME, user.getPatientname());
        values.put(KEY_AUDIO_VOLUME, user.getAudioVolume());
        values.put(KEY_FONT_SIZE, user.getFontSize());
        values.put(KEY_SCREEN_BRIGHTNESS, user.getScreenBrightness());
        values.put(KEY_VOICE, user.getVoice());
        values.put(KEY_THEME, user.getTheme());
        values.put(KEY_RECOVERY_CONTACT_ID, user.getRecoveryContactId());
        values.put(KEY_USER_NUMBER, user.getUserNumber());

        // updating row
        return db.update(TABLE_USERS, values, KEY_ID + " = ?"
                , new String[]{String.valueOf(user.getId())});
    }

    /**
     * Adds or updates user //TODO not currently used but may down the road, so keeping for now
     *
     * @param user User
     * @return Integer number of rows updated
     */
    public int upsertUser(User user) {
        //check to see if user already exists, if so, update, if not, add
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_USERS + " WHERE " + KEY_USERNAME + " = '" + user.getUsername() + "'";
        ContentValues values = new ContentValues();
        values.put(KEY_ID, user.getId());
        values.put(KEY_USERNAME, user.getUsername());
        values.put(KEY_PIN, user.getPin());
        values.put(KEY_PATIENTNAME, user.getPatientname());
        values.put(KEY_AUDIO_VOLUME, user.getAudioVolume());
        values.put(KEY_FONT_SIZE, user.getFontSize());
        values.put(KEY_SCREEN_BRIGHTNESS, user.getScreenBrightness());
        values.put(KEY_VOICE, user.getVoice());
        values.put(KEY_THEME, user.getTheme());
        values.put(KEY_RECOVERY_CONTACT_ID, user.getRecoveryContactId());
        values.put(KEY_USER_NUMBER, user.getUserNumber());

        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null) {
            c.moveToFirst();
            if (c.getCount() > 0) {
                //record found, do an update
                return db.update(TABLE_USERS, values, KEY_ID + " = ?"
                        , new String[]{String.valueOf(user.getId())});
            }
            c.close();
        }

        // add
        int returnVal = (int) db.insert(TABLE_USERS, null, values);
        db.close();
        return returnVal;
    }

    /**
     * Returns a list of all contacts sorted by name
     *
     * @return List of Contact objects
     */
    public List<Contact> getContacts() {
        List<Contact> contactList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CONTACTS + " ORDER BY " + KEY_CONTACT_NAME + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                contact.setUserid(c.getInt(c.getColumnIndex(KEY_USERID)));
                contact.setName(c.getString(c.getColumnIndex(KEY_CONTACT_NAME)));
                contact.setTextNumber(c.getString(c.getColumnIndex(KEY_CONTACT_TEXT_NUMBER)));
                contact.setVoiceNumber(c.getString(c.getColumnIndex(KEY_CONTACT_VOICE_NUMBER)));
                contact.setEmail(c.getString(c.getColumnIndex(KEY_CONTACT_EMAIL)));
                contact.setCategory(c.getString(c.getColumnIndex(KEY_CONTACT_CATEGORY)));
                contact.setSelected(c.getInt(c.getColumnIndex(KEY_IS_CONTACT_SELECTED)) > 0);
                contact.setTextNumberSelected(c.getInt(c.getColumnIndex(KEY_IS_TEXT_SELECTED)) > 0);
                contact.setVoiceNumberSelected(c.getInt(c.getColumnIndex(KEY_IS_VOICE_SELECTED)) > 0);
                contact.setEmailSelected(c.getInt(c.getColumnIndex(KEY_IS_EMAIL_SELECTED)) > 0);
                contact.setRelationship(c.getInt(c.getColumnIndex(KEY_RELATIONSHIP)));

                // adding to list
                contactList.add(contact);
            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return contactList;
    }

    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERID, contact.getUserid());
        values.put(KEY_CONTACT_NAME, contact.getName());
        values.put(KEY_CONTACT_TEXT_NUMBER, contact.getTextNumber());
        values.put(KEY_CONTACT_VOICE_NUMBER, contact.getVoiceNumber());
        values.put(KEY_CONTACT_EMAIL, contact.getEmail());
        values.put(KEY_CONTACT_CATEGORY, contact.getCategory());
        values.put(KEY_IS_CONTACT_SELECTED, contact.isSelected());
        values.put(KEY_IS_TEXT_SELECTED, contact.isTextNumberSelected());
        values.put(KEY_IS_VOICE_SELECTED, contact.isVoiceNumberSelected());
        values.put(KEY_IS_EMAIL_SELECTED, contact.isEmailSelected());
        values.put(KEY_RELATIONSHIP, contact.getRelationship());

        // updating row
        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?"
                , new String[]{String.valueOf(contact.getId())});
    }

    public int addNotificationSetting(NotificationSetting setting) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTIFICATION_SETTING_ID, setting.getId());
        values.put(KEY_NOTIFICATION_SETTING_NAME, setting.getName());
        values.put(KEY_USERID, setting.getUserId());
        values.put(KEY_CONTACT_ID, setting.getContactId());
        values.put(KEY_IS_TEXT_SELECTED, setting.isTextSelected());
        values.put(KEY_IS_VOICE_SELECTED, setting.isVoiceSelected());
        values.put(KEY_IS_EMAIL_SELECTED, setting.isEmailSelected());

        // insert row
        int returnVal = (int) db.insert(TABLE_NOTIFICATION_SETTINGS, null, values);
        db.close();
        return returnVal;
    }

    public int addNotificationSettings(List<NotificationSetting> notificationSettings) {
        SQLiteDatabase db = this.getWritableDatabase();
        int returnVal = 0;
        for (NotificationSetting setting : notificationSettings) {//add current settings
            ContentValues values = new ContentValues();
            values.put(KEY_NOTIFICATION_SETTING_ID, setting.getId());
            values.put(KEY_NOTIFICATION_SETTING_NAME, setting.getName());
            values.put(KEY_USERID, setting.getUserId());
            values.put(KEY_CONTACT_ID, setting.getContactId());
            values.put(KEY_IS_TEXT_SELECTED, setting.isTextSelected());
            values.put(KEY_IS_VOICE_SELECTED, setting.isVoiceSelected());
            values.put(KEY_IS_EMAIL_SELECTED, setting.isEmailSelected());

            // insert row
            returnVal = (int) db.insert(TABLE_NOTIFICATION_SETTINGS, null, values);
        }
        db.close();
        return returnVal;
    }

    public List<NotificationSetting> getNotificationSettingList(User user, Contact contact) {
        List<NotificationSetting> settingList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NOTIFICATION_SETTINGS
                + " WHERE " + KEY_USERID + " =" + user.getId() + " AND " + KEY_CONTACT_ID + "=" + contact.getId()
                + " ORDER BY " + KEY_NOTIFICATION_SETTING_ID + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                NotificationSetting setting = new NotificationSetting();
                setting.setContactId(c.getInt(c.getColumnIndex(KEY_CONTACT_ID)));
                setting.setUserId(c.getInt(c.getColumnIndex(KEY_USERID)));
                setting.setName(c.getString(c.getColumnIndex(KEY_NOTIFICATION_SETTING_NAME)));
                setting.setId(c.getInt(c.getColumnIndex(KEY_NOTIFICATION_SETTING_ID)));
                setting.setTextSelected(c.getInt(c.getColumnIndex(KEY_IS_TEXT_SELECTED)) > 0);
                setting.setVoiceSelected(c.getInt(c.getColumnIndex(KEY_IS_VOICE_SELECTED)) > 0);
                setting.setEmailSelected(c.getInt(c.getColumnIndex(KEY_IS_EMAIL_SELECTED)) > 0);

                // adding to list
                settingList.add(setting);
            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return settingList;
    }

    public int deleteNotificationSettings(User user, Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        int returnVal = db.delete(TABLE_NOTIFICATION_SETTINGS, KEY_USERID + " = ? AND " + KEY_CONTACT_ID + " = ?"
                , new String[]{String.valueOf(user.getId()), String.valueOf(contact.getId())});
        db.close();
        return returnVal;
    }

    public int addMedication(Medication medication) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MEDICATION_NAME, medication.getName());
        values.put(KEY_MEDICATION_NICKNAME, medication.getNickname());
        values.put(KEY_USERID, medication.getUserId());
        values.put(KEY_STRENGTH_VALUE, medication.getStrength_value());
        values.put(KEY_STRENGTH_MEASUREMENT, medication.getStrength_measurement());
        values.put(KEY_DOSAGE_INSTRUCTIONS, medication.getDosage_instructions());
        values.put(KEY_TIME_BETWEEN_DOSES, medication.getTime_between_doses());
        values.put(KEY_MAX_UNTIS_PER_DAY, medication.getMax_units_per_day());
        values.put(KEY_MAX_NUMBER_PER_DOSE, medication.getMax_number_per_dose());
        values.put(KEY_MEDICATION_QUANTITY, medication.getMedication_quantity());
        values.put(KEY_USE_BY_DATE, medication.getUse_by_date());
        values.put(KEY_FILL_DATE, medication.getFill_date());
        values.put(KEY_MEDICATION_LOCATION, medication.getMedication_location());
        values.put(KEY_MEDICATION_LOCATION_NAME, medication.getMedication_location_name());
        values.put(KEY_PATIENTNAME, medication.getPatient_name());
        values.put(KEY_IS_MEDICATION_PAUSED, medication.isPaused());
        values.put(KEY_MEDICATION_SCHEDULE_TYPE, medication.getMedication_schedule_type());

        // insert row
        int returnVal = (int) db.insert(TABLE_MEDICATIONS, null, values);
        db.close();
        return returnVal;//this is the record ID
    }

    public int updateMedication(Medication medication) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MEDICATION_NAME, medication.getName());
        values.put(KEY_MEDICATION_NICKNAME, medication.getNickname());
        values.put(KEY_USERID, medication.getUserId());
        values.put(KEY_STRENGTH_VALUE, medication.getStrength_value());
        values.put(KEY_STRENGTH_MEASUREMENT, medication.getStrength_measurement());
        values.put(KEY_DOSAGE_INSTRUCTIONS, medication.getDosage_instructions());
        values.put(KEY_TIME_BETWEEN_DOSES, medication.getTime_between_doses());
        values.put(KEY_MAX_UNTIS_PER_DAY, medication.getMax_units_per_day());
        values.put(KEY_MAX_NUMBER_PER_DOSE, medication.getMax_number_per_dose());
        values.put(KEY_MEDICATION_QUANTITY, medication.getMedication_quantity());
        values.put(KEY_USE_BY_DATE, medication.getUse_by_date());
        values.put(KEY_FILL_DATE, medication.getFill_date());
        values.put(KEY_MEDICATION_LOCATION, medication.getMedication_location());
        values.put(KEY_MEDICATION_LOCATION_NAME, medication.getMedication_location_name());
        values.put(KEY_PATIENTNAME, medication.getPatient_name());
        values.put(KEY_IS_MEDICATION_PAUSED, medication.isPaused());
        values.put(KEY_MEDICATION_SCHEDULE_TYPE, medication.getMedication_schedule_type());

        // updating row
        return db.update(TABLE_MEDICATIONS, values, KEY_ID + " = ?"
                , new String[]{String.valueOf(medication.getId())});
    }

    public int deleteMedication(Medication medication) {
        SQLiteDatabase db = this.getWritableDatabase();
        int returnVal = db.delete(TABLE_MEDICATIONS, KEY_ID + " = ?", new String[]{String.valueOf(medication.getId())});
        db.close();
        return returnVal;
    }

    public List<Medication> getMedications() {
        List<Medication> medicationList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_MEDICATIONS + " ORDER BY " + KEY_MEDICATION_NAME + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Medication medication = new Medication();
                medication.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                medication.setName(c.getString(c.getColumnIndex(KEY_MEDICATION_NAME)));
                medication.setNickname(c.getString(c.getColumnIndex(KEY_MEDICATION_NICKNAME)));
                medication.setUserId(c.getInt(c.getColumnIndex(KEY_USERID)));
                medication.setStrength_value(c.getInt(c.getColumnIndex(KEY_STRENGTH_VALUE)));
                medication.setStrength_measurement(c.getString(c.getColumnIndex(KEY_STRENGTH_MEASUREMENT)));
                medication.setDosage_instructions(c.getString(c.getColumnIndex(KEY_DOSAGE_INSTRUCTIONS)));
                medication.setTime_between_doses(c.getInt(c.getColumnIndex(KEY_TIME_BETWEEN_DOSES)));
                medication.setMax_units_per_day(c.getInt(c.getColumnIndex(KEY_MAX_UNTIS_PER_DAY)));
                medication.setMax_number_per_dose(c.getInt(c.getColumnIndex(KEY_MAX_NUMBER_PER_DOSE)));
                medication.setMedication_quantity(c.getInt(c.getColumnIndex(KEY_MEDICATION_QUANTITY)));
                medication.setUse_by_date(c.getString(c.getColumnIndex(KEY_USE_BY_DATE)));
                medication.setFill_date(c.getString(c.getColumnIndex(KEY_FILL_DATE)));
                medication.setMedication_location(c.getInt(c.getColumnIndex(KEY_MEDICATION_LOCATION)));
                medication.setMedication_location_name(c.getString(c.getColumnIndex(KEY_MEDICATION_LOCATION_NAME)));
                medication.setPatient_name(c.getString(c.getColumnIndex(KEY_PATIENTNAME)));
                medication.setPaused(c.getInt(c.getColumnIndex(KEY_IS_MEDICATION_PAUSED)) > 0);
                medication.setMedication_schedule_type(c.getInt(c.getColumnIndex(KEY_MEDICATION_SCHEDULE_TYPE)));

                // adding to list
                medicationList.add(medication);
            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return medicationList;
    }

    public List<Medication> getMedicationsByBin() {
        List<Medication> medicationList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_MEDICATIONS + " ORDER BY " + KEY_MEDICATION_LOCATION + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Medication medication = new Medication();
                medication.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                medication.setName(c.getString(c.getColumnIndex(KEY_MEDICATION_NAME)));
                medication.setNickname(c.getString(c.getColumnIndex(KEY_MEDICATION_NICKNAME)));
                medication.setUserId(c.getInt(c.getColumnIndex(KEY_USERID)));
                medication.setStrength_value(c.getInt(c.getColumnIndex(KEY_STRENGTH_VALUE)));
                medication.setStrength_measurement(c.getString(c.getColumnIndex(KEY_STRENGTH_MEASUREMENT)));
                medication.setDosage_instructions(c.getString(c.getColumnIndex(KEY_DOSAGE_INSTRUCTIONS)));
                medication.setTime_between_doses(c.getInt(c.getColumnIndex(KEY_TIME_BETWEEN_DOSES)));
                medication.setMax_units_per_day(c.getInt(c.getColumnIndex(KEY_MAX_UNTIS_PER_DAY)));
                medication.setMax_number_per_dose(c.getInt(c.getColumnIndex(KEY_MAX_NUMBER_PER_DOSE)));
                medication.setMedication_quantity(c.getInt(c.getColumnIndex(KEY_MEDICATION_QUANTITY)));
                medication.setUse_by_date(c.getString(c.getColumnIndex(KEY_USE_BY_DATE)));
                medication.setFill_date(c.getString(c.getColumnIndex(KEY_FILL_DATE)));
                medication.setMedication_location(c.getInt(c.getColumnIndex(KEY_MEDICATION_LOCATION)));
                medication.setMedication_location_name(c.getString(c.getColumnIndex(KEY_MEDICATION_LOCATION_NAME)));
                medication.setPatient_name(c.getString(c.getColumnIndex(KEY_PATIENTNAME)));
                medication.setPaused(c.getInt(c.getColumnIndex(KEY_IS_MEDICATION_PAUSED)) > 0);
                medication.setMedication_schedule_type(c.getInt(c.getColumnIndex(KEY_MEDICATION_SCHEDULE_TYPE)));

                // adding to list
                medicationList.add(medication);
            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return medicationList;
    }

    public List<Medication> getMedicationsForUser(User user) {
        List<Medication> medicationList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_MEDICATIONS + " WHERE " + KEY_USERID + "=" + user.getId() + " ORDER BY " + KEY_ID + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Medication medication = new Medication();
                medication.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                medication.setName(c.getString(c.getColumnIndex(KEY_MEDICATION_NAME)));
                medication.setNickname(c.getString(c.getColumnIndex(KEY_MEDICATION_NICKNAME)));
                medication.setUserId(c.getInt(c.getColumnIndex(KEY_USERID)));
                medication.setStrength_value(c.getInt(c.getColumnIndex(KEY_STRENGTH_VALUE)));
                medication.setStrength_measurement(c.getString(c.getColumnIndex(KEY_STRENGTH_MEASUREMENT)));
                medication.setDosage_instructions(c.getString(c.getColumnIndex(KEY_DOSAGE_INSTRUCTIONS)));
                medication.setTime_between_doses(c.getInt(c.getColumnIndex(KEY_TIME_BETWEEN_DOSES)));
                medication.setMax_units_per_day(c.getInt(c.getColumnIndex(KEY_MAX_UNTIS_PER_DAY)));
                medication.setMax_number_per_dose(c.getInt(c.getColumnIndex(KEY_MAX_NUMBER_PER_DOSE)));
                medication.setMedication_quantity(c.getInt(c.getColumnIndex(KEY_MEDICATION_QUANTITY)));
                medication.setUse_by_date(c.getString(c.getColumnIndex(KEY_USE_BY_DATE)));
                medication.setFill_date(c.getString(c.getColumnIndex(KEY_FILL_DATE)));
                medication.setMedication_location(c.getInt(c.getColumnIndex(KEY_MEDICATION_LOCATION)));
                medication.setMedication_location_name(c.getString(c.getColumnIndex(KEY_MEDICATION_LOCATION_NAME)));
                medication.setPatient_name(c.getString(c.getColumnIndex(KEY_PATIENTNAME)));
                medication.setPaused(c.getInt(c.getColumnIndex(KEY_IS_MEDICATION_PAUSED)) > 0);
                medication.setMedication_schedule_type(c.getInt(c.getColumnIndex(KEY_MEDICATION_SCHEDULE_TYPE)));

                // adding to list
                medicationList.add(medication);
            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return medicationList;
    }

    public List<Medication> getMedicationsForUser(User user, int scheduleType) {
        List<Medication> medicationList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_MEDICATIONS
                + " WHERE " + KEY_USERID + "=" + user.getId()
                + " AND " + KEY_MEDICATION_SCHEDULE_TYPE + "=" + scheduleType
                + " ORDER BY " + KEY_ID + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Medication medication = new Medication();
                medication.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                medication.setName(c.getString(c.getColumnIndex(KEY_MEDICATION_NAME)));
                medication.setNickname(c.getString(c.getColumnIndex(KEY_MEDICATION_NICKNAME)));
                medication.setUserId(c.getInt(c.getColumnIndex(KEY_USERID)));
                medication.setStrength_value(c.getInt(c.getColumnIndex(KEY_STRENGTH_VALUE)));
                medication.setStrength_measurement(c.getString(c.getColumnIndex(KEY_STRENGTH_MEASUREMENT)));
                medication.setDosage_instructions(c.getString(c.getColumnIndex(KEY_DOSAGE_INSTRUCTIONS)));
                medication.setTime_between_doses(c.getInt(c.getColumnIndex(KEY_TIME_BETWEEN_DOSES)));
                medication.setMax_units_per_day(c.getInt(c.getColumnIndex(KEY_MAX_UNTIS_PER_DAY)));
                medication.setMax_number_per_dose(c.getInt(c.getColumnIndex(KEY_MAX_NUMBER_PER_DOSE)));
                medication.setMedication_quantity(c.getInt(c.getColumnIndex(KEY_MEDICATION_QUANTITY)));
                medication.setUse_by_date(c.getString(c.getColumnIndex(KEY_USE_BY_DATE)));
                medication.setFill_date(c.getString(c.getColumnIndex(KEY_FILL_DATE)));
                medication.setMedication_location(c.getInt(c.getColumnIndex(KEY_MEDICATION_LOCATION)));
                medication.setMedication_location_name(c.getString(c.getColumnIndex(KEY_MEDICATION_LOCATION_NAME)));
                medication.setPatient_name(c.getString(c.getColumnIndex(KEY_PATIENTNAME)));
                medication.setPaused(c.getInt(c.getColumnIndex(KEY_IS_MEDICATION_PAUSED)) > 0);
                medication.setMedication_schedule_type(c.getInt(c.getColumnIndex(KEY_MEDICATION_SCHEDULE_TYPE)));

                // adding to list
                medicationList.add(medication);
            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return medicationList;
    }

    public List<Medication> getMedicationsForUser(User user, boolean isScheduled) {
        List<Medication> medicationList = new ArrayList<>();
        String selectQuery;
        if (isScheduled) {
            selectQuery = "SELECT * FROM " + TABLE_MEDICATIONS
                    + " WHERE " + KEY_USERID + "=" + user.getId()
                    + " AND (" + KEY_MEDICATION_SCHEDULE_TYPE + "=0 OR " + KEY_MEDICATION_SCHEDULE_TYPE + "=2)"  //scheduled or both
                    + " ORDER BY " + KEY_ID + " ASC";
        } else {
            selectQuery = "SELECT * FROM " + TABLE_MEDICATIONS
                    + " WHERE " + KEY_USERID + "=" + user.getId()
                    + " AND (" + KEY_MEDICATION_SCHEDULE_TYPE + "=1 OR " + KEY_MEDICATION_SCHEDULE_TYPE + "=0)" //as-needed or both
                    + " ORDER BY " + KEY_ID + " ASC";
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Medication medication = new Medication();
                medication.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                medication.setName(c.getString(c.getColumnIndex(KEY_MEDICATION_NAME)));
                medication.setNickname(c.getString(c.getColumnIndex(KEY_MEDICATION_NICKNAME)));
                medication.setUserId(c.getInt(c.getColumnIndex(KEY_USERID)));
                medication.setStrength_value(c.getInt(c.getColumnIndex(KEY_STRENGTH_VALUE)));
                medication.setStrength_measurement(c.getString(c.getColumnIndex(KEY_STRENGTH_MEASUREMENT)));
                medication.setDosage_instructions(c.getString(c.getColumnIndex(KEY_DOSAGE_INSTRUCTIONS)));
                medication.setTime_between_doses(c.getInt(c.getColumnIndex(KEY_TIME_BETWEEN_DOSES)));
                medication.setMax_units_per_day(c.getInt(c.getColumnIndex(KEY_MAX_UNTIS_PER_DAY)));
                medication.setMax_number_per_dose(c.getInt(c.getColumnIndex(KEY_MAX_NUMBER_PER_DOSE)));
                medication.setMedication_quantity(c.getInt(c.getColumnIndex(KEY_MEDICATION_QUANTITY)));
                medication.setUse_by_date(c.getString(c.getColumnIndex(KEY_USE_BY_DATE)));
                medication.setFill_date(c.getString(c.getColumnIndex(KEY_FILL_DATE)));
                medication.setMedication_location(c.getInt(c.getColumnIndex(KEY_MEDICATION_LOCATION)));
                medication.setMedication_location_name(c.getString(c.getColumnIndex(KEY_MEDICATION_LOCATION_NAME)));
                medication.setPatient_name(c.getString(c.getColumnIndex(KEY_PATIENTNAME)));
                medication.setPaused(c.getInt(c.getColumnIndex(KEY_IS_MEDICATION_PAUSED)) > 0);
                medication.setMedication_schedule_type(c.getInt(c.getColumnIndex(KEY_MEDICATION_SCHEDULE_TYPE)));

                // adding to list
                medicationList.add(medication);
            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return medicationList;
    }

    public Medication getMedication(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_MEDICATIONS + " WHERE " + KEY_ID + " = " + id;
        Cursor c = db.rawQuery(selectQuery, null);
        Medication medication = null;
        if (c != null) {
            c.moveToFirst();

            medication = new Medication();
            medication.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            medication.setName(c.getString(c.getColumnIndex(KEY_MEDICATION_NAME)));
            medication.setNickname(c.getString(c.getColumnIndex(KEY_MEDICATION_NICKNAME)));
            medication.setUserId(c.getInt(c.getColumnIndex(KEY_USERID)));
            medication.setStrength_value(c.getInt(c.getColumnIndex(KEY_STRENGTH_VALUE)));
            medication.setStrength_measurement(c.getString(c.getColumnIndex(KEY_STRENGTH_MEASUREMENT)));
            medication.setDosage_instructions(c.getString(c.getColumnIndex(KEY_DOSAGE_INSTRUCTIONS)));
            medication.setTime_between_doses(c.getInt(c.getColumnIndex(KEY_TIME_BETWEEN_DOSES)));
            medication.setMax_units_per_day(c.getInt(c.getColumnIndex(KEY_MAX_UNTIS_PER_DAY)));
            medication.setMax_number_per_dose(c.getInt(c.getColumnIndex(KEY_MAX_NUMBER_PER_DOSE)));
            medication.setMedication_quantity(c.getInt(c.getColumnIndex(KEY_MEDICATION_QUANTITY)));
            medication.setUse_by_date(c.getString(c.getColumnIndex(KEY_USE_BY_DATE)));
            medication.setFill_date(c.getString(c.getColumnIndex(KEY_FILL_DATE)));
            medication.setMedication_location(c.getInt(c.getColumnIndex(KEY_MEDICATION_LOCATION)));
            medication.setMedication_location_name(c.getString(c.getColumnIndex(KEY_MEDICATION_LOCATION_NAME)));
            medication.setPatient_name(c.getString(c.getColumnIndex(KEY_PATIENTNAME)));
            medication.setPaused(c.getInt(c.getColumnIndex(KEY_IS_MEDICATION_PAUSED)) > 0);
            medication.setMedication_schedule_type(c.getInt(c.getColumnIndex(KEY_MEDICATION_SCHEDULE_TYPE)));

            c.close();
        }
        db.close();
        return medication;
    }

    public int removeMedication(Medication medication) {
        SQLiteDatabase db = this.getWritableDatabase();
        int returnVal = db.delete(TABLE_MEDICATIONS, KEY_ID + " = ?", new String[]{String.valueOf(medication.getId())});
        db.close();
        return returnVal;
    }

    public int addDispenseTime(DispenseTime dispenseTime) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERID, dispenseTime.getUserId());
        values.put(KEY_DISPENSE_NAME, dispenseTime.getDispenseName());
        values.put(KEY_DISPENSE_TIME, dispenseTime.getDispenseTime());
        values.put(KEY_IS_ACTIVE, dispenseTime.isActive());

        // insert row
        int returnVal = (int) db.insert(TABLE_DISPENSE_TIMES, null, values);
        db.close();
        return returnVal;
    }

    public List<DispenseTime> getDispenseTimes(boolean isActiveOnly) {
        List<DispenseTime> dispenseTimes = new ArrayList<>();
        String selectQuery;
        if (isActiveOnly) {
            selectQuery = "SELECT * FROM " + TABLE_DISPENSE_TIMES + " WHERE " + KEY_IS_ACTIVE + "=1 ORDER BY " + KEY_ID + " ASC";
        } else {
            selectQuery = "SELECT * FROM " + TABLE_DISPENSE_TIMES + " ORDER BY " + KEY_ID + " ASC";
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                DispenseTime dispenseTime = new DispenseTime();
                dispenseTime.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                dispenseTime.setUserId(c.getInt(c.getColumnIndex(KEY_USERID)));
                dispenseTime.setDispenseName(c.getString(c.getColumnIndex(KEY_DISPENSE_NAME)));
                dispenseTime.setDispenseTime(c.getString(c.getColumnIndex(KEY_DISPENSE_TIME)));
                dispenseTime.setActive(c.getInt(c.getColumnIndex(KEY_IS_ACTIVE)) > 0);

                // adding to list
                dispenseTimes.add(dispenseTime);
            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return dispenseTimes;
    }

    public int updateDispenseTime(DispenseTime dispenseTime) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, dispenseTime.getId());
        values.put(KEY_USERID, dispenseTime.getUserId());
        values.put(KEY_DISPENSE_NAME, dispenseTime.getDispenseName());
        values.put(KEY_DISPENSE_TIME, dispenseTime.getDispenseTime());
        values.put(KEY_IS_ACTIVE, dispenseTime.isActive());

        // updating row
        return db.update(TABLE_DISPENSE_TIMES, values, KEY_ID + " = ?"
                , new String[]{String.valueOf(dispenseTime.getId())});
    }

    public int deleteDispenseTime(DispenseTime dispenseTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        int returnVal = db.delete(TABLE_DISPENSE_TIMES, KEY_ID + " = ?", new String[]{String.valueOf(dispenseTime.getId())});
        db.close();
        return returnVal;
    }

    public int addScheduleItem(ScheduleItem scheduleItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERID, scheduleItem.getUserId());
        values.put(KEY_MEDICATION_ID, scheduleItem.getMedicationId());
        values.put(KEY_DISPENSE_TIME_ID, scheduleItem.getDispenseTimeId());
        values.put(KEY_DISPENSE_AMOUNT, scheduleItem.getDispenseAmount());
        values.put(KEY_SCHEDULE_DATE, scheduleItem.getScheduleDate());
        values.put(KEY_SCHEDULE_DAY, scheduleItem.getScheduleDay());
        values.put(KEY_SCHEDULE_TYPE, scheduleItem.getScheduleType());
        //values.put(KEY_DISPENSE_TIME, scheduleItem.getDispenseTime());
        //values.put(KEY_DISPENSE_NAME, scheduleItem.getDispenseName());

        // insert row
        int returnVal = (int) db.insert(TABLE_SCHEDULE, null, values);
        db.close();
        return returnVal;
    }

    public List<ScheduleItem> getScheduledItemsForDay(String sDay, User user) {
        List<ScheduleItem> scheduleItems = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_SCHEDULE + " WHERE " + KEY_USERID + "=" + user.getId()
                + " AND " + KEY_SCHEDULE_DAY + " ='" + sDay + "' ORDER BY " + KEY_DISPENSE_TIME_ID + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {

                ScheduleItem scheduleItem = new ScheduleItem();
                scheduleItem.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                scheduleItem.setUserId(c.getInt(c.getColumnIndex(KEY_USERID)));
                scheduleItem.setMedicationId(c.getInt(c.getColumnIndex(KEY_MEDICATION_ID)));
                scheduleItem.setDispenseTimeId(c.getInt(c.getColumnIndex(KEY_DISPENSE_TIME_ID)));
                scheduleItem.setScheduleType(c.getInt(c.getColumnIndex(KEY_SCHEDULE_TYPE)));
                scheduleItem.setScheduleDate(c.getString(c.getColumnIndex(KEY_SCHEDULE_DATE)));
                scheduleItem.setScheduleDay(c.getString(c.getColumnIndex(KEY_SCHEDULE_DAY)));
                scheduleItem.setDispenseAmount(c.getInt(c.getColumnIndex(KEY_DISPENSE_AMOUNT)));

                // get the following fields from TABLE_DISPENSE_TIMES
                DispenseTime dispenseTime = getDispenseTime(scheduleItem.getDispenseTimeId());
                scheduleItem.setDispenseTime(dispenseTime.getDispenseTime());
                scheduleItem.setDispenseName(dispenseTime.getDispenseName());

                // adding to list
                scheduleItems.add(scheduleItem);
            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return scheduleItems;
    }

    public List<ScheduleItem> getAllScheduledItemsForUser(User user, Medication medication) {
        List<ScheduleItem> scheduleItems = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_SCHEDULE + " WHERE " + KEY_USERID + "=" + user.getId()
                + " AND " + KEY_MEDICATION_ID + "=" + medication.getId() + " ORDER BY " + KEY_SCHEDULE_TYPE + ", " + KEY_DISPENSE_TIME_ID + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {

                ScheduleItem scheduleItem = new ScheduleItem();
                scheduleItem.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                scheduleItem.setUserId(c.getInt(c.getColumnIndex(KEY_USERID)));
                scheduleItem.setMedicationId(c.getInt(c.getColumnIndex(KEY_MEDICATION_ID)));
                scheduleItem.setDispenseTimeId(c.getInt(c.getColumnIndex(KEY_DISPENSE_TIME_ID)));
                scheduleItem.setScheduleType(c.getInt(c.getColumnIndex(KEY_SCHEDULE_TYPE)));
                scheduleItem.setScheduleDate(c.getString(c.getColumnIndex(KEY_SCHEDULE_DATE)));
                scheduleItem.setScheduleDay(c.getString(c.getColumnIndex(KEY_SCHEDULE_DAY)));

                // get the following fields from TABLE_DISPENSE_TIMES
                DispenseTime dispenseTime = getDispenseTime(scheduleItem.getDispenseTimeId());
                scheduleItem.setDispenseTime(dispenseTime.getDispenseTime());
                scheduleItem.setDispenseName(dispenseTime.getDispenseName());

                // adding to list
                scheduleItems.add(scheduleItem);
            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return scheduleItems;
    }

    public List<ScheduleItem> getAllScheduledItemsForUser(User user) {
        List<ScheduleItem> scheduleItems = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_SCHEDULE + " WHERE " + KEY_USERID + "=" + user.getId()
                + " ORDER BY " + KEY_SCHEDULE_TYPE + ", " + KEY_DISPENSE_TIME_ID + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {

                ScheduleItem scheduleItem = new ScheduleItem();
                scheduleItem.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                scheduleItem.setUserId(c.getInt(c.getColumnIndex(KEY_USERID)));
                scheduleItem.setMedicationId(c.getInt(c.getColumnIndex(KEY_MEDICATION_ID)));
                scheduleItem.setDispenseTimeId(c.getInt(c.getColumnIndex(KEY_DISPENSE_TIME_ID)));
                scheduleItem.setScheduleType(c.getInt(c.getColumnIndex(KEY_SCHEDULE_TYPE)));
                scheduleItem.setScheduleDate(c.getString(c.getColumnIndex(KEY_SCHEDULE_DATE)));
                scheduleItem.setScheduleDay(c.getString(c.getColumnIndex(KEY_SCHEDULE_DAY)));

                // get the following fields from TABLE_DISPENSE_TIMES
                DispenseTime dispenseTime = getDispenseTime(scheduleItem.getDispenseTimeId());
                scheduleItem.setDispenseTime(dispenseTime.getDispenseTime());
                scheduleItem.setDispenseName(dispenseTime.getDispenseName());

                // adding to list
                scheduleItems.add(scheduleItem);
            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return scheduleItems;
    }

    public List<ScheduleItem> getScheduleItemsByMedication(Medication medication) {
        List<ScheduleItem> scheduleItems = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_SCHEDULE + " WHERE " + KEY_MEDICATION_ID + "=" + medication.getId();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                ScheduleItem scheduleItem = new ScheduleItem();
                scheduleItem.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                scheduleItem.setUserId(c.getInt(c.getColumnIndex(KEY_USERID)));
                scheduleItem.setMedicationId(c.getInt(c.getColumnIndex(KEY_MEDICATION_ID)));
                scheduleItem.setDispenseTimeId(c.getInt(c.getColumnIndex(KEY_DISPENSE_TIME_ID)));
                scheduleItem.setScheduleType(c.getInt(c.getColumnIndex(KEY_SCHEDULE_TYPE)));
                scheduleItem.setScheduleDate(c.getString(c.getColumnIndex(KEY_SCHEDULE_DATE)));
                scheduleItem.setScheduleDay(c.getString(c.getColumnIndex(KEY_SCHEDULE_DAY)));
                scheduleItem.setDispenseAmount(c.getInt(c.getColumnIndex(KEY_DISPENSE_AMOUNT)));

                // get the following fields from TABLE_DISPENSE_TIMES
                DispenseTime dispenseTime = getDispenseTime(scheduleItem.getDispenseTimeId());
                scheduleItem.setDispenseTime(dispenseTime.getDispenseTime());
                scheduleItem.setDispenseName(dispenseTime.getDispenseName());
                scheduleItems.add(scheduleItem);
            } while (c.moveToNext());
        }
        c.close();
        db.close();

        return scheduleItems;
    }

    public ScheduleItem getScheduleItem(int id) {
        ScheduleItem scheduleItem = null;
        String selectQuery = "SELECT * FROM " + TABLE_SCHEDULE + " WHERE " + KEY_ID + "=" + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            scheduleItem = new ScheduleItem();
            scheduleItem.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            scheduleItem.setUserId(c.getInt(c.getColumnIndex(KEY_USERID)));
            scheduleItem.setMedicationId(c.getInt(c.getColumnIndex(KEY_MEDICATION_ID)));
            scheduleItem.setDispenseTimeId(c.getInt(c.getColumnIndex(KEY_DISPENSE_TIME_ID)));
            scheduleItem.setScheduleType(c.getInt(c.getColumnIndex(KEY_SCHEDULE_TYPE)));
            scheduleItem.setScheduleDate(c.getString(c.getColumnIndex(KEY_SCHEDULE_DATE)));
            scheduleItem.setScheduleDay(c.getString(c.getColumnIndex(KEY_SCHEDULE_DAY)));
            scheduleItem.setDispenseAmount(c.getInt(c.getColumnIndex(KEY_DISPENSE_AMOUNT)));

            // get the following fields from TABLE_DISPENSE_TIMES
            DispenseTime dispenseTime = getDispenseTime(scheduleItem.getDispenseTimeId());
            scheduleItem.setDispenseTime(dispenseTime.getDispenseTime());
            scheduleItem.setDispenseName(dispenseTime.getDispenseName());

        }
        c.close();
        db.close();
        return scheduleItem;
    }

    public List<ScheduleItem> getScheduledItemsForDate(String sDate, User user) {
        List<ScheduleItem> scheduleItems = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_SCHEDULE + " WHERE " + KEY_USERID + "=" + user.getId()
                + " AND " + KEY_SCHEDULE_DATE + " ='" + sDate + "' ORDER BY " + KEY_DISPENSE_TIME_ID + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {

                ScheduleItem scheduleItem = new ScheduleItem();
                scheduleItem.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                scheduleItem.setUserId(c.getInt(c.getColumnIndex(KEY_USERID)));
                scheduleItem.setMedicationId(c.getInt(c.getColumnIndex(KEY_MEDICATION_ID)));
                scheduleItem.setDispenseTimeId(c.getInt(c.getColumnIndex(KEY_DISPENSE_TIME_ID)));
                scheduleItem.setScheduleType(c.getInt(c.getColumnIndex(KEY_SCHEDULE_TYPE)));
                scheduleItem.setScheduleDate(c.getString(c.getColumnIndex(KEY_SCHEDULE_DATE)));
                scheduleItem.setScheduleDay(c.getString(c.getColumnIndex(KEY_SCHEDULE_DAY)));
                scheduleItem.setDispenseAmount(c.getInt(c.getColumnIndex(KEY_DISPENSE_AMOUNT)));

                // get the following fields from TABLE_DISPENSE_TIMES
                DispenseTime dispenseTime = getDispenseTime(scheduleItem.getDispenseTimeId());
                scheduleItem.setDispenseTime(dispenseTime.getDispenseTime());
                scheduleItem.setDispenseName(dispenseTime.getDispenseName());

                // adding to list
                scheduleItems.add(scheduleItem);
            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return scheduleItems;
    }

    public List<ScheduleItem> getDailyScheduledItems(User user) {
        List<ScheduleItem> scheduleItems = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_SCHEDULE + " WHERE " + KEY_USERID + "=" + user.getId()
                + " AND " + KEY_SCHEDULE_DATE + " IS NULL AND "
                + KEY_SCHEDULE_DAY + " IS NULL ORDER BY " + KEY_DISPENSE_TIME_ID + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {

                ScheduleItem scheduleItem = new ScheduleItem();
                scheduleItem.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                scheduleItem.setUserId(c.getInt(c.getColumnIndex(KEY_USERID)));
                scheduleItem.setMedicationId(c.getInt(c.getColumnIndex(KEY_MEDICATION_ID)));
                scheduleItem.setDispenseTimeId(c.getInt(c.getColumnIndex(KEY_DISPENSE_TIME_ID)));
                scheduleItem.setScheduleType(c.getInt(c.getColumnIndex(KEY_SCHEDULE_TYPE)));
                scheduleItem.setScheduleDate(c.getString(c.getColumnIndex(KEY_SCHEDULE_DATE)));
                scheduleItem.setScheduleDay(c.getString(c.getColumnIndex(KEY_SCHEDULE_DAY)));

                // get the following fields from TABLE_DISPENSE_TIMES
                DispenseTime dispenseTime = getDispenseTime(scheduleItem.getDispenseTimeId());
                scheduleItem.setDispenseTime(dispenseTime.getDispenseTime());
                scheduleItem.setDispenseName(dispenseTime.getDispenseName());

                // adding to list
                scheduleItems.add(scheduleItem);
            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return scheduleItems;
    }

    public DaySchedule getDaySchedule(User user) {
        List<ScheduleItem> scheduleItems = new ArrayList<>();
        DaySchedule daySchedule = new DaySchedule();

        //get current day (SUNDAY, MONDAY, etc.) scheduled items
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_WEEK) - 1;
        DayOfWeek dayOfWeek = DayOfWeek.of(day);
        String sDay = dayOfWeek.name();
        scheduleItems.addAll(getScheduledItemsForDay(sDay, user));

        //get current date scheduled items
        String sDate = new SimpleDateFormat("M/d/yyyy", Locale.getDefault()).format(Calendar.getInstance().getTime());
        scheduleItems.addAll(getScheduledItemsForDate(sDate, user));

        //get daily scheduled items
        scheduleItems.addAll(getDailyScheduledItems(user));

        //sort list by time
        Collections.sort(scheduleItems, new Comparator<ScheduleItem>() {
            DateFormat f = new SimpleDateFormat("h:mm a", Locale.getDefault());

            @Override
            public int compare(ScheduleItem o1, ScheduleItem o2) {
                try {
                    return f.parse(o1.getDispenseTime()).compareTo(f.parse(o2.getDispenseTime()));
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });

        daySchedule.setScheduleItemList(scheduleItems);
        daySchedule.setDay(sDay);
        daySchedule.setDate(sDate);

        return daySchedule;
    }

    public int removeMedicationFromSchedule(User user, Medication medication) {
        SQLiteDatabase db = this.getWritableDatabase();
        int returnVal = db.delete(TABLE_SCHEDULE, KEY_USERID + " = ? AND "
                + KEY_MEDICATION_ID + " = ?", new String[]{String.valueOf(user.getId()), String.valueOf(medication.getId())});
        db.close();
        return returnVal;
    }

    public List<NotificationSetting> getNotificationSettingsForUser(User user, int notification_setting_id) {
        List<NotificationSetting> settingList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_NOTIFICATION_SETTINGS
                + " WHERE " + KEY_USERID + "=" + user.getId() + " AND " + KEY_NOTIFICATION_SETTING_ID + "=" + notification_setting_id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                NotificationSetting setting = new NotificationSetting();
                setting.setContactId(c.getInt(c.getColumnIndex(KEY_CONTACT_ID)));
                setting.setUserId(c.getInt(c.getColumnIndex(KEY_USERID)));
                setting.setName(c.getString(c.getColumnIndex(KEY_NOTIFICATION_SETTING_NAME)));
                setting.setId(c.getInt(c.getColumnIndex(KEY_NOTIFICATION_SETTING_ID)));
                setting.setTextSelected(c.getInt(c.getColumnIndex(KEY_IS_TEXT_SELECTED)) > 0);
                setting.setVoiceSelected(c.getInt(c.getColumnIndex(KEY_IS_VOICE_SELECTED)) > 0);
                setting.setEmailSelected(c.getInt(c.getColumnIndex(KEY_IS_EMAIL_SELECTED)) > 0);

                // adding to list
                settingList.add(setting);
            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return settingList;
    }

    public Contact getContact(int contactId) {
        Contact contact = null;
        String selectQuery = "SELECT * FROM " + TABLE_CONTACTS + " WHERE " + KEY_ID + "=" + contactId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            contact = new Contact();
            contact.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            contact.setUserid(c.getInt(c.getColumnIndex(KEY_USERID)));
            contact.setName(c.getString(c.getColumnIndex(KEY_CONTACT_NAME)));
            contact.setTextNumber(c.getString(c.getColumnIndex(KEY_CONTACT_TEXT_NUMBER)));
            contact.setVoiceNumber(c.getString(c.getColumnIndex(KEY_CONTACT_VOICE_NUMBER)));
            contact.setEmail(c.getString(c.getColumnIndex(KEY_CONTACT_EMAIL)));
            contact.setCategory(c.getString(c.getColumnIndex(KEY_CONTACT_CATEGORY)));
            contact.setSelected(c.getInt(c.getColumnIndex(KEY_IS_CONTACT_SELECTED)) > 0);
            contact.setTextNumberSelected(c.getInt(c.getColumnIndex(KEY_IS_TEXT_SELECTED)) > 0);
            contact.setVoiceNumberSelected(c.getInt(c.getColumnIndex(KEY_IS_VOICE_SELECTED)) > 0);
            contact.setEmailSelected(c.getInt(c.getColumnIndex(KEY_IS_EMAIL_SELECTED)) > 0);
            contact.setRelationship(c.getInt(c.getColumnIndex(KEY_RELATIONSHIP)));
        }
        c.close();
        db.close();
        return contact;
    }

    public DispenseTime getDispenseTime(int dispenseTimeId) {
        String selectQuery = "SELECT * FROM " + TABLE_DISPENSE_TIMES + " WHERE "
                + KEY_ID + "=" + dispenseTimeId
                + " ORDER BY " + KEY_ID + " ASC";
        DispenseTime dispenseTime = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                dispenseTime = new DispenseTime();
                dispenseTime.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                dispenseTime.setUserId(c.getInt(c.getColumnIndex(KEY_USERID)));
                dispenseTime.setDispenseName(c.getString(c.getColumnIndex(KEY_DISPENSE_NAME)));
                dispenseTime.setDispenseTime(c.getString(c.getColumnIndex(KEY_DISPENSE_TIME)));
                dispenseTime.setActive(c.getInt(c.getColumnIndex(KEY_IS_ACTIVE)) > 0);

            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return dispenseTime;
    }

    /**
     * Adds bin detection data
     *
     * @param binDetection BinDetection object
     * @return number of rows inserted
     */
    public int addBinDetection(BinDetection binDetection) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        // convert Bitmap to BLOB
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        binDetection.getDetectImage().compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageArray = stream.toByteArray();

        values.put(KEY_BIN_DETECT_IMAGE, imageArray);
        values.put(KEY_BIN_PILL_COUNT, binDetection.getPillCount());
        values.put(KEY_BIN_UPPER_LEFT_X, binDetection.getUpperLeftX());
        values.put(KEY_BIN_UPPER_LEFT_Y, binDetection.getUpperLeftY());
        values.put(KEY_BIN_LOWER_RIGHT_X, binDetection.getLowerRightX());
        values.put(KEY_BIN_LOWER_RIGHT_Y, binDetection.getLowerRightY());
        values.put(KEY_BIN_PILL_TEMPLATE, binDetection.getPillTemplate());
        values.put(KEY_BIN_RUN_ANALYSIS, binDetection.isRunAnalysis());
        values.put(KEY_BIN_FILL_LEVEL, binDetection.getFillLevel());

        // insert row
        int returnVal = (int) db.insert(TABLE_BINS, null, values);
        db.close();
        return returnVal;
    }

    /**
     * Returns bin detection data
     *
     * @param id bin id
     * @return BinDetection object
     */
    public BinDetection getBinDetection(int id) {
        BinDetection binDetection = null;
        String selectQuery = "SELECT * FROM " + TABLE_BINS + " WHERE " + KEY_ID + "=" + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            binDetection = new BinDetection();
            binDetection.setBinId(c.getInt(c.getColumnIndex(KEY_ID)));
            binDetection.setPillCount(c.getInt(c.getColumnIndex(KEY_BIN_PILL_COUNT)));
            binDetection.setUpperLeftX(c.getInt(c.getColumnIndex(KEY_BIN_UPPER_LEFT_X)));
            binDetection.setUpperLeftY(c.getInt(c.getColumnIndex(KEY_BIN_UPPER_LEFT_Y)));
            binDetection.setLowerRightX(c.getInt(c.getColumnIndex(KEY_BIN_LOWER_RIGHT_X)));
            binDetection.setLowerRightY(c.getInt(c.getColumnIndex(KEY_BIN_LOWER_RIGHT_Y)));
            binDetection.setPillTemplate(c.getString(c.getColumnIndex(KEY_BIN_PILL_TEMPLATE)));
            binDetection.setFillLevel(c.getInt(c.getColumnIndex(KEY_BIN_FILL_LEVEL)));
            binDetection.setRunAnalysis(c.getInt(c.getColumnIndex(KEY_BIN_RUN_ANALYSIS)) > 0);

            // convert BLOB to Bitmap
            byte[] imageArray = c.getBlob(c.getColumnIndex(KEY_BIN_DETECT_IMAGE));
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length);
            binDetection.setDetectImage(bitmap);
        }
        c.close();
        db.close();
        return binDetection;
    }

    /**
     * Deletes bin detection data
     *
     * @param id bin id
     * @return number of rows deleted
     */
    public int deleteBinDetection(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int returnVal = db.delete(TABLE_BINS, KEY_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return returnVal;
    }

    public ScheduleItem getScheduleItemByDispenseTimeId(int id) {
        ScheduleItem scheduleItem = null;
        String selectQuery = "SELECT * FROM " + TABLE_SCHEDULE + " WHERE " + KEY_DISPENSE_TIME_ID + "=" + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            scheduleItem = new ScheduleItem();
            scheduleItem.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            scheduleItem.setUserId(c.getInt(c.getColumnIndex(KEY_USERID)));
            scheduleItem.setMedicationId(c.getInt(c.getColumnIndex(KEY_MEDICATION_ID)));
            scheduleItem.setDispenseTimeId(c.getInt(c.getColumnIndex(KEY_DISPENSE_TIME_ID)));
            scheduleItem.setScheduleType(c.getInt(c.getColumnIndex(KEY_SCHEDULE_TYPE)));
            scheduleItem.setScheduleDate(c.getString(c.getColumnIndex(KEY_SCHEDULE_DATE)));
            scheduleItem.setScheduleDay(c.getString(c.getColumnIndex(KEY_SCHEDULE_DAY)));
            scheduleItem.setDispenseAmount(c.getInt(c.getColumnIndex(KEY_DISPENSE_AMOUNT)));

            // get the following fields from TABLE_DISPENSE_TIMES
            DispenseTime dispenseTime = getDispenseTime(scheduleItem.getDispenseTimeId());
            scheduleItem.setDispenseTime(dispenseTime.getDispenseTime());
            scheduleItem.setDispenseName(dispenseTime.getDispenseName());

        }
        c.close();
        db.close();
        return scheduleItem;
    }

    public int updateScheduleItem(ScheduleItem scheduleItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERID, scheduleItem.getUserId());
        values.put(KEY_MEDICATION_ID, scheduleItem.getMedicationId());
        values.put(KEY_DISPENSE_TIME_ID, scheduleItem.getDispenseTimeId());
        values.put(KEY_DISPENSE_AMOUNT, scheduleItem.getDispenseAmount());
        values.put(KEY_SCHEDULE_DATE, scheduleItem.getScheduleDate());
        values.put(KEY_SCHEDULE_DAY, scheduleItem.getScheduleDay());
        values.put(KEY_SCHEDULE_TYPE, scheduleItem.getScheduleType());

        // update row
        return db.update(TABLE_SCHEDULE, values, KEY_ID + " = ?"
                , new String[]{String.valueOf(scheduleItem.getId())});
    }

    public int deleteScheduleItems(User user, Medication medication) {
        SQLiteDatabase db = this.getWritableDatabase();
        int returnVal = db.delete(TABLE_SCHEDULE, KEY_USERID + " = ? AND "
                + KEY_MEDICATION_ID + " = ? ", new String[]{String.valueOf(user.getId())
                , String.valueOf(medication.getId())});
        db.close();
        return returnVal;
    }

    public ScheduleItem getScheduleItem(User user, Medication medication, int dispenseTimeId) {
        ScheduleItem scheduleItem = null;
        String selectQuery = "SELECT * FROM " + TABLE_SCHEDULE + " WHERE " + KEY_USERID + "=" + user.getId()
                + " AND " + KEY_MEDICATION_ID + "=" + medication.getId()
                + " AND " + KEY_DISPENSE_TIME_ID + "=" + dispenseTimeId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            scheduleItem = new ScheduleItem();
            scheduleItem.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            scheduleItem.setUserId(c.getInt(c.getColumnIndex(KEY_USERID)));
            scheduleItem.setMedicationId(c.getInt(c.getColumnIndex(KEY_MEDICATION_ID)));
            scheduleItem.setDispenseTimeId(c.getInt(c.getColumnIndex(KEY_DISPENSE_TIME_ID)));
            scheduleItem.setScheduleType(c.getInt(c.getColumnIndex(KEY_SCHEDULE_TYPE)));
            scheduleItem.setScheduleDate(c.getString(c.getColumnIndex(KEY_SCHEDULE_DATE)));
            scheduleItem.setScheduleDay(c.getString(c.getColumnIndex(KEY_SCHEDULE_DAY)));
            scheduleItem.setDispenseAmount(c.getInt(c.getColumnIndex(KEY_DISPENSE_AMOUNT)));

            // get the following fields from TABLE_DISPENSE_TIMES
            DispenseTime dispenseTime = getDispenseTime(scheduleItem.getDispenseTimeId());
            scheduleItem.setDispenseTime(dispenseTime.getDispenseTime());
            scheduleItem.setDispenseName(dispenseTime.getDispenseName());

        }
        c.close();
        db.close();
        return scheduleItem;
    }

    /**
     * Returns a unique list of patient names that have been entered into the system
     *
     * @return String list of patient names
     */
    public List<String> getPatientNames(User user) {
        List<String> patientNames = new ArrayList<>();
        String selectQuery = "SELECT DISTINCT " + KEY_PATIENTNAME + " FROM " + TABLE_MEDICATIONS
                + " WHERE " + KEY_USERID + "=" + user.getId()
                + " ORDER BY " + KEY_PATIENTNAME + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                String patientName = c.getString(c.getColumnIndex(KEY_PATIENTNAME));
                patientNames.add(patientName);
            } while (c.moveToNext());
            c.close();
        }
        db.close();

        return patientNames;
    }

    public int deleteNotificationSettingsForContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        int returnVal = db.delete(TABLE_NOTIFICATION_SETTINGS, KEY_CONTACT_ID + " = ?"
                , new String[]{String.valueOf(contact.getId())});
        db.close();
        return returnVal;
    }

    public boolean isDuplicateMedicationFound(Medication medication) {
        // check to see if this medication is already in the db
        boolean isFound = false;
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_MEDICATIONS + " WHERE " + KEY_MEDICATION_NAME + "='" + medication.getName()
                + "' AND " + KEY_STRENGTH_MEASUREMENT + "='" + medication.getStrength_measurement() + "'";
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null && c.getCount() > 0) {
            isFound = true;
            c.close();
        }
        db.close();

        return isFound;
    }
}