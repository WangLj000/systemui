package com.android.settingslib.deviceinfo;

import android.net.NetworkInfo;
import android.os.SystemProperties;
import androidx.annotation.VisibleForTesting;


import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import androidx.annotation.VisibleForTesting;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.settingslib.R;
import com.android.settingslib.core.lifecycle.Lifecycle;


import android.util.Log;
public class AbstractEthernetMacAddressPreferenceController extends AbstractConnectivityPreferenceController{
    @VisibleForTesting
    static final String KEY_ETHERNET_MAC_ADDRESS = "ethernet_mac_address";
    @VisibleForTesting
    static final int OFF = 0;
    @VisibleForTesting
    static final int ON = 1;
    private static final String KEY_ROOT_MAC = "ro.boot.mac";
    private static final String TAG = "AbstractEthernetMacAddressPreferenceController";
    private static final String[] CONNECTIVITY_INTENTS = {
            ConnectivityManager.CONNECTIVITY_ACTION,
            WifiManager.ACTION_LINK_CONFIGURATION_CHANGED,
            WifiManager.NETWORK_STATE_CHANGED_ACTION,
    };

    private Preference mEthernetMacAddress;
    private Context mContext;

    public AbstractEthernetMacAddressPreferenceController(Context context, Lifecycle lifecycle) {
        super(context, lifecycle);
        mContext=context;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getPreferenceKey() {
        return KEY_ETHERNET_MAC_ADDRESS;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        if (isAvailable()) {
            mEthernetMacAddress = screen.findPreference(KEY_ETHERNET_MAC_ADDRESS);
            updateConnectivity();
        }
    }

    @Override
    protected String[] getConnectivityIntents() {
        return CONNECTIVITY_INTENTS;
    }

    @SuppressLint("HardwareIds")
    @Override
    protected void updateConnectivity() {
        String macAddress = getMacAddress();
        if(macAddress.equals("0")){
            Log.w(TAG, "Unable to read ethernet mac address from system properties");
            macAddress = getEthernetMac();
        }
        if (TextUtils.isEmpty(macAddress)) {
            mEthernetMacAddress.setSummary(R.string.status_unavailable);
        } else {
            mEthernetMacAddress.setSummary(macAddress);
        }
    }

    private static String getMacAddress(){
        return SystemProperties.get(KEY_ROOT_MAC,"0").toLowerCase();
    }

    private String getEthernetMac() {
        String ethMac = "";
        ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        Log.w(TAG, "Get ethernet mac address from ConnectivityManager");
        if (info != null) {
            ethMac = info.getExtraInfo();
            Log.d(TAG, "ethernet mac = " + ethMac);
        } else {
            Log.e(TAG, "info is null !");
        }
        return ethMac;
    }

}
