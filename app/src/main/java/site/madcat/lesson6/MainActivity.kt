package site.madcat.lesson6

import android.content.*
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.util.Log
import site.madcat.lesson6.databinding.ActivityMainBinding
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val uiHandler: Handler by lazy { Handler(mainLooper) }
    private val handlerThread: HandlerThread=HandlerThread("HideAppThread").apply { start() }
    private val workerHandler: Handler by lazy { Handler(handlerThread.looper) }
    private val TAG="@@@@"


    private val myReceiver: BroadcastReceiver=object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                val networkInfo: NetworkInfo?=
                    intent!!.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO)
                if (networkInfo!!.isConnected) {
                    Log.d("Network_State", "Network status - ok")
                } else {
                    Log.d("Network_State", "Network status - error")
                }
                workThread()
            }
        }
    }
    private val connection=object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            Log.d(TAG, "onServiceConnected() called with: name = $name, binder = $binder")
            val myBinder=binder as MyService.MyBinder
            myBinder.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "onServiceDisconnected() called with: name = $name")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //   workThread()
        Services()
    }

    override fun onResume() {
        super.onResume()

        val intentFilter=IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(myReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause();
        unregisterReceiver(myReceiver);
    }

    override fun onDestroy() {
        handlerThread.quit()
        super.onDestroy()

    }

    fun workThread() {
        workerHandler.post {
            val serviceIntent=Intent(this, MyService::class.java)
                bindService(serviceIntent, connection, BIND_AUTO_CREATE)
        }
    }

    fun Services() {
        val serviceIntent=Intent(this, MyService::class.java)

        binding.bindButton.setOnClickListener {
            bindService(serviceIntent, connection, BIND_AUTO_CREATE)
        }
        binding.unbindButton.setOnClickListener {
            unbindService(connection)
        }
    }

}