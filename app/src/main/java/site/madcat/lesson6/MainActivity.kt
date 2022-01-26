package site.madcat.lesson6

import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.net.ConnectivityManager
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
    private var myReceiver=MyReceiver()
    private val uiHandler: Handler by lazy { Handler(mainLooper) }
    private val handlerThread: HandlerThread=HandlerThread("HideAppThread").apply { start() }
    private val workerHandler: Handler by lazy { Handler(handlerThread.looper) }

    private val TAG="@@@@"

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
        workThread()
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
            var i=1
            while (i < 10000) {
                if (binding.checkBox.isChecked) {
                    i=i + i * 2
                    Thread.sleep(1000)
                    uiHandler.post {
                        binding.textView.text=i.toString()
                    }
                }
            }
        }
    }

    fun Services() {
        val serviceIntent=Intent(this, MyService::class.java)

        binding.startButton.setOnClickListener {
            serviceIntent.putExtra("message", "ololo")
            startService(serviceIntent)
        }

        binding.stopButton.setOnClickListener {
            stopService(serviceIntent)
        }

        binding.bindButton.setOnClickListener {
            bindService(serviceIntent, connection, BIND_AUTO_CREATE)
        }
        binding.unbindButton.setOnClickListener {
            unbindService(connection)
        }
    }

}