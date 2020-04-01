package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.TrafficStats
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.models.ModelCalls
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
//    private lateinit var networkStats : NetworkStatsManager
//    private lateinit var  connectivityManager : ConnectivityManager
    private val TAG = "PermissionDemo"
    private val REQUEST_CODE = 1
//    private val READ_PHONE_STATE_REQUEST_CODE = 2

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        askPermission()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun askPermission() {
            val permission = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CALL_LOG)

//            val permissionMSISDN = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_NUMBERS)
            val permissionPHONESTATE = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE)
            val permissionSMS = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_SMS)
            val permissionLOCATION = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)

            val permissionUSAGE = ContextCompat.checkSelfPermission(this,Manifest.permission.PACKAGE_USAGE_STATS)


            if (permission != PackageManager.PERMISSION_GRANTED
//                || permissionMSISDN != PackageManager.PERMISSION_GRANTED
                || permissionPHONESTATE != PackageManager.PERMISSION_GRANTED
                || permissionSMS != PackageManager.PERMISSION_GRANTED
                || permissionLOCATION != PackageManager.PERMISSION_GRANTED
                || permissionUSAGE != PackageManager.PERMISSION_GRANTED
            ) {
                makeRequest()
            }else{
                loadData()
            }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(
                Manifest.permission.READ_CALL_LOG
                ,Manifest.permission.READ_PHONE_NUMBERS
                ,Manifest.permission.READ_PHONE_STATE
                ,Manifest.permission.READ_SMS
                ,Manifest.permission.PACKAGE_USAGE_STATS
            ),
            REQUEST_CODE)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        if(requestCode==REQUEST_CODE) loadData()
//        when (requestCode) {
//            REQUEST_CODE -> {
//                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                    Log.i(TAG, "Permission has been denied by user")
//                } else {
//                    Log.i(TAG, "Permission has been granted by user")
//                }
//            }
//
//        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun loadData(){
        var list = getCallLogs(this)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission", "ServiceCast")
    private fun getCallLogs(context: Context):ArrayList<ModelCalls>{
        val callDetails = ArrayList<ModelCalls>()
        val contentUri = CallLog.Calls.CONTENT_URI


        val dateTime = System.currentTimeMillis()
        val whereValue = arrayOf<String>((dateTime-86400000).toString(),dateTime.toString())

//        val manager = context.packageManager
//        val info = manager.getApplicationInfo("com.example.app", 0)
//        val uid = info.uid

        val telephonyManager =  context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val subId = telephonyManager.subscriberId

        val service = context.getSystemService(NetworkStatsManager::class.java)
        val bucket =
            service.queryDetails(ConnectivityManager.TYPE_MOBILE, subId, dateTime-86400000, dateTime)

        bucket.


//      networkStats.queryDetails(
//            ConnectivityManager.TYPE_MOBILE,
//            subId,
//            1585471164000,
//            1585553006584
//        )


        val  received = TrafficStats.getMobileRxBytes()
        val send = TrafficStats.getMobileTxBytes()
        val total = received + send

        val mStartRX = TrafficStats.getTotalRxBytes();
        val mStartTX = TrafficStats.getTotalTxBytes();
        val totalM = mStartRX + mStartTX

        val pcRx = TrafficStats.getTotalRxPackets();
        val pcTx = TrafficStats.getTotalTxPackets();

        val mcRx = TrafficStats.getMobileTxPackets();
        val mcTx = TrafficStats.getMobileRxPackets();


//        val tx1  = TrafficStats.getUidRxBytes(uid)
//        val rx1 =TrafficStats.getUidTxBytes(uid)
//
//        Log.i(TAG, tx1.toString()+" xxxx")
//        Log.i(TAG, rx1.toString()+" xxxx")



        Log.i(TAG, total.toString()+" GB")
        Log.i(TAG, send.toString()+" GB")
        Log.i(TAG, received.toString()+" GB")

        Log.i(TAG, totalM.toString()+" GB1")
        Log.i(TAG, mStartRX.toString()+" GB1")
        Log.i(TAG, mStartTX.toString()+" GB1")


        Log.i(TAG, pcRx.toString()+" GB11")
        Log.i(TAG, pcTx.toString()+" GB11")


        Log.i(TAG, mcRx.toString()+" GB11")
        Log.i(TAG, mcTx.toString()+" GB11")


//        var bucket: NetworkStats.Bucket
//        var nws : NetworkStatsManager








        try{
            val cursor = context.contentResolver.query(contentUri,null
//                ,CallLog.Calls.DATE+" BETWEEN 1585471164000 AND 1585553006584 "
                ,CallLog.Calls.DATE+" BETWEEN ? AND ? " //BETWEEN low_expression AND high_expression
                , whereValue
                ,null
                ,null)

            val nameUri = cursor?.getColumnIndex(CallLog.Calls.CACHED_LOOKUP_URI)
            val number = cursor?.getColumnIndex(CallLog.Calls.NUMBER)
            val duration = cursor?.getColumnIndex(CallLog.Calls.DURATION)
            val date = cursor?.getColumnIndex(CallLog.Calls.DATE)
            val type = cursor?.getColumnIndex(CallLog.Calls.TYPE)

            Log.i(TAG, "1111111111111111111111111111")
            Log.i(TAG, date.toString())
            Log.i(TAG, number.toString())
            Log.i(TAG, duration.toString())
            Log.i(TAG, dateTime.toString())



            if (cursor != null) {
                Log.i(TAG, "222222222222222222222")
                if(cursor.moveToFirst()){
                    Log.i(TAG, "33333333333333333333333")

                    do{
                        val callType = when(cursor.getInt(type!!)){
                            CallLog.Calls.INCOMING_TYPE->"Incoming"
                            CallLog.Calls.OUTGOING_TYPE->"Outgoing"
                            CallLog.Calls.MISSED_TYPE->"Missed"
                            CallLog.Calls.REJECTED_TYPE->"Rejected"
                            else->"Undefied"
                        }
                        val phoneNumber = cursor.getString(number!!)
//                        val callNameUri = cursor.getString(nameUri!!)
                        val callDate = cursor.getString(date!!)
                        val callDaytime = Date(callDate.toLong()).toString()
                        val callDuration = cursor.getString(duration!!)

                        Log.i(TAG, "FUCK V4 HI BRO")
                        Log.i(TAG, callDate)
                        Log.i(TAG, phoneNumber)
                        Log.i(TAG, callDaytime)
                        Log.i(TAG, callDuration)
                        Log.i(TAG, callType)
                        Log.i(TAG, "=======================")

                        callDetails.add(
                            ModelCalls(
                                phoneNumber,
                                callType,
                                callDaytime,
                                callDuration
                            )
                        )
                    }while (cursor.moveToNext())
                }
                cursor.close()
            }else{
                Log.i(TAG, "NO EVERYTHING")
            }
        }catch (e:SecurityException){
            Log.i(TAG, "Catchhhhhhhhhhhhhhhhhhhhhhhh")
        }
        return  callDetails
    }
}
