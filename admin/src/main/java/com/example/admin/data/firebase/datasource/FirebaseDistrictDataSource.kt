package com.example.admin.data.firebase.datasource
import android.content.Context
import android.util.Log
import com.example.admin.data.firebase.model.district.FirestoreDistrict
import com.example.admin.data.firebase.model.region.FirestoreRegion
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject


class FirebaseDistrictDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val appContext: Context
) {

    private fun districtsCollection(regionId: String) =
        firestore.collection("regions").document(regionId).collection("districts")

    suspend fun uploadDistrict(regionId: String, district: FirestoreDistrict): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val docRef = if (district.id.isBlank()) {
                val newId = "district_" + java.util.UUID.randomUUID().toString()
                districtsCollection(regionId).document(newId)
            } else {
                districtsCollection(regionId).document(district.id)
            }

            val districtToSave = if (district.id.isBlank()) {
                district.copy(id = docRef.id)
            } else {
                district
            }

            docRef.set(districtToSave).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseDistrictDataSource", "Lỗi upload district: ${e.message}", e)
            Result.failure(e)
        }
    }


    fun getDistricts(regionId: String): Flow<List<FirestoreDistrict>> = callbackFlow {
        try {
            val listener = districtsCollection(regionId).orderBy("name")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val districts = snapshot?.toObjects(FirestoreDistrict::class.java) ?: emptyList()
                    trySend(districts).isSuccess
                }
            awaitClose { listener.remove() }
        } catch (e: Exception) {
            Log.e("FirebaseDistrictDataSource", "Lỗi listen districts: ${e.message}", e)
            close(e)
        }
    }

    suspend fun getDistrictById(regionId: String, districtId: String): Result<FirestoreDistrict> = withContext(Dispatchers.IO) {
        try {
            Log.d("FirebaseDistrictDataSource", "Bắt đầu lấy district, regionId=$regionId, districtId=$districtId")

            val snapshot = firestore.collection("regions")
                .document(regionId)
                .collection("districts")
                .document(districtId)
                .get()
                .await()

            if (!snapshot.exists()) {
                Log.w("FirebaseDistrictDataSource", "Không tìm thấy document với regionId=$regionId, districtId=$districtId")
                return@withContext Result.failure(Exception("Không tìm thấy quận/huyện với ID: $districtId"))
            }

            val district = snapshot.toObject(FirestoreDistrict::class.java)

            if (district == null) {
                Log.w("FirebaseDistrictDataSource", "Chuyển đổi snapshot sang FirestoreDistrict trả về null")
                return@withContext Result.failure(Exception("Không tìm thấy quận/huyện với ID: $districtId"))
            }

            Log.d("FirebaseDistrictDataSource", "Lấy district thành công: $district")
            Result.success(district)

        } catch (e: Exception) {
            Log.e("FirebaseDistrictDataSource", "Lỗi khi lấy district by id: ${e.message}", e)
            Result.failure(e)
        }
    }
    fun getAllDistricts(): Flow<List<FirestoreDistrict>> = callbackFlow {
        try {
            val listener = firestore.collectionGroup("districts")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val districts = snapshot?.toObjects(FirestoreDistrict::class.java) ?: emptyList()
                    trySend(districts).isSuccess
                }
            awaitClose { listener.remove() }
        } catch (e: Exception) {
            Log.e("FirebaseDistrictDataSource", "Lỗi lấy tất cả districts: ${e.message}", e)
            close(e)
        }
    }


    suspend fun updateDistrict(regionId: String, districtId: String, updatedData: Map<String, Any>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val docRef = firestore.collection("regions")
                .document(regionId)
                .collection("districts")
                .document(districtId)

            val snapshot = docRef.get().await()
            if (!snapshot.exists()) {
                return@withContext Result.failure(Exception("Document district không tồn tại để cập nhật"))
            }

            docRef.update(updatedData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseDistrictDataSource", "Lỗi update district: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun deleteDistrict(regionId: String, districtId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            districtsCollection(regionId).document(districtId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseDistrictDataSource", "Lỗi delete district: ${e.message}", e)
            Result.failure(e)
        }
    }
}

