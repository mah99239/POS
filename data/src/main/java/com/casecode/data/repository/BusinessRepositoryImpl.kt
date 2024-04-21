package com.casecode.data.repository

import com.casecode.data.mapper.toBusinessRequest
import com.casecode.data.utils.AppDispatchers.IO
import com.casecode.data.utils.Dispatcher
import com.casecode.domain.model.users.Business
import com.casecode.domain.repository.AddBusiness
import com.casecode.domain.repository.BusinessRepository
import com.casecode.domain.repository.CompleteBusiness
import com.casecode.domain.utils.BUSINESS_FIELD
import com.casecode.domain.utils.BUSINESS_IS_COMPLETED_STEP_FIELD
import com.casecode.domain.utils.USERS_COLLECTION_PATH
import com.casecode.pos.data.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.UnknownHostException
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Created by Mahmoud Abdalhafeez on 12/13/2023
 */
class BusinessRepositoryImpl
    @Inject
    constructor(
        private val firestore: FirebaseFirestore,
        @Dispatcher(IO) val ioDispatcher: CoroutineDispatcher,
    ) : BusinessRepository {
        override suspend fun getBusiness(uid: String): Business {
            TODO("Not yet implemented")
        }

        override suspend fun setBusiness(
            business: Business,
            uid: String,
        ): AddBusiness {
            return withContext(ioDispatcher) {
                try {
                    // Use suspendCoroutine to handle the asynchronous Firestore operation
                    val resultAddBusiness =
                        suspendCoroutine<AddBusiness> { continuation ->

                            firestore.collection(USERS_COLLECTION_PATH).document(uid)
                                .set(business.toBusinessRequest() as Map<String, Any>).addOnSuccessListener {
                                    continuation.resume(AddBusiness.success(true))
                                }.addOnFailureListener {
                                    val message = it.message ?: "Failure in database, when add new business"
                                    Timber.e("Business Failure: $message")
                                    continuation.resume(AddBusiness.error(R.string.add_subscription_business_failure))
                                }
                        }
                    resultAddBusiness
                } catch (e: FirebaseFirestoreException) {
                    AddBusiness.error(R.string.add_business_failure)
                } catch (e: UnknownHostException) {
                    AddBusiness.error(R.string.add_business_network)
                } catch (e: Exception) {
                    AddBusiness.error(R.string.add_business_failure)
                }
            }
        }

        override suspend fun completeBusinessSetup(uid: String): CompleteBusiness {
            return withContext(ioDispatcher) {
                try {
                    val resultCompleteBusinessStep =
                        suspendCoroutine<CompleteBusiness> { continuation ->
                            firestore.collection(USERS_COLLECTION_PATH).document(uid).update(
                                "$BUSINESS_FIELD.$BUSINESS_IS_COMPLETED_STEP_FIELD",
                                true,
                            )
                                .addOnSuccessListener {
                                    continuation.resume(CompleteBusiness.success(true))
                                }.addOnFailureListener {
                                    continuation.resume(CompleteBusiness.error(R.string.complete_business_failure))
                                }
                        }
                    resultCompleteBusinessStep
                } catch (e: FirebaseFirestoreException) {
                    CompleteBusiness.error(R.string.complete_business_failure)
                } catch (e: Exception) {
                    CompleteBusiness.error(R.string.complete_business_failure)
                }
            }
        }
    }