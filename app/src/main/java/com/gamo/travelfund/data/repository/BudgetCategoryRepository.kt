package com.gamo.travelfund.data.repository

import com.gamo.travelfund.data.model.dao.BudgetCategoryDao
import com.gamo.travelfund.data.model.entity.BudgetCategoryEntity

class BudgetCategoryRepository(
    private val dao: BudgetCategoryDao
) {
    fun getCategoriesForTrip(tripId: Long) =
        dao.getCategoriesForTrip(tripId)

    suspend fun insertCategory(category: BudgetCategoryEntity) =
        dao.insertCategory(category)

    suspend fun updateCategory(category: BudgetCategoryEntity) =
        dao.updateCategory(category)

    suspend fun deleteCategory(category: BudgetCategoryEntity) =
        dao.deleteCategory(category)

    fun getCategoriesWithStats(tripId: Long) =
        dao.getCategoriesWithStats(tripId)
}