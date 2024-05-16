package com.casecode.di.data

import com.casecode.data.repository.BusinessRepositoryImpl
import com.casecode.data.repository.EmployeesBusinessRepositoryImpl
import com.casecode.data.repository.InvoiceRepositoryImpl
import com.casecode.data.repository.ItemImageRepositoryImpl
import com.casecode.data.repository.ItemRepositoryImpl
import com.casecode.data.repository.StoreRepositoryImpl
import com.casecode.data.repository.SubscriptionsBusinessRepositoryImpl
import com.casecode.data.repository.SubscriptionsRepositoryImpl
import com.casecode.domain.repository.BusinessRepository
import com.casecode.domain.repository.EmployeesBusinessRepository
import com.casecode.domain.repository.InvoiceRepository
import com.casecode.domain.repository.ItemImageRepository
import com.casecode.domain.repository.ItemRepository
import com.casecode.domain.repository.StoreRepository
import com.casecode.domain.repository.SubscriptionsBusinessRepository
import com.casecode.domain.repository.SubscriptionsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    internal abstract fun bindBusinessRepo(businessRepositoryImpl: BusinessRepositoryImpl): BusinessRepository

    @Binds
    internal abstract fun bindEmployeesBusinessRepo(employeesBusinessRepositoryImpl: EmployeesBusinessRepositoryImpl): EmployeesBusinessRepository

    @Binds
    internal abstract fun bindStoreRepo(storeRepositoryImpl: StoreRepositoryImpl): StoreRepository

    @Binds
    internal abstract fun bindSubscriptionsBusinessRepo(subscriptionsBusinessRepositoryImpl: SubscriptionsBusinessRepositoryImpl): SubscriptionsBusinessRepository

    @Binds
    internal abstract fun bindSubscriptionsRepo(subscriptionsRepositoryImpl: SubscriptionsRepositoryImpl): SubscriptionsRepository

    @Binds
    internal abstract fun bindItemRepo(itemRepositoryImpl: ItemRepositoryImpl): ItemRepository

    @Binds
    internal abstract fun bindImageRepo(itemImageRepositoryImpl: ItemImageRepositoryImpl): ItemImageRepository

    @Binds
    internal abstract fun bindInvoiceRepo(invoiceRepositoryImpl: InvoiceRepositoryImpl): InvoiceRepository


}