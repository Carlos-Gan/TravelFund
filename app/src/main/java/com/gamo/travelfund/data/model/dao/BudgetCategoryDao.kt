package com.gamo.travelfund.data.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.gamo.travelfund.data.model.entity.BudgetCategoryEntity
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
}