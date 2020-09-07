package resu.io;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Handler;
import android.text.TextUtils;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import io.mob.resu.reandroidsdk.AppConstants;
import io.mob.resu.reandroidsdk.AppLifecyclePresenter;
import io.mob.resu.reandroidsdk.IDeepLinkInterface;
import io.mob.resu.reandroidsdk.MRegisterUser;
import io.mob.resu.reandroidsdk.ReAndroidSDK;
import io.mob.resu.reandroidsdk.error.Log;


/**
 * This class echoes a string called from JavaScript.
 */
public class ReCordovaPlugin extends CordovaPlugin {
    private static final String TAG = "ReCordovaPlugin";
    private Handler handler = new Handler();
    public static CordovaWebView gWebView;
    public static JSONObject jsonObject;
    String OldScreenName = null;
    String newScreenName = null;
    CallbackContext NotificationCallbacks;
    ArrayList<JSONObject> notificationByObject;
    private Calendar oldCalendar = Calendar.getInstance();
    private Calendar sCalendar = Calendar.getInstance();
    String tag = "ReCordovaPlugin.getViewJson";

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("SocketCallBacks")) {
                if (intent.getExtras().getString("state").equalsIgnoreCase("start")) {
                    handler.removeCallbacks(runnable);
                    handler.postDelayed(runnable, 1000);
                } else if (intent.getExtras().getString("state").equalsIgnoreCase("stop")) {
                    handler.removeCallbacks(runnable);
                }
            }
        }


    };

    public ReCordovaPlugin() {

    }

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        gWebView = webView;
        AppConstants.isHyBird = true;
        AppConstants.isCordova = true;
        android.util.Log.d(TAG, "==> ReCordovaPlugin initialize");
        LocalBroadcastManager.getInstance(cordova.getActivity()).registerReceiver(mMessageReceiver, new IntentFilter("SocketCallBacks"));

        ReAndroidSDK.getInstance(cordova.getActivity()).getCampaignData(new IDeepLinkInterface() {
            @Override
            public void onInstallDataReceived(String data) {
                String callBack = "javascript:" + "ResulticksDeeplinkData" + "(" + data + ")";
                ReCordovaPlugin.gWebView.sendJavascript(callBack);
                android.util.Log.e(TAG, "==> ReCordovaPlugin getCampaignData: " + data);

            }

            @Override
            public void onDeepLinkData(String data) {
                android.util.Log.e(TAG, "==> ReCordovaPlugin getCampaignData: " + data);
                String callBack = "javascript:" + "ResulticksDeeplinkData" + "(" + data + ")";
                ReCordovaPlugin.gWebView.sendJavascript(callBack);

            }

        });
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        switch (action) {

            case "userRegister":
                this.userRegister(args, callbackContext);
                break;

            case "customEvent":
                this.customEvent(args, callbackContext);
                break;

            case "screenNavigation":
                this.screenNavigation(args, callbackContext);
                break;

            case "locationUpdate":
                this.locationUpdate(args, callbackContext);
                break;

            case "getNotification":
                this.getNotification(args, callbackContext);
                break;

            case "deleteNotification":
                this.deleteNotification(args, callbackContext);
                break;

            case "deleteNotificationByNotificationId":
                this.deleteNotificationByNotificationId(args, callbackContext);
                break;

            case "deleteNotificationByCampaignId":
                this.deleteNotificationByCampaignId(args, callbackContext);
                break;

            case "notificationPayLoadReceiver":
                this.notificationPayLoadReceiver(args, callbackContext);
                break;

            case "updateViewsJson": // Connect Socket
                this.updateViewsJson(args, callbackContext);
                break;

            case "updateFieldTrackData": // tracked data
                this.UpdateFieldTrackData(args, callbackContext);
                break;

            case "getFieldTrackData": // Enable field track
                this.getFieldTrackData(args, callbackContext);
                break;

            case "getReadNotificationCount": // Enable field track
                this.getReadNotificationCount(args, callbackContext);
                break;

            case "getUnReadNotificationCount": // Enable field track
                this.getUnReadNotificationCount(args, callbackContext);
                break;

            case "readNotification": // Enable field track
                this.readNotification(args, callbackContext);
                break;

            case "unReadNotification": // Enable field track
                this.unReadNotification(args, callbackContext);
                break;
            case "notificationCTAClicked": // Enable field track
                this.notificationCTAClicked(args, callbackContext);
                break;

            case "appConversionTracking": // Enable field track
                this.appConversionTracking(args, callbackContext);
                break;

            default:

                break;

        }
        return false;
    }

    private void appConversionTracking(JSONArray args, CallbackContext callbackContext) {
        try {
            ReAndroidSDK.getInstance(cordova.getActivity()).appConversionTracking();
        } catch (Exception e) {
            Log.e("appConversionTracking  Exception: ", String.valueOf(e.getMessage()));
        }

    }

    private void notificationCTAClicked(JSONArray message, CallbackContext callbackContext) {


        if (message != null && message.length() > 0) {

            try {
                JSONObject jsonObject = message.getJSONObject(0);
                ReAndroidSDK.getInstance(cordova.getActivity()).notificationCTAClicked(jsonObject.optString("campaignId"), jsonObject.optString("actionId"));
                Log.e("notificationCTAClicked : ", " successfully");
            } catch (Exception e) {
                Log.e("notificationCTAClicked  Exception: ", String.valueOf(e.getMessage()));
            }
        } else {
            Log.e("notificationCTAClicked  Exception : ", "Expected one non-empty string argument.");
        }

    }


    private void getFieldTrackData(JSONArray args, CallbackContext callbackContext) {

        try {
            JSONObject jsonObject = args.getJSONObject(0);
            String screenName = jsonObject.getString("screenName").replace("/", "___");
            ArrayList<JSONObject> list = ReAndroidSDK.getInstance(cordova.getActivity()).getFieldTrackData(screenName);
            if (list != null) {
                callbackContext.success(new JSONArray(list).toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void UpdateFieldTrackData(JSONArray args, CallbackContext callbackContext) {

        try {
            AppConstants.hybridFieldTrack = null;
            JSONObject jsonObject = args.getJSONObject(0);
            AppConstants.hybridFieldTrack = jsonObject.getJSONArray("fieldTrack");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void updateViewsJson(JSONArray args, CallbackContext callbackContext) throws JSONException {

        JSONObject jsonObject = args.getJSONObject(0);
        JSONArray jsonArray = jsonObject.getJSONArray("views");
        ArrayList<JSONObject> screenViews = new ArrayList<>();
        ArrayList<Integer> viewChildrens = new ArrayList<>();
        JSONObject rootView = new JSONObject();
        rootView.put("left", 0);
        rootView.put("top", 22);
        rootView.put("category", "parant");
        rootView.put("id", Math.floor(100000 + Math.random() * 900000));
        rootView.put("isShow", false);
        rootView.put("translationX", 0);
        rootView.put("translationY", 0);
        rootView.put("scrollX", jsonObject.getInt("scrollX"));
        rootView.put("scrollY", jsonObject.getInt("scrollY"));
        rootView.put("isWebView", false);
        rootView.put("viewType", "Linear");
        rootView.put("screenName", jsonObject.getString("screenName").replace("/", "___"));
        rootView.put("height", pxToDp(cordova.getActivity().getWindow().getDecorView().getRootView().getHeight()));
        rootView.put("width", pxToDp(cordova.getActivity().getWindow().getDecorView().getRootView().getWidth()));
        rootView.put("mainScreenName", cordova.getActivity().getClass().getSimpleName());
        rootView.put("activityName", cordova.getActivity().getClass().getName());
        screenViews.add(rootView);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
            jsonObject2.put("mainScreenName", cordova.getActivity().getClass().getSimpleName());
            jsonObject2.put("activityName", cordova.getActivity().getClass().getName());
            jsonObject2.put("screenName", jsonObject.getString("screenName").replace("/", "___"));
            screenViews.add(jsonObject2);
            jsonObject2.put("id", jsonObject2.getInt("id"));
            viewChildrens.add(jsonObject2.getInt("id"));
        }
        screenViews.get(0).put("subviews", viewChildrens);
        AppConstants.hybridViewsJson = new JSONArray(screenViews);
        AppConstants.HybridScreenUrl = cordova.getActivity().getClass().getSimpleName();
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 1000);
    }


    private static int pxToDp(int px) {
        return (int) ((float) px / Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * Socket interval screen update
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String callBack = "javascript:" + tag + "('')";
                    ReCordovaPlugin.gWebView.sendJavascript(callBack);
                    handler.removeCallbacks(runnable);
                    handler.postDelayed(runnable, 1000);
                }
            });
        }
    };

    private void notificationPayLoadReceiver(final JSONArray args, CallbackContext callbackContext) {

    }

    private void deleteNotification(JSONArray message, CallbackContext callbackContext) {

        if (message != null && message.length() > 0) {

            try {
                JSONObject jsonObject = message.getJSONObject(0);
                ReAndroidSDK.getInstance(cordova.getActivity()).deleteNotificationByObject(jsonObject);
                Log.e("Notification : ", "Delete successfully");
            } catch (Exception e) {
                Log.e("Delete Notification Exception: ", String.valueOf(e.getMessage()));
            }
        } else {
            Log.e("Delete Notification Exception : ", "Expected one non-empty string argument.");
        }
    }

    private void deleteNotificationByNotificationId(JSONArray message, CallbackContext callbackContext) {

        if (message != null && message.length() > 0) {

            try {
                JSONObject jsonObject = message.getJSONObject(0);
                ReAndroidSDK.getInstance(cordova.getActivity()).deleteNotificationByNotificationId(jsonObject.optString("notificationId"));
                Log.e("Notification : ", "Delete successfully");
            } catch (Exception e) {
                Log.e("Delete Notification Exception: ", String.valueOf(e.getMessage()));
            }
        } else {
            Log.e("Delete Notification Exception : ", "Expected one non-empty string argument.");
        }
    }

    private void deleteNotificationByCampaignId(JSONArray message, CallbackContext callbackContext) {

        if (message != null && message.length() > 0) {

            try {
                JSONObject jsonObject = message.getJSONObject(0);
                ReAndroidSDK.getInstance(cordova.getActivity()).deleteNotificationByCampaignId(jsonObject.optString("campaignId"));
                Log.e("Notification : ", "Delete successfully");
            } catch (Exception e) {
                Log.e("Delete Notification Exception: ", String.valueOf(e.getMessage()));
            }
        } else {
            Log.e("Delete Notification Exception : ", "Expected one non-empty string argument.");
        }
    }


    private void getNotification(final JSONArray args, CallbackContext callbackContext) {
        try {
            notificationByObject = ReAndroidSDK.getInstance(cordova.getActivity()).getNotificationByObject();
            JSONArray jsonArray = new JSONArray(notificationByObject);
            callbackContext.success(jsonArray.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getReadNotificationCount(final JSONArray args, CallbackContext callbackContext) {
        try {
            int count = ReAndroidSDK.getInstance(cordova.getActivity()).getReadNotificationCount();
            callbackContext.success("" + count);
            Log.e("getReadNotificationCount : ", " successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getUnReadNotificationCount(final JSONArray args, CallbackContext callbackContext) {
        try {
            int count = ReAndroidSDK.getInstance(cordova.getActivity()).getUnReadNotificationCount();
            callbackContext.success("" + count);
            Log.e("getUnReadNotificationCount : ", " successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readNotification(JSONArray message, CallbackContext callbackContext) {

        if (message != null && message.length() > 0) {

            try {
                JSONObject jsonObject = message.getJSONObject(0);
                ReAndroidSDK.getInstance(cordova.getActivity()).readNotification(jsonObject.optString("campaignId"));
                Log.e("readNotification : ", " successfully");
            } catch (Exception e) {
                Log.e("readNotification Notification Exception: ", String.valueOf(e.getMessage()));
            }
        } else {
            Log.e("readNotification  Exception : ", "Expected one non-empty string argument.");
        }
    }

    private void unReadNotification(JSONArray message, CallbackContext callbackContext) {

        if (message != null && message.length() > 0) {

            try {
                JSONObject jsonObject = message.getJSONObject(0);
                ReAndroidSDK.getInstance(cordova.getActivity()).unReadNotification(jsonObject.optString("campaignId"));
                Log.e("readNotification : ", " successfully");
            } catch (Exception e) {
                Log.e("readNotification Notification Exception: ", String.valueOf(e.getMessage()));
            }
        } else {
            Log.e("readNotification  Exception : ", "Expected one non-empty string argument.");
        }
    }


    private void locationUpdate(final JSONArray message, CallbackContext callbackContext) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {

                if (message != null && message.length() > 0) {
                    try {
                        JSONObject jsonObject = message.getJSONObject(0);
                        double latitude = jsonObject.optDouble("latitude");
                        double longitude = jsonObject.optDouble("longitude");
                        if (latitude != 0 && longitude != 0)
                            ReAndroidSDK.getInstance(cordova.getActivity()).onLocationUpdate(latitude, longitude);
                    } catch (Exception e) {
                        Log.e("User events Exception: ", String.valueOf(e.getMessage()));
                    }
                } else {
                    Log.e("User events Exception: ", "Expected one non-empty string argument.");
                }
            }

        });

    }


    private void screenNavigation(final JSONArray message, CallbackContext callbackContext) {

        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (message != null && message.length() > 0) {
                    try {
                        JSONObject jsonObject = message.getJSONObject(0);
                        screenTracking(jsonObject.optString("screenName"));
                        OldScreenName = newScreenName;
                        newScreenName = jsonObject.optString("screenName");

                    } catch (Exception e) {
                        Log.e("userNavigation Exception: ", String.valueOf(e.getMessage()));
                    }

                } else {
                    Log.e("userNavigation Exception: ", "Expected one non-empty string argument.");
                }
            }
        });

    }

    private void userRegister(final JSONArray message, CallbackContext callbackContext) {

        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (message != null && message.length() > 0) {
                    try {
                        JSONObject jsonObject = message.getJSONObject(0);
                        MRegisterUser registerUser = new MRegisterUser();
                        registerUser.setUserUniqueId(jsonObject.optString("uniqueId"));
                        registerUser.setName(jsonObject.optString("name"));
                        registerUser.setEmail(jsonObject.optString("email"));
                        registerUser.setPhone(jsonObject.optString("phone"));
                        registerUser.setAge(jsonObject.optString("age"));
                        registerUser.setGender(jsonObject.optString("gender"));
                        registerUser.setDeviceToken(jsonObject.optString("token"));
                        registerUser.setProfileUrl(jsonObject.optString("profileUrl"));
                        ReAndroidSDK.getInstance(cordova.getActivity()).onDeviceUserRegister(registerUser);
                    } catch (Exception e) {
                        Log.e("register Exception: ", String.valueOf(e.getMessage()));
                    }

                } else {

                    Log.e("register Exception: ", "Expected one non-empty string argument.");
                }
            }
        });
    }

    private void customEvent(final JSONArray message, CallbackContext callbackContext) {

        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (message != null && message.length() > 0) {
                    try {
                        JSONObject jsonObject = message.getJSONObject(0);
                        String eventName = jsonObject.optString("eventName");
                        JSONObject eventData = jsonObject.optJSONObject("data");

                        if (TextUtils.isEmpty(eventName)) {
                            Log.e("Event name can't be empty!", "");
                            return;
                        }
                        if (TextUtils.isEmpty(eventData.toString()))
                            ReAndroidSDK.getInstance(cordova.getActivity()).onTrackEvent(eventName);
                        else
                            ReAndroidSDK.getInstance(cordova.getActivity()).onTrackEvent(eventData, eventName);

                    } catch (Exception e) {
                        Log.e("User events Exception: ", String.valueOf(e.getMessage()));
                    }
                } else {
                    Log.e("User events Exception: ", "Expected one non-empty string argument.");
                }
            }
        });
    }

    private void screenTracking(String screenName) {

        try {

            if (sCalendar == null)
                sCalendar = Calendar.getInstance();

            oldCalendar = sCalendar;
            sCalendar = Calendar.getInstance();

            if (OldScreenName != null) {
                AppLifecyclePresenter.getInstance().onSessionStop(cordova.getActivity(), oldCalendar, sCalendar, OldScreenName, null, null);
                AppLifecyclePresenter.getInstance().onSessionStartFragment(cordova.getActivity(), OldScreenName, null);
            }
            if (newScreenName == null)
                newScreenName = screenName;

        } catch (Exception e) {
            Log.e("screenTracking Exception: ", "" + e.getMessage());

        }
    }

    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);
        screenTracking(newScreenName);
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        oldCalendar = Calendar.getInstance();
        sCalendar = Calendar.getInstance();
    }


}
