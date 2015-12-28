package android.bruno.notifier;

import android.graphics.Color;

/**
 * Created by bruno on 28/12/15.
 */
public class ColorValues {
    public final int alpha;
    public final int red;
    public final int green;
    public final int blue;

    private ColorValues(int alpha, int red, int green, int blue) {
        this.alpha = alpha;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public static ColorValues from(int color) {
        ColorValues values = new ColorValues(Color.alpha(color),
                Color.red(color), Color.green(color), Color.blue(color));
        return values;
    }

    public boolean isBlack() {
        return (red + green + blue) == 0;
    }

    public String toHexRGB() {
        return String.format("#%02x%02x%02x", red, green, blue);
    }
}
