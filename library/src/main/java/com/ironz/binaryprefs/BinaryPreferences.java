package com.ironz.binaryprefs;

import android.content.SharedPreferences;
import com.ironz.binaryprefs.exception.ExceptionHandler;
import com.ironz.binaryprefs.files.FileAdapter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class BinaryPreferences implements SharedPreferences {

    private final FileAdapter fileAdapter;
    private final ExceptionHandler exceptionHandler;

    BinaryPreferences(FileAdapter fileAdapter, ExceptionHandler exceptionHandler) {
        this.fileAdapter = fileAdapter;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public Map<String, ?> getAll() {
        try {
            return getStringMapInternal();
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
        return new HashMap<>();
    }

    @Override
    public String getString(String key, String defValue) {
        try {
            return getStringInternal(key);
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
        return defValue;
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        try {
            return getStringsInternal(key);
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
        return defValues;
    }

    @Override
    public int getInt(String key, int defValue) {
        try {
            return getIntInternal(key);
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
        return defValue;
    }

    @Override
    public long getLong(String key, long defValue) {
        try {
            return getLongInternal(key);
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
        return defValue;
    }

    @Override
    public float getFloat(String key, float defValue) {
        try {
            return getFloatInternal(key);
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
        return defValue;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        try {
            return getBooleanInternal(key);
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
        return defValue;
    }

    @Override
    public boolean contains(String key) {
        return containsInternal(key);
    }

    private Map<String, ?> getStringMapInternal() {
        Map<String, Object> map = new HashMap<>();
        for (String name : fileAdapter.names()) {

            String[] split = name.split("\\.");
            String suffix = split[split.length - 1];
            String prefName = split[0];

            if (suffix.equals(Constants.STRING_FILE_POSTFIX_WITHOUT_DOT)) {
                map.put(prefName, getStringInternal(prefName));
                continue;
            }
            if (suffix.equals(Constants.INTEGER_FILE_POSTFIX_WITHOUT_DOT)) {
                map.put(prefName, getIntInternal(prefName));
                continue;
            }
            if (suffix.equals(Constants.LONG_FILE_POSTFIX_WITHOUT_DOT)) {
                map.put(prefName, getLongInternal(prefName));
                continue;
            }
            if (suffix.equals(Constants.FLOAT_FILE_POSTFIX_WITHOUT_DOT)) {
                map.put(prefName, getFloatInternal(prefName));
                continue;
            }
            if (suffix.equals(Constants.BOOLEAN_FILE_POSTFIX_WITHOUT_DOT)) {
                map.put(prefName, getBooleanInternal(prefName));
                continue;
            }
            if (suffix.equals(Constants.STRING_SET_FILE_POSTFIX_WITHOUT_DOT)) {
                map.put(prefName, getStringsInternal(prefName));
            }
        }
        return map;
    }

    private String getStringInternal(String key) {
        byte[] bytes = fileAdapter.fetch(key + Constants.STRING_FILE_POSTFIX);
        return new String(bytes);
    }

    private Set<String> getStringsInternal(String key) {
        final HashSet<String> strings = new HashSet<>(0);
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            String name = key + "." + i + Constants.STRING_SET_FILE_POSTFIX;
            if (fileAdapter.contains(name)) {
                byte[] bytes = fileAdapter.fetch(name);
                strings.add(new String(bytes));
                continue;
            }
            break;
        }
        return strings;
    }

    private int getIntInternal(String key) {
        byte[] bytes = fileAdapter.fetch(key + Constants.INTEGER_FILE_POSTFIX);
        return Bits.intFromBytes(bytes);
    }

    private long getLongInternal(String key) {
        byte[] bytes = fileAdapter.fetch(key + Constants.LONG_FILE_POSTFIX);
        return Bits.longFromBytes(bytes);
    }

    private float getFloatInternal(String key) {
        byte[] bytes = fileAdapter.fetch(key + Constants.FLOAT_FILE_POSTFIX);
        return Bits.floatFromBytes(bytes);
    }

    private boolean getBooleanInternal(String key) {
        byte[] bytes = fileAdapter.fetch(key + Constants.BOOLEAN_FILE_POSTFIX);
        return Bits.booleanFromBytes(bytes);
    }

    private boolean containsInternal(String key) {
        for (String s : fileAdapter.names()) {
            if (s.split("\\.", 2)[0].equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Editor edit() {
        return new BinaryPreferencesEditor(fileAdapter, exceptionHandler);
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }
}