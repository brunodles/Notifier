package android.bruno.notifier;

/**
 * Created by bruno on 18/08/14.
 */
public class ReflectionRunable extends Reflector implements Runnable{

    public ReflectionRunable(Object object, String methodName) {
        super(object, methodName);
    }

    @Override
    public void run() {
        invoke();
    }
}
