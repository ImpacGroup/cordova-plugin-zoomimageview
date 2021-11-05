package de.impacgroup.zoomimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import de.impacgroup.zoomimageview.module.ZoomImageActivity;

public class ZoomImageView extends CordovaPlugin {

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if ("presentImage".equals(action)) {
            ImageInfo info = new Gson().fromJson(args.getString(0), ImageInfo.class);
            String base64String = info.getImage().replace("data:image/png;base64,", "").replace("data:image/jpeg;base64,", "");
            byte[] decodedString = Base64.decode(base64String, Base64.NO_WRAP);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            int[] point = null;
            if (info.getImageRect() != null) {
                point = new int[]{info.getImageRect().getX(), info.getImageRect().getY()};
                point[0] = pxFromDp(cordova.getContext(), point[0]);
                point[1] = pxFromDp(cordova.getContext(), point[1]);
            }
            AppCompatActivity activity = this.cordova.getActivity();
            int[] finalPoint = point;
            if (bitmap != null) {
                activity.runOnUiThread(() -> {
                    ZoomImageActivity.Companion.present(activity, bitmap, finalPoint, info.getCloseButton());
                });
                return true;
            }
            return false;
        }
        return super.execute(action, args, callbackContext);
    }

    public static int pxFromDp(final Context context, final float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}
