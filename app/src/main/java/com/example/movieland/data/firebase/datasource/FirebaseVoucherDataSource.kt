package com.example.movieland.data.firebase.datasource


import com.example.movieland.data.firebase.model.voucher.FirestoreVoucher
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseVoucherDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("vouchers")

    suspend fun addVoucher(voucher: FirestoreVoucher): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val id = if (voucher.id.isBlank()) {
                "voucher_" + System.currentTimeMillis()
            } else {
                voucher.id
            }

            val voucherWithId = voucher.copy(id = id)
            collection.document(id).set(voucherWithId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun getVoucherById(voucherId: String): Result<FirestoreVoucher> = withContext(Dispatchers.IO) {
        return@withContext try {
            val snapshot = collection.document(voucherId).get().await()
            val voucher = snapshot.toObject(FirestoreVoucher::class.java)
                ?: return@withContext Result.failure(Exception("Voucher không tồn tại: $voucherId"))
            Result.success(voucher)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteVoucher(voucherId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            collection.document(voucherId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loadAllVouchers(): Result<List<FirestoreVoucher>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val snapshot = collection.get().await()
            val vouchers = snapshot.toObjects(FirestoreVoucher::class.java)
            Result.success(vouchers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
