package com.example.track

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MentalActivity : AppCompatActivity() {

    private lateinit var adapter: MentalTaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mental)

        val userId = intent.getLongExtra("USER_ID", 0L)
        val date = today()
        val db = AppDatabase.get(this)
        val userDao = db.userDao()
        val dao = db.mentalTaskDao()

        val input = findViewById<EditText>(R.id.taskInput)
        val addButton = findViewById<Button>(R.id.addTaskButton)
        val recycler = findViewById<RecyclerView>(R.id.tasksRecyclerView)
        val emptyText = findViewById<TextView>(R.id.emptyText)
        val progressCircle = findViewById<CircularProgressView>(R.id.mentalProgress)
        val fireworks = findViewById<FireworksView>(R.id.fireworks)

        adapter = MentalTaskAdapter(
            onToggle = { task ->
                lifecycleScope.launch {
                    val newState = !task.isDone
                    dao.update(task.copy(isDone = newState))

                    val rankUp = PointsManager.onTaskToggled(userDao, userId, newState)
                    val rankUpFromBonus = PointsManager.checkDailyBonus(db, userId)

                    val event = rankUpFromBonus ?: rankUp
                    if (event != null) {
                        fireworks.launch(burstCount = 4)
                        Toast.makeText(
                            this@MentalActivity,
                            "🎉 Новое звание: ${event.newRank.emoji} ${event.newRank.title}!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            },
            onDelete = { task -> lifecycleScope.launch { dao.delete(task) } }
        )
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        addButton.setOnClickListener {
            val title = input.text.toString().trim()
            if (title.isBlank()) {
                Toast.makeText(this, "Введите задачу", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                dao.insert(MentalTask(userId = userId, title = title))
                input.text.clear()
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                dao.observeByUserAndDate(userId, date).collect { tasks ->
                    adapter.submitList(tasks)
                    emptyText.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
                    recycler.visibility   = if (tasks.isEmpty()) View.GONE    else View.VISIBLE
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    dao.observeTotalCount(userId, date),
                    dao.observeDoneCount(userId, date)
                ) { total, done ->
                    if (total == 0) 0f else done * 100f / total
                }.collect { percent ->
                    progressCircle.setProgress(percent)
                }
            }
        }
    }
}