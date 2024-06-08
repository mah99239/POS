package com.casecode.domain.usecase

import com.casecode.domain.model.users.Branch
import com.casecode.domain.model.users.Business
import com.casecode.domain.repository.AddBusiness
import com.casecode.domain.repository.BusinessRepository
import com.casecode.domain.repository.CompleteBusiness
import com.casecode.domain.utils.EmptyType
import com.casecode.domain.utils.Resource
import com.casecode.pos.domain.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class SetBusinessUseCase @Inject constructor(private val businessRep: BusinessRepository)
{
   suspend operator fun invoke(business: Business, uid: String) : AddBusiness{
      if(uid.isEmpty()){
         return Resource.empty( EmptyType.DATA, R.string.uid_empty)
      }
      if(business.branches.isEmpty())
      {
         return Resource.empty( EmptyType.DATA, R.string.branches_empty)
      }
      if(business.storeType?.name.isNullOrBlank()){
         return Resource.empty( EmptyType.DATA, R.string.store_type_business_empty)
      }
      if(business.phone?.isEmpty() == true){
         return Resource.empty( EmptyType.DATA, R.string.phone_business_empty)
      }
      if(business.email?.isEmpty() == true){
         return Resource.empty( EmptyType.DATA, R.string.email_business_empty)
      }
     return businessRep.setBusiness(business, uid)
   }
}
class CompleteBusinessUseCase @Inject constructor(private val businessRepo: BusinessRepository){
   suspend operator fun invoke(uid: String): CompleteBusiness
   {
      if(uid.isEmpty()){
         return Resource.empty( EmptyType.DATA, R.string.uid_empty)
      }
      return businessRepo.completeBusinessSetup(uid)
   }
}

class GetBusinessUseCase @Inject constructor(private val businessRep: BusinessRepository) {

   suspend operator fun invoke():Flow<Resource<Business>> {
     return flow {
         emit(Resource.loading())
         emit(businessRep.getBusiness())
     }
   }
}
class AddBranchBusinessUseCase @Inject constructor(private val businessRep: BusinessRepository) {
   suspend operator fun invoke(branch:Branch) = businessRep.addBranch(branch)

}