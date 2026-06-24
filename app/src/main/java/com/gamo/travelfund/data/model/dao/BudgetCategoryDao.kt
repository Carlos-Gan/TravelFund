package com.gamo.travelfund.data.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.gamo.travelfund.data.model.entity.BudgetCategoryEntity
import com.gamo.travelfund.data.stats.BudgetCategoryWithStats
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetCategoryDao {
    @Query("SELECT * FROM budget_categories WHERE tripId = :tripId")
    fun getCategoriesForTrip(tripId: Long): Flow<List<BudgetCategoryEntity>>

    @Insert
    suspend fun insertCategory(category: BudgetCategoryEntity)

    @Update
    suspend fun updateCategory(category: BudgetCategoryEntity)

    @Delete
    suspend fun deleteCategory(category: BudgetCategoryEntity)

    @Query("""
SELECT
    bc.*,
    COALESCE(
        (
            SELECT SUM(sm.amount)
            FROM saving_movements sm
            WHERE sm.categoryId = bc.id
            AND sm.type = 'EXPENSE'
        ),
        0
    ) AS spentAmount
FROM budget_categories bc
WHERE bc.tripId = :tripId
ORDER BY bc.id
""")
    fun getCategoriesWithStats(
        tripId: Long
    ): Flow<List<BudgetCategoryWithStats>>
}