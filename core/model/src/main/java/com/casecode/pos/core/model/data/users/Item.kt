package com.casecode.pos.core.model.data.users

/**
 * Represents an item in inventory or a product.
 *
 * @property name The name of the item.
 * @property price The price of the item. Default is 0.0.
 * @property quantity The quantity of the item. Default is 0.0.
 * @property sku The stock keeping unit (SKU) of the item.
 * @property unitOfMeasurement The unit of measurement for the item. Can be null.
 * @property imageUrl The URL of the image associated with the item. Default is null.
 * @constructor Creates an item with default values for name, price, quantity, and imageUrl.
 */
data class Item(
    val name: String = "",
    val price: Double = 0.0,
    var quantity: Double = 0.0,
    val sku: String = "",
    var unitOfMeasurement: String? = "",
     var imageUrl: String? = "",
)