package com.example.track

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val userId = intent.getLongExtra("USER_ID", 0L)
        val date = today()

        val greeting = findViewById<TextView>(R.id.greetingText)
        val userLogin = intent.getStringExtra("USER_LOGIN") ?: "друг"
        greeting.text = "Привет, $userLogin!"

        val overallCircle = findViewById<CircularProgressView>(R.id.overallProgress)

        val db = AppDatabase.get(this)
        val careerDao = db.careerTaskDao()
        val mentalDao = db.mentalTaskDao()
        val sportDao = db.sportTaskDao()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    combine(
                        careerDao.observeTotalCount(userId, date),
                        careerDao.observeDoneCount(userId, date),
                        mentalDao.observeTotalCount(userId, date),
                        mentalDao.observeDoneCount(userId, date)
                    ) { cTotal, cDone, mTotal, mDone ->
                        intArrayOf(cTotal, cDone, mTotal, mDone)
                    },
                    combine(
                        sportDao.observeTotalCount(userId, date),
                        sportDao.observeDoneCount(userId, date)
                    ) { sTotal, sDone ->
                        intArrayOf(sTotal, sDone)
                    }
                ) { careerMental, sport ->
                    val totalAll = careerMental[0] + careerMental[2] + sport[0]
                    val doneAll  = careerMental[1] + careerMental[3] + sport[1]
                    if (totalAll == 0) 0f else doneAll * 100f / totalAll
                }.collect { percent ->
                    overallCircle.setProgress(percent)
                }
            }
        }

        findViewById<CardView>(R.id.cardMental).setOnClickListener {
            startActivity(Intent(this, MentalActivity::class.java).apply {
                putExtra("USER_ID", userId)
            })
        }
        findViewById<CardView>(R.id.cardSport).setOnClickListener {
            startActivity(Intent(this, SportActivity::class.java).apply {
                putExtra("USER_ID", userId)
            })
        }
        findViewById<CardView>(R.id.cardCareer).setOnClickListener {
            startActivity(Intent(this, CareerActivity::class.java).apply {
                putExtra("USER_ID", userId)
            })
        }

        findViewById<Button>(R.id.historyButton).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java).apply {
                putExtra("USER_ID", userId)
            })
        }

        findViewById<Button>(R.id.profileButton).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java).apply {
                putExtra("USER_ID", userId)
            })
        }
    }
}