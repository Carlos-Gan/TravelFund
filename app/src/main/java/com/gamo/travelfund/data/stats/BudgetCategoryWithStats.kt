package com.gamo.travelfund.data.stats

import androidx.room.Embedded
import com.gamo.travelfund.data.model.entity.BudgetCategoryEntity

data class BudgetCategoryWithStats(
    @Embedded
    val category: BudgetCategoryEntity,

    val spentAmount: Double,
)