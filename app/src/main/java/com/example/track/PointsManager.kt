package com.example.track

data class RankUpEvent(val newRank: Rank)

object PointsManager {

    suspend fun onTaskToggled(
        userDao: UserDao,
        userId: Long,
        nowDone: Boolean
    ): RankUpEvent? {
        val before = userDao.getById(userId) ?: return null
        val oldRank = rankFor(before.points)

        userDao.addPoints(userId, if (nowDone) 1 else -1)

        val after = userDao.getById(userId) ?: return null
        val newRank = rankFor(after.points)

        return if (newRank.title != oldRank.title) RankUpEvent(newRank) else null
    }

    suspend fun checkDailyBonus(
        db: AppDatabase,
        userId: Long
    ): RankUpEvent? {
        val date = today()
        val user = db.userDao().getById(userId) ?: return null
        if (user.lastBonusDate == date) return null

        val careerDone = db.careerTaskDao().countDoneToday(userId, date)
        val mentalDone = db.mentalTaskDao().countDoneToday(userId, date)
        val sportDone  = db.sportTaskDao().countDoneToday(userId, date)

        if (!(careerDone > 0 && mentalDone > 0 && sportDone > 0)) return null

        val oldRank = rankFor(user.points)
        db.userDao().addPoints(userId, 10)
        db.userDao().setLastBonusDate(userId, date)

        val updated = db.userDao().getById(userId) ?: return null
        val newRank = rankFor(updated.points)

        return if (newRank.title != oldRank.title) RankUpEvent(newRank) else null
    }
}