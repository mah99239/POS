package com.casecode.pos.feature.item

import android.graphics.Bitmap
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.pos.core.data.utils.NetworkMonitor
import com.casecode.pos.core.domain.repository.ResourceItems
import com.casecode.pos.core.domain.usecase.AddItemUseCase
import com.casecode.pos.core.domain.usecase.DeleteItemUseCase
import com.casecode.pos.core.domain.usecase.GetItemsUseCase
import com.casecode.pos.core.domain.usecase.ItemImageUseCase
import com.casecode.pos.core.domain.usecase.UpdateItemUseCase
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class UIItemsState(
    val resourceItems: ResourceItems = Resource.loading(),
    @StringRes val userMessage: Int? = null,
)

@HiltViewModel
class ItemsViewModel
    @Inject
    constructor(
        private val savedStateHandle: SavedStateHandle,
        private val networkMonitor: NetworkMonitor,
        getItemsUseCase: GetItemsUseCase,
        private val addItemsUseCase: AddItemUseCase,
        private val updateItemUseCase: UpdateItemUseCase,
        private val deleteItemUseCase: DeleteItemUseCase,
        private val imageUseCase: ItemImageUseCase,
    ) : ViewModel() {
        private val isOnline = MutableStateFlow(false)

    private val _userMessage: MutableStateFlow<Int?> = MutableStateFlow(null)
    val userMessage get() = _userMessage
    val uiItemsState: StateFlow<UIItemsState> =
        combine(getItemsUseCase(), _userMessage) { items, userMessage ->
            UIItemsState(items, userMessage)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UIItemsState(Resource.Loading),
        )
    val searchQuery = savedStateHandle.getStateFlow(key = SEARCH_QUERY, initialValue = "")
    val searchWidgetState =
        savedStateHandle.getStateFlow(
            key = SEARCH_WIDGET_STATE,
            initialValue = SearchWidgetState.CLOSED,
        )

    private val _itemSelected = MutableStateFlow<Item?>(null)
    val itemSelected: StateFlow<Item?> = _itemSelected
    private val itemImageChanged = MutableStateFlow(false)

    init {
        setNetworkMonitor()
    }

    fun closeSearchWidgetState() {
        savedStateHandle[SEARCH_WIDGET_STATE] = SearchWidgetState.CLOSED
    }

    fun openSearchWidgetState() {
        savedStateHandle[SEARCH_WIDGET_STATE] = SearchWidgetState.OPENED
    }

    fun onSearchQueryChanged(query: String) {
        savedStateHandle[SEARCH_QUERY] = query
    }

    private fun setNetworkMonitor() =
        viewModelScope.launch {
            networkMonitor.isOnline.collect {
                isOnline.value = it
            }
        }

    fun snackbarMessageShown() {
        _userMessage.value = null
    }

    fun showSnackbarMessage(message: Int) {
        _userMessage.value = message
    }

    fun checkNetworkAndAddItem(
        name: String,
        price: Double,
        quantity: Double,
        barcode: String,
        bitmap: Bitmap?,
    ) {
        if (isOnline.value) {
            uploadImageAndAddItem(name, price, quantity, barcode, bitmap)
        } else {
            showSnackbarMessage(com.casecode.pos.core.ui.R.string.core_ui_error_network)
        }
    }

    private fun uploadImageAndAddItem(
        name: String,
        price: Double,
        quantity: Double,
        barcode: String,
        bitmap: Bitmap?,
    ) {
        Timber.e("uploadImageAndAddItem bitmap = $bitmap")
        viewModelScope.launch {
            imageUseCase.uploadImage(bitmap = bitmap, imageName = barcode).collect {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        showSnackbarMessage(it.message as Int)
                    }

                    is Resource.Empty -> {
                        addItem(name, price, quantity, barcode, null)
                    }

                    is Resource.Success -> {
                        Timber.e("uploadImageAndAddItem it.data = ${it.data}")
                        addItem(name, price, quantity, barcode, it.data)
                    }
                }
            }
        }
    }

    private fun addItem(
        name: String,
        price: Double,
        quantity: Double,
        sku: String,
        imageUrl: String?,
    ) {
        viewModelScope.launch {
            handleResponseAddAndUpdateItem(
                addItemsUseCase(
                    Item(
                        name = name,
                        price = price,
                        quantity = quantity,
                        sku = sku,
                        imageUrl = imageUrl,
                    ),
                ),
            )
        }
    }

    fun updateItemImage() {
        this.itemImageChanged.value = true
    }

    fun checkNetworkAndUpdateItem(
        name: String,
        price: Double,
        quantity: Double,
        barcode: String,
        bitmap: Bitmap?,
    ) {
        if (isOnline.value) {
            checkItemImageChangeAndItemUpdate(name, price, quantity, barcode, bitmap)
        } else {
            showSnackbarMessage(com.casecode.pos.core.ui.R.string.core_ui_error_network)
        }
    }

    private fun checkItemImageChangeAndItemUpdate(
        name: String,
        price: Double,
        quantity: Double,
        barcode: String,
        bitmap: Bitmap?,
    ) {
        if (itemImageChanged.value) {
            println("itemChange")
            replaceImageAndUpdateItem(name, price, quantity, barcode, bitmap)
            itemImageChanged.value = false
        } else {
            updateItem(name, price, quantity, barcode, itemSelected.value?.imageUrl)
        }
    }

    private fun replaceImageAndUpdateItem(
        name: String,
        price: Double,
        quantity: Double,
        sku: String,
        bitmap: Bitmap?,
    ) {
        viewModelScope.launch {
            imageUseCase.replaceOrUploadImage(bitmap, itemSelected.value?.imageUrl, sku).collect {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        showSnackbarMessage(it.message as Int)
                    }

                    is Resource.Success -> {
                        updateItem(name, price, quantity, sku, it.data)
                    }

                    is Resource.Empty -> {
                        updateItem(name, price, quantity, sku, itemSelected.value?.imageUrl)
                    }
                }
            }
        }
    }

    private fun updateItem(
        name: String,
        price: Double,
        quantity: Double,
        sku: String,
        imageUrl: String?,
    ) {
        viewModelScope.launch {
            val itemOld = itemSelected.value
            if (itemOld?.name != name || itemOld.price != price || itemOld.quantity != quantity || itemOld.imageUrl != imageUrl) {
                handleResponseAddAndUpdateItem(
                    updateItemUseCase(
                        Item(
                            name = name,
                            price = price,
                            quantity = quantity,
                            sku = sku,
                            imageUrl = imageUrl,
                        ),
                    ),
                )
            } else {
                showSnackbarMessage(R.string.feature_item_error_update_item_message)
            }
        }
    }

    private fun handleResponseAddAndUpdateItem(result: Resource<Int>) {
        when (result) {
            is Resource.Loading -> {}
            is Resource.Error, is Resource.Empty -> {
                showSnackbarMessage((result as Resource.Error).message as Int)
            }

            is Resource.Success -> {
                showSnackbarMessage(result.data)
            }
        }
    }

    fun checkNetworkAndDeleteItem() {
        if (isOnline.value) {
            val item =
                _itemSelected.value
                    ?: return showSnackbarMessage(com.casecode.pos.core.ui.R.string.core_ui_error_unknown)
            deleteImageAndDeleteItem(item)
        } else {
            showSnackbarMessage(com.casecode.pos.core.ui.R.string.core_ui_error_network)
        }
    }

    private fun deleteImageAndDeleteItem(item: Item) {
        viewModelScope.launch {
            imageUseCase.deleteImage(imageUrl = item.imageUrl).collect {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        showSnackbarMessage(it.message as Int)
                        deleteItem(item)
                    }

                    is Resource.Success, is Resource.Empty -> {
                        deleteItem(item)
                    }
                }
            }
        }
    }

    private fun deleteItem(item: Item) {
        viewModelScope.launch {
            handleResponseDeleteItem(deleteItemUseCase(item))
        }
    }

    private fun handleResponseDeleteItem(result: Resource<Int>) {
        when (result) {
            is Resource.Loading -> {}
            is Resource.Error -> {
                showSnackbarMessage(result.message as Int)
            }

            is Resource.Success, is Resource.Empty -> {
                showSnackbarMessage((result as Resource.Success).data)
            }
        }
    }

    fun setItemSelected(item: Item) {
        _itemSelected.value = item
    }
}

private const val SEARCH_QUERY = "searchQuery"
private const val SEARCH_WIDGET_STATE = "searchWidgetState"