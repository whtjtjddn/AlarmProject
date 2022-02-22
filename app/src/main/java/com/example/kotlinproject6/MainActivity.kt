package com.example.kotlinproject6

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import java.util.*

class MainActivity : AppCompatActivity() {
    private val setAlarmButton: Button by lazy {
        findViewById<Button>(R.id.setAlarmButton)
    }
    private val setTimeButton: Button by lazy {
        findViewById<Button>(R.id.setTimeButton)
    }
    private val timeTextView : TextView by lazy{
        findViewById<TextView>(R.id.timeTextView)
    }
    private val ampmTextView : TextView by lazy{
        findViewById<TextView>(R.id.ampmTextView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //TODO 알람 설정 데이터 (Time) + 알람 ON/OFF 여부 -> SharedPreference 사용
        //step 1 : 뷰 초기화 및 데이터 가져오기
        //step 2 : 데이터 뷰에 뿌려주기
        //step 3 : 데이터에 따라 알람

        initSetAlarmButton()
        initSetTimeButton()

        //데이터 가져오기
        val model = fetchDataFromSharedPreferences()

        renderView(model)
    }

    private fun initSetAlarmButton() {
        setAlarmButton.setOnClickListener {
            // data 확인
            // 온오프에 따라 작업 처리
            // 오프 -> 알람 제거 온 -> 알람 켜기
            // 데이터 저장
            val model = it.tag as? AlarmDisplayModel ?: return@setOnClickListener

            val newModel = saveAlarmModel(model.hour, model.minute, model.onOff)
            renderView(newModel)

            if(newModel.onOff){
                val calendar = Calendar.getInstance().apply{
                    set(Calendar.HOUR_OF_DAY, newModel.hour)
                    set(Calendar.MINUTE, newModel.minute)

                    if(before(Calendar.getInstance())){
                        add(Calendar.DATE,1)
                    }
                }
                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

                val intent = Intent(this, AlarmReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, intent ,PendingIntent.FLAG_UPDATE_CURRENT)

                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            }
            else {
                cancelAlarm()
            }
        }

    }

    private fun cancelAlarm(){
        val pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, Intent(this,AlarmReceiver::class.java),PendingIntent.FLAG_NO_CREATE)
        pendingIntent?.cancel()
    }

    private fun initSetTimeButton() {
        setTimeButton.setOnClickListener {
            val calendar = Calendar.getInstance()

            TimePickerDialog(this, { picker, hour, minute ->
                //데이터 저장(SaveAlarmModel) 및 데이터에 따른 뷰 업데이트(RenderView)
                val model = saveAlarmModel(hour, minute, false)
                renderView(model)

                //기존 알람 삭제
                cancelAlarm()

            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false)
                .show()
        }
    }

    private fun saveAlarmModel(hour: Int, minute: Int, onOff: Boolean): AlarmDisplayModel {
        val model = AlarmDisplayModel(
            hour = hour,
            minute = minute,
            onOff = onOff
        )
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

        with(sharedPreferences.edit()){
            putString(ALARM_KEY, model.makeDataForDB())
            putBoolean(ONOFF_KEY,model.onOff)
            commit()
        }
        return model

    }

    private fun fetchDataFromSharedPreferences() : AlarmDisplayModel{
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

        val timeDBValue = sharedPreferences.getString(ALARM_KEY, "00:00") ?: "00:00"
        val onOFFDBValue = sharedPreferences.getBoolean(ONOFF_KEY,false)
        val alarmData = timeDBValue.split(":")

        val alarmModel = AlarmDisplayModel(
            hour = alarmData[0].toInt(),
            minute = alarmData[1].toInt(),
            onOff = onOFFDBValue
        )

        val pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, Intent(this,AlarmReceiver::class.java),PendingIntent.FLAG_NO_CREATE)

        if((pendingIntent == null) and alarmModel.onOff){
            //알람은 꺼져잇는데, 데이터는 켜져있는 경우
            alarmModel.onOff = false
        }
        else if((pendingIntent != null) and alarmModel.onOff.not()){
            //알람은 켜져있는데, 데이터는 꺼져있는 경우
            cancelAlarm()
        }

        return alarmModel
    }

    private fun renderView(model : AlarmDisplayModel){
        ampmTextView.apply{
            text = model.ampmText
        }
        timeTextView.apply{
            text = model.timeText
        }
        setTimeButton.apply{
            text = model.onOffText
            tag = model
        }
    }

    companion object{
        private const val ALARM_REQUEST_CODE = 1000
        private const val SHARED_PREFERENCES_NAME = "time"
        private const val ALARM_KEY = "alarm"
        private const val ONOFF_KEY = "onOff"
    }

}