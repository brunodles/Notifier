package android.bruno.notifier;

import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by bruno on 18/08/14.
 */
public abstract class Reflector {

    public static final String TAG = "Reflector";
    public static boolean debuggable = true;

    private Method method;
    private Object object;
    private String methodName;

    public Reflector(Object object, String methodName) {
        this.object = object;
        this.methodName = methodName;
    }

    protected static void Loge(String tag, String msg, Throwable tr){
        if (debuggable)
            Log.e(tag, msg, tr);
    }

    protected void tryToFindMethodByName() {
        try {
            method = object.getClass().getDeclaredMethod(methodName, null);
            if (!method.isAccessible())
                method.setAccessible(true);
        } catch (NoSuchMethodException e) {
            Loge(TAG, "IllegalAccessException?", e);
            throw new RuntimeException(String.format("The method \"%s\" isn't accessible!", methodName), e);
        }
    }

    protected void invoke(){
        try {
            method.invoke(object, null);
        } catch (IllegalAccessException e) {
            Loge(TAG, "IllegalAccessException? on invoke "+methodName, e);
        } catch (InvocationTargetException e) {
            Loge(TAG, "InvocationTargetException on invoke "+methodName, e);
        } catch (Exception e) {
            Loge(TAG, "Exception on invoke "+methodName, e);
        }
    }
}
