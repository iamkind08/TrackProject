package com.example.track

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class AvatarAdapter(
    private val avatars: List<AvatarItem>,
    private val userPoints: Int,
    private val selectedAvatar: String,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<AvatarAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_avatar, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = avatars[position]
        val resId = holder.itemView.context.resources.getIdentifier(
            item.name, "drawable", holder.itemView.context.packageName
        )
        if (resId != 0) holder.image.setImageResource(resId)

        val isUnlocked = userPoints >= item.minPoints

        if (isUnlocked) {
            holder.lockOverlay.visibility = View.GONE
            holder.lockLabel.visibility = View.GONE
            holder.image.alpha = 1f

            holder.image.setOnClickListener { onClick(item.name) }

            if (item.name == selectedAvatar) {
                holder.image.setBackgroundResource(R.drawable.bg_avatar_selected)
            } else {
                holder.image.setBackgroundResource(R.drawable.bg_card_green)
            }
        } else {
            holder.lockOverlay.visibility = View.VISIBLE
            holder.lockLabel.visibility = View.VISIBLE
            holder.lockLabel.text = "${item.minPoints} очков"
            holder.image.alpha = 0.6f
            holder.image.setOnClickListener {
                Toast.makeText(
                    holder.itemView.context,
                    "Нужно ${item.minPoints} очков",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun getItemCount() = avatars.size

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val image: ImageView = v.findViewById(R.id.avatarImage)
        val lockOverlay: View = v.findViewById(R.id.lockOverlay)
        val lockLabel: TextView = v.findViewById(R.id.lockLabel)
    }
}