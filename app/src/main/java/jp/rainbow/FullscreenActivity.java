package jp.rainbow;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Choreographer;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.util.Random;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends Activity {


    float colorDuration = 5f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);
        final View content = findViewById(R.id.content);

        View v = content;
        while (v != null) {
            if (v.getFitsSystemWindows()) {
                v.setFitsSystemWindows(false);
            }
            ViewParent parent = v.getParent();
            if (parent instanceof View) {
                v = (View) parent;
            } else {
                break;
            }
        }

        Window window = getWindow();
        final WindowManager.LayoutParams attributes = window.getAttributes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        attributes.screenBrightness = 1f;
        window.setAttributes(attributes);

        window.setStatusBarColor(0);
        window.setNavigationBarColor(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.setNavigationBarDividerColor(0);
        }
        WindowCompat.setDecorFitsSystemWindows(window, false);

        WindowInsetsControllerCompat insetsController = WindowCompat.getInsetsController(window, content);
        if (insetsController != null) {
            insetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            insetsController.hide(~0);
        }

        final Choreographer choreographer = Choreographer.getInstance();
        choreographer.postFrameCallback(new Choreographer.FrameCallback() {
            long lastFrameTimeNanos = 0;
            int lastColor = randomColor();
            int nextColor = randomColor();
            float colorProgress = 0f;


            @Override
            public void doFrame(long frameTimeNanos) {
                choreographer.postFrameCallback(this);
                float time = (float)((frameTimeNanos - lastFrameTimeNanos) / 1000000000.0);
                lastFrameTimeNanos = frameTimeNanos;
                if (time > 1f) {
                    time = 1f;
                }
                colorProgress += time;
                if (colorProgress >= colorDuration) {
                    colorProgress %= colorDuration;
                    lastColor = nextColor;
                    nextColor = randomColor();
                }

                float blend = colorProgress / colorDuration;


                content.setBackgroundColor(hsvLerp(lastColor, nextColor, blend));
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 25) {
            // Down
            colorDuration /= 2f;
            colorDuration = Math.max(0.01f, Math.min(colorDuration, 100f));
            return true;
        } else if (keyCode == 24) {
            // Up
            colorDuration *= 2f;
            colorDuration = Math.max(0.01f, Math.min(colorDuration, 100f));
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private static final Random random = new Random();
    private static final float[] hsvTmp0 = new float[3];
    private static final float[] hsvTmp1 = new float[3];

    private static int randomColor() {
        hsvTmp0[0] = random.nextInt(360);
        hsvTmp0[1] = 1f;
        hsvTmp0[2] = 1f;
        final int argb = Color.HSVToColor(255, hsvTmp0);
        return Color.argb(Color.alpha(argb), Math.max(Color.red(argb) + 20, 255), Math.max(Color.green(argb) + 10, 255), Math.max(Color.blue(argb) + 20, 255));
    }

    private static int hsvLerp(int a, int b, float t) {
        float[] aHsv = hsvTmp0;
        float[] bHsv = hsvTmp1;
        Color.colorToHSV(a, aHsv);
        Color.colorToHSV(b, bHsv);

        aHsv[0] = lerpAngleDeg(aHsv[0], bHsv[0], t);
        aHsv[1] = lerp(aHsv[1], bHsv[1], t);
        aHsv[2] = lerp(aHsv[2], bHsv[2], t);
        return Color.HSVToColor(255, aHsv);
    }

    private static float lerp(float a, float b, float t) {
        // Numerically stable approach (https://math.stackexchange.com/a/1798323)
        if (t < 0.5f) {
            return a + (b - a) * t;
        } else {
            return b - (b - a) * (1f - t);
        }
    }

    public static float lerpAngleDeg (float fromDegrees, float toDegrees, float progress) {
        float delta = ((toDegrees - fromDegrees + 360 + 180) % 360) - 180;
        return (fromDegrees + delta * progress + 360) % 360;
    }
}