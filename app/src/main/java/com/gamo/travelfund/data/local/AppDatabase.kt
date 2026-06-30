package com.gamo.travelfund.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gamo.travelfund.data.model.dao.BudgetCategoryDao
import com.gamo.travelfund.data.model.dao.ContributionDao
import com.gamo.travelfund.data.model.dao.SavingMovementDao
import com.gamo.travelfund.data.model.dao.TripDao
import com.gamo.travelfund.data.model.entity.BudgetCategoryEntity
import com.gamo.travelfund.data.model.entity.ContributionEntity
import com.gamo.travelfund.data.model.entity.SavingMovementEntity
import com.gamo.travelfund.data.model.entity.TripEntity


@Database(
    entities = [
        TripEntity::class,
        BudgetCategoryEntity::class,
        ContributionEntity::class,
        SavingMovementEntity::class
    ],
    version = AppDatabase.VERSION,
    exportSchema = true,
)


abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun budgetCategoryDao(): BudgetCategoryDao
    abstract fun contributionDao(): ContributionDao
    abstract fun savingMovementDao(): SavingMovementDao

    companion object {
        const val DATABASE_NAME = "travel_fund_db"
        const val VERSION = 6
    }
}
