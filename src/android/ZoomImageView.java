package de.impacgroup.zoomimageview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Base64;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import de.impacgroup.zoomimageview.module.ViewState;
import de.impacgroup.zoomimageview.module.ZoomImageActivity;

public class ZoomImageView extends CordovaPlugin {

    private CallbackContext presentCallbackContext;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        LocalBroadcastManager.getInstance(cordova.getContext()).registerReceiver(receiver, new IntentFilter(ZoomImageActivity.ZOOM_IMAGE_STATE_ACTION));
    }

    @Override
    public void onDestroy() {
        if (cordova.getActivity().isFinishing()) {
            LocalBroadcastManager.getInstance(cordova.getContext()).unregisterReceiver(receiver);
        }
        super.onDestroy();

    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if ("presentImage".equals(action)) {
            presentCallbackContext = callbackContext;
            ImageInfo info = new Gson().fromJson(args.getString(0), ImageInfo.class);
            String base64String = info.getImage().replace("data:image/png;base64,", "").replace("data:image/jpeg;base64,", "");
            byte[] decodedString = Base64.decode(base64String, Base64.NO_WRAP);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            AppCompatActivity activity = this.cordova.getActivity();

            if (bitmap != null) {
                activity.runOnUiThread(() -> {
                    ZoomImageActivity.Companion.present(activity, bitmap, info.getImageRect(), info.getCloseButton());
                });
                return true;
            }
            return false;
        }
        return super.execute(action, args, callbackContext);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(ZoomImageActivity.ZOOM_IMAGE_STATE_KEY)) {
                String key = intent.getStringExtra(ZoomImageActivity.ZOOM_IMAGE_STATE_KEY);
                ViewState state = ViewState.Companion.getFor(key);
                switch (state) {
                    case DID_CLOSE:
                        didClose();
                        break;
                    case WILL_CLOSE:
                        willClose();
                        break;
                    default: {
                        break;
                    }
                }
            }
        }

        public void willClose() {
            if (presentCallbackContext != null) {
                PluginResult result = new PluginResult(PluginResult.Status.OK, "willClose");
                result.setKeepCallback(true);
                presentCallbackContext.sendPluginResult(result);
            }
        }

        public void didClose() {
            if (presentCallbackContext != null) {
                PluginResult result = new PluginResult(PluginResult.Status.OK, "didClose");
                presentCallbackContext.sendPluginResult(result);
                presentCallbackContext = null;
            }
        }
    };

    public static int pxFromDp(final Context context, final float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    /**
     * Calculates the height of the status bar.
     * @return int value for height
     */
    private int getStatusBarHeight() {
        Rect rectangle = new Rect();
        Window window = cordova.getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        return rectangle.top;
    }
}
