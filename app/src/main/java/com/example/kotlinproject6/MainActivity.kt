package com.example.kotlinproject6

import android.app.TimePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import java.util.*

class MainActivity : AppCompatActivity() {
    private val setAlarmButton: Button by lazy {
        findViewById<Button>(R.id.setAlarmButton)
    }
    private val setTimeButton: Button by lazy {
        findViewById<Button>(R.id.setTimeButton)
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
    }

    private fun initSetAlarmButton() {
        setAlarmButton.setOnClickListener {
            // data 확인
            // 온오프에 따라 작업 처리
            // 오프 -> 알람 제거 온 -> 알람 켜기
            // 데이터 저장
        }
    }

    private fun initSetTimeButton() {
        setTimeButton.setOnClickListener {
            val calendar = Calendar.getInstance()

            TimePickerDialog(this, { picker, hour, minute ->
                saveAlarmModel(hour, minute, false)

            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false)
                .show()
            //현재시간 가져오기
            //timePickDialog에서 가져온 시간 데이터 저장 -> View 업데이트
            //기존에 있던 알람 삭제
        }
    }

    private fun saveAlarmModel(hour: Int, minute: Int, onOff: Boolean): AlarmDisplayModel {
        val model = AlarmDisplayModel(
            hour = hour,
            minute = minute,
            onOff = false
        )
        val sharedPreferences = getSharedPreferences("time", Context.MODE_PRIVATE)

        with(sharedPreferences.edit()){
            putString("alarm", model.makeDataForDB())
            putBoolean("onOff",model.onOff)
            commit()
        }
        return model

    }

}