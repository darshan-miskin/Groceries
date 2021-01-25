package com.gne.groceries.util

import android.content.Context
import android.net.ConnectivityManager


fun isNetworkAvailable(context: Context): Boolean {
    var haveConnectedWifi = false
    var haveConnectedMobile = false
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        ?: return false
    val netInfo = connectivityManager.allNetworkInfo
    for (ni in netInfo) {
        if (ni.typeName.equals("WIFI", ignoreCase = true)) if (ni.isConnected) haveConnectedWifi = true
        if (ni.typeName.equals("MOBILE", ignoreCase = true)) if (ni.isConnected) haveConnectedMobile = true
    }
    return haveConnectedWifi || haveConnectedMobile
}
