// IMyServiceAidlInterface.aidl
package com.example.automusic.aidl;

// Declare any non-default types here with import statements

interface IMyServiceAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
            
    String setMusicInfo(String path);
    void getMusicInfo();
}