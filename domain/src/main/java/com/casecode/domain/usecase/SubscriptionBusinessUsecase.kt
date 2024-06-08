package com.casecode.domain.usecase

import com.casecode.domain.model.users.SubscriptionBusiness
import com.casecode.domain.repository.AddSubscriptionBusiness
import com.casecode.domain.repository.SubscriptionsBusinessRepository
import com.casecode.domain.utils.EmptyType
import com.casecode.domain.utils.Resource
import com.casecode.pos.domain.R
import javax.inject.Inject
import javax.inject.Singleton

class GetSubscriptionBusinessUseCase @Inject constructor(private val subscriptionsRep: SubscriptionsBusinessRepository)
{
   operator fun invoke() = subscriptionsRep.getSubscriptionsBusiness()
}
class SetSubscriptionBusinessUseCase @Inject constructor(private val subscriptionsRep: SubscriptionsBusinessRepository)
{
   suspend operator fun invoke(subscriptionBusiness: SubscriptionBusiness, uid: String): AddSubscriptionBusiness
   {
      if(uid.isEmpty()){
         return Resource.empty( EmptyType.DATA, R.string.uid_empty)
      }
      if(subscriptionBusiness.type.isNullOrEmpty()){
         return Resource.empty( EmptyType.DATA, R.string.add_subscription_business_empty)
      }
      
    return  subscriptionsRep.setSubscriptionBusiness(subscriptionBusiness, uid)
   }
}