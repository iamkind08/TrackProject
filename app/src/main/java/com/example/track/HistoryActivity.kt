package com.example.track

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class HistoryEntry(
    val date: String,
    val title: String,
    val category: String
)

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val userId = intent.getLongExtra("USER_ID", 0L)
        val db = AppDatabase.get(this)
        val recycler = findViewById<RecyclerView>(R.id.historyRecycler)
        val emptyText = findViewById<TextView>(R.id.historyEmpty)

        val adapter = HistoryAdapter()
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    db.careerTaskDao().observeHistory(userId),
                    db.mentalTaskDao().observeHistory(userId),
                    db.sportTaskDao().observeHistory(userId)
                ) { career, mental, sport ->
                    val entries = mutableListOf<HistoryEntry>()
                    career.forEach  { entries += HistoryEntry(it.date, it.title, "⭐") }
                    mental.forEach  { entries += HistoryEntry(it.date, it.title, "🧠") }
                    sport.forEach   { entries += HistoryEntry(it.date, it.title, "🏋️") }
                    entries.sortedByDescending { it.date }
                }.collect { entries ->
                    adapter.submit(entries)
                    emptyText.visibility = if (entries.isEmpty()) View.VISIBLE else View.GONE
                    recycler.visibility   = if (entries.isEmpty()) View.GONE    else View.VISIBLE
                }
            }
        }
    }
}

class HistoryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<Any>()

    fun submit(entries: List<HistoryEntry>) {
        items.clear()
        var lastDate: String? = null
        entries.forEach {
            if (it.date != lastDate) {
                items.add(formatDate(it.date))
                lastDate = it.date
            }
            items.add(it)
        }
        notifyDataSetChanged()
    }

    private fun formatDate(iso: String): String {
        val parts = iso.split("-")
        val months = listOf("янв", "фев", "мар", "апр", "мая", "июн",
            "июл", "авг", "сен", "окт", "ноя", "дек")
        return "${parts[2].toInt()} ${months[parts[1].toInt() - 1]} ${parts[0]}"
    }

    override fun getItemViewType(position: Int): Int =
        if (items[position] is String) 0 else 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == 0) {
            HeaderVH(inflater.inflate(R.layout.item_history_date, parent, false))
        } else {
            TaskVH(inflater.inflate(R.layout.item_history_task, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderVH -> holder.bind(items[position] as String)
            is TaskVH   -> holder.bind(items[position] as HistoryEntry)
        }
    }

    override fun getItemCount(): Int = items.size

    class HeaderVH(v: View) : RecyclerView.ViewHolder(v) {
        private val t: TextView = v.findViewById(R.id.dateHeader)
        fun bind(text: String) { t.text = text }
    }
    class TaskVH(v: View) : RecyclerView.ViewHolder(v) {
        private val title: TextView    = v.findViewById(R.id.historyTitle)
        private val category: TextView = v.findViewById(R.id.historyCategory)
        fun bind(entry: HistoryEntry) {
            title.text = entry.title
            category.text = entry.category
        }
    }
}