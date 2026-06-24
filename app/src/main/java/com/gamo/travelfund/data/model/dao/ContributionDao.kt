package com.gamo.travelfund.data.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.gamo.travelfund.data.model.entity.ContributionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContributionDao {
    @Query("SELECT * FROM contributions WHERE tripId = :tripId ORDER BY id DESC")
    fun getContributionsForTrip(tripId: Long): Flow<List<ContributionEntity>>

    @Insert
    suspend fun insertContribution(contribution: ContributionEntity)

    @Delete
    suspend fun deleteContribution(contribution: ContributionEntity)
}