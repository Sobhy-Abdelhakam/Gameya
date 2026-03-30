package dev.sobhy.gameya.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.sobhy.gameya.data.local.entity.PaymentEntity
import kotlinx.coroutines.flow.Flow

data class PaymentAuditRow(
    val paymentId: Long,
    val groupName: String,
    val cycleIndex: Int,
    val memberName: String,
    val amount: Double,
    val status: String,
    val paidAt: Long?
)

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

    @Query("SELECT * FROM payments ORDER BY id ASC")
    suspend fun getAllPayments(): List<PaymentEntity>

    @Query(
        """
        SELECT p.id AS paymentId, g.name AS groupName, c.cycleIndex AS cycleIndex, m.name AS memberName,
               p.amount AS amount, p.status AS status, p.paidAt AS paidAt
        FROM payments p
        INNER JOIN cycles c ON c.id = p.cycleId
        INNER JOIN `groups` g ON g.id = c.groupId
        INNER JOIN members m ON m.id = p.memberId
        ORDER BY p.id ASC
        """
    )
    suspend fun getPaymentAuditHistory(): List<PaymentAuditRow>
}