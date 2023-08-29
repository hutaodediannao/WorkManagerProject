package com.example.workmanagerproject

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.work.BackoffPolicy
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkContinuation
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.impl.workers.ConstraintTrackingWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.concurrent.TimeUnit
import kotlin.math.log

private const val TAG = "MainActivity"
const val DOWNLOAD_TAG = "download_tag"

/**
 * version:1.0.1
 */
class MainActivity : AppCompatActivity() {

    private val mWorkManager by lazy { WorkManager.getInstance(this) }
    private val mStartBtn by findView<Button>(R.id.btnStart)
    private val mStopBtn by findView<Button>(R.id.btnStop)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)
        mStartBtn.setOnClickListener { start() }
        mStopBtn.setOnClickListener {
            mWorkManager.getWorkInfosByTag(DOWNLOAD_TAG).cancel(true)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun start() {
        val workInfoList = mWorkManager.getWorkInfosByTag(DOWNLOAD_TAG).get()
        if (workInfoList.isNullOrEmpty()) {
            Log.i(TAG, "start: workInfoList is null or empty!")

            //设置条件
            val inputData = Data.Builder().put("key", "参数").build()
            val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresCharging(true).build()
            val workRequest =
                PeriodicWorkRequest.Builder(DownloadWork::class.java, 15, TimeUnit.MINUTES)
                    .setInitialDelay(5, TimeUnit.SECONDS)
//                    .setInputData(inputData)
                    .setConstraints(constraints).addTag(DOWNLOAD_TAG).build()
            mWorkManager.enqueue(workRequest)

//            val ontTimeRequest1 = OneTimeWorkRequest.Builder(DownloadWork::class.java)
//                .addTag(DOWNLOAD_TAG)
//                .build()
//            val ontTimeRequest2 = OneTimeWorkRequest.Builder(DownloadWork::class.java)
//                .addTag(DOWNLOAD_TAG)
//                .build()
//            mWorkManager.beginWith(ontTimeRequest1)
//                .then(ontTimeRequest2)
//                .enqueue()
        } else {
            Log.i(TAG, "start: workInfoList is not empty!")
            workInfoList.forEach {
                Log.i(TAG, "start: ${it.id}")
                Log.i(TAG, "start: ${it.tags}")
                Log.i(TAG, "start: ${it.outputData}")
                Log.i(TAG, "start: ${it.progress}")

            }
        }
    }

    private fun <T : View> AppCompatActivity.findView(@IdRes id: Int): Lazy<T> =
        lazy { findViewById(id) }

}

class DownloadWork(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {
    @SuppressLint("RestrictedApi")
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            Log.i(TAG, "doWork: 开始下载...")
            delay(2000)
            Log.i(TAG, "doWork: 下载中...")
            delay(3000)
            Log.i(TAG, "doWork: 下载完成...")
            delay(3000)
            val outPutData = Data.Builder().put("key", "下载完成!").build()
            Result.success(outPutData)
        }
    }

}