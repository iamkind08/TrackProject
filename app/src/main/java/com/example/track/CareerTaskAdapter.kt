package com.example.track

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CareerTaskAdapter(
    private val onToggle: (CareerTask) -> Unit,
    private val onDelete: (CareerTask) -> Unit
) : RecyclerView.Adapter<CareerTaskAdapter.TaskViewHolder>() {

    private var tasks: List<CareerTask> = emptyList()

    fun submitList(newTasks: List<CareerTask>) {
        tasks = newTasks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_career_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount(): Int = tasks.size

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkbox: CheckBox = itemView.findViewById(R.id.taskCheckbox)
        private val title: TextView = itemView.findViewById(R.id.taskTitle)

        fun bind(task: CareerTask) {
            title.text = task.title
            checkbox.isChecked = task.isDone

            if (task.isDone) {
                title.paintFlags = title.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                title.alpha = 0.5f
            } else {
                title.paintFlags = title.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                title.alpha = 1f
            }

            itemView.setOnClickListener { onToggle(task) }

            itemView.setOnLongClickListener {
                onDelete(task)
                true
            }
        }
    }
}