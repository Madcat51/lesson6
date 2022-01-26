package site.madcat.lesson6

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log

class MyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent?.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            val networkInfo: NetworkInfo?=
                intent!!.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO)
            if (networkInfo!!.isConnected) {
                Log.d("Network_State", "Network status - ok")
            } else {
                Log.d("Network_State", "Network status - error")
            }
        }
    }
}