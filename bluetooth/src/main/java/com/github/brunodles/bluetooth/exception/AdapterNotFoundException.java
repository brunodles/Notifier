package com.github.brunodles.bluetooth.exception;

/**
 * Created by bruno on 17/03/16.
 */
public class AdapterNotFoundException extends RuntimeException {
    public AdapterNotFoundException() {
        super("Hey dev, looks like this device don't have a bluetooth adapter. You need to set a AdapterErrorListener to manage this error.");
    }
}
