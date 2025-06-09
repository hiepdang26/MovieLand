package com.example.admin.data.firebase.datasource

import com.example.admin.data.firebase.model.combo.FirestoreCombo
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseComboDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    /**
     * Load tất cả combo theo districtId (có thể lọc region nếu cần).
     */
    suspend fun loadCombosByDistrict(districtId: String): Result<List<FirestoreCombo>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val snapshot = firestore.collection("combos")
                .whereEqualTo("districtId", districtId)
                .get().await()

            val combos = snapshot.toObjects(FirestoreCombo::class.java)
            Result.success(combos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Thêm mới combo (tạo id mới nếu chưa có).
     */
    suspend fun addCombo(combo: FirestoreCombo): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val comboId = if (combo.id.isBlank()) {
                "combo_" + java.util.UUID.randomUUID().toString()
            } else {
                combo.id
            }

            val comboWithId = combo.copy(id = comboId)
            firestore.collection("combos").document(comboId).set(comboWithId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Lấy thông tin combo theo id.
     */
    suspend fun getComboById(comboId: String): Result<FirestoreCombo> = withContext(Dispatchers.IO) {
        return@withContext try {
            val snapshot = firestore.collection("combos").document(comboId).get().await()
            val combo = snapshot.toObject(FirestoreCombo::class.java)
                ?: return@withContext Result.failure(Exception("Không tìm thấy combo với ID: $comboId"))
            Result.success(combo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Xoá combo theo id.
     */
    suspend fun deleteCombo(comboId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            firestore.collection("combos").document(comboId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Cập nhật thông tin combo (chỉ cập nhật các trường được chỉ định).
     */
    suspend fun updateCombo(comboId: String, updatedFields: Map<String, Any?>): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            firestore.collection("combos").document(comboId).update(updatedFields).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
