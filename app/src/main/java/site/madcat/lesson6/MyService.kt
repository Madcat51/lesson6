package site.madcat.lesson6

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

class MyService : Service() {

    private val TAG ="@@@"
    private val binder = MyBinder()

    override fun onCreate() {
        Log.d(TAG, "onCreate() called")
        super.onCreate()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy() called")
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(
            TAG,
            "onStartCommand() message = ${intent?.extras?.getString("message")} called with: intent = $intent, flags = $flags, startId = $startId"
        )
        Thread {
            Thread.sleep(1_000)
            if (startId == 5) {
                Log.d(TAG, "stopSelf() startId = $startId")
                stopSelf(startId)
            }
        }.start()
        return super.onStartCommand(intent, flags, startId)
    }
    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "onBind() called with: intent = $intent")
       return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind() called with: intent = $intent")
        return super.onUnbind(intent)
    }


    class MyBinder : Binder() {
        fun getService() = MyService@this
    }
}