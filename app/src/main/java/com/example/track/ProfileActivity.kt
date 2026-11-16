package com.example.track

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var adapter: AvatarAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val userId = intent.getLongExtra("USER_ID", 0L)
        val db = AppDatabase.get(this)
        val userDao = db.userDao()

        val avatarView   = findViewById<ImageView>(R.id.profileAvatar)
        val loginText    = findViewById<TextView>(R.id.profileLogin)
        val rankText     = findViewById<TextView>(R.id.profileRank)
        val pointsText   = findViewById<TextView>(R.id.profilePoints)
        val nextRankText = findViewById<TextView>(R.id.profileNextRank)
        val grid         = findViewById<RecyclerView>(R.id.avatarGrid)
        val fireworks    = findViewById<FireworksView>(R.id.fireworks)

        grid.layoutManager = GridLayoutManager(this, 3)

        // Локальные переменные для отслеживания смены звания
        var lastRankTitle: String? = null
        var firstLoad = true

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userDao.observeById(userId).collect { user ->
                    if (user == null) return@collect

                    // === Заголовок профиля ===
                    loginText.text = user.login
                    val rank = rankFor(user.points)
                    rankText.text = "${rank.emoji} ${rank.title}"
                    pointsText.text = "${user.points} очков"

                    val next = nextRankFor(user.points)
                    nextRankText.text = if (next != null) {
                        "До звания «${next.title}» — ${next.minPoints - user.points} очков"
                    } else {
                        "Максимальное звание достигнуто!"
                    }

                    // === Аватар сверху ===
                    val resId = resources.getIdentifier(user.avatar, "drawable", packageName)
                    if (resId != 0) avatarView.setImageResource(resId)

                    // === Сетка аватарок ===
                    adapter = AvatarAdapter(
                        avatars = ALL_AVATARS,
                        userPoints = user.points,
                        selectedAvatar = user.avatar,
                        onClick = { avatarName ->
                            lifecycleScope.launch {
                                userDao.setAvatar(userId, avatarName)
                            }
                        }
                    )
                    grid.adapter = adapter

                    // === САЛЮТ ПРИ СМЕНЕ ЗВАНИЯ ===
                    val currentRankTitle = rank.title
                    if (firstLoad) {
                        // Первый показ профиля — просто запоминаем
                        lastRankTitle = currentRankTitle
                        firstLoad = false
                    } else if (currentRankTitle != lastRankTitle) {
                        // Звание изменилось — салют!
                        fireworks.launch(burstCount = 4)
                        Toast.makeText(
                            this@ProfileActivity,
                            "🎉 Новое звание: ${rank.emoji} ${rank.title}!",
                            Toast.LENGTH_LONG
                        ).show()
                        lastRankTitle = currentRankTitle
                    }
                }
            }
        }
    }
}