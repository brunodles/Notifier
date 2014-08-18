package android.bruno.notifier;

import android.view.View;

import java.lang.reflect.InvocationTargetException;

/**
 * This class intent to simplify access to activity's buttons.
 *
 * Created by bruno on 17/08/14.
 */
public class ReflectionListener extends Reflector implements View.OnClickListener{

    private static final String TAG = "ReflectionListener";

    public ReflectionListener(Object object, String methodName) {
        super(object, methodName);
        tryToFindMethodByName();
    }

    @Override
    public void onClick(View v) {
        invoke();
    }

}