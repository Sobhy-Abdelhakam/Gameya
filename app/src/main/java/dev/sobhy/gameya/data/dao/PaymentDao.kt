package dev.sobhy.gameya.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.sobhy.gameya.data.entity.PaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {

    @Insert
    suspend fun insertPayments(payments: List<PaymentEntity>)

    @Query("SELECT * FROM payments WHERE cycleId = :cycleId")
    fun getPayments(cycleId: Long): Flow<List<PaymentEntity>>

    @Query("""
        UPDATE payments 
        SET status = :status, paidAt = :paidAt 
        WHERE id = :paymentId
    """)
    suspend fun updatePaymentStatus(
        paymentId: Long,
        status: String,
        paidAt: Long?
    )
}