// IMyAidlInterface2.aidl
package com.fxc.libCanWrapperNDK;

// Declare any non-default types here with import statements
import com.fxc.libCanWrapperNDK.ICanStCallback;

interface IMyAidlInterface2 {
    String getCanData();
    void setCanData(String aString);
    String getReqCanData(String aString);
    //void register(ICanStCallback callback);
}