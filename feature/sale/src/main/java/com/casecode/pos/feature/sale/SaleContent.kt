package com.casecode.pos.feature.sale

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.casecode.pos.core.designsystem.component.DynamicAsyncImage
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.data.users.Item
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenuBoxSearch(
    modifier: Modifier = Modifier,
    items: List<Item>,
    onScan: () -> Unit,
    onSearchItemClick: (Item) -> Unit,
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val (allowExpanded, setExpanded) = remember { mutableStateOf(false) }
    val filteredItems by remember(searchQuery, items) {
        derivedStateOf {
            if (searchQuery.isBlank()) {
                emptyList()
            } else {
                items.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                        it.sku.contains(searchQuery, ignoreCase = true) ||
                        it.sku.contains(normalizeNumber(searchQuery), ignoreCase = true)
                }
            }
        }
    }
    val expanded = allowExpanded || filteredItems.isNotEmpty()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    ExposedDropdownMenuBox(
        modifier = modifier.fillMaxWidth(),
        expanded = expanded,
        onExpandedChange = setExpanded,
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = {
                Text(
                    stringResource(R.string.feature_sale_sale_search_hint),
                    style = MaterialTheme.typography.bodySmall,
                )
            },
            modifier =
            Modifier
                .menuAnchor(MenuAnchorType.PrimaryEditable)
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onKeyEvent {
                    if (it.key == Key.Enter) {
                        keyboardController?.hide()
                        true
                    } else {
                        false
                    }
                },
            maxLines = 1,
            singleLine = true,
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            PosIcons.Close,
                            contentDescription = null,
                        )
                    }
                } else {
                    IconButton(onClick = onScan) {
                        Icon(
                            PosIcons.QrCodeScanner,
                            contentDescription = stringResource(R.string.feature_sale_scan_item_text),
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions =
            KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                },
            ),
        )
        if (filteredItems.isNotEmpty()) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { setExpanded(false) },
            ) {
                filteredItems.forEach { item ->
                    DropdownMenuItem(
                        text = { ItemDropMenuItem(item) },
                        onClick = {
                            onSearchItemClick(item)
                            setExpanded(false)
                            searchQuery = ""
                        },
                    )
                }
            }
        }
    }
}

private fun normalizeNumber(input: String): String {
    val arabicNumerals = listOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    val englishNumerals = listOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')

    val sb = StringBuilder()
    for (char in input) {
        when (char) {
            in englishNumerals -> sb.append(char)
            in arabicNumerals -> {
                val index = arabicNumerals.indexOf(char)
                sb.append(englishNumerals[index])
            }
            else -> sb.append(char) // Append non-numeric characters as is
        }
    }
    return sb.toString()
}

@Composable
fun ItemDropMenuItem(item: Item) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name)
            Text(item.sku)
        }
        Text(
            stringResource(
                com.casecode.pos.core.ui.R.string.core_ui_item_quantity_format,
                item.quantity,
            ),
        )
    }
}

@Composable
fun ColumnScope.SaleItems(
    itemsInvoice: Set<Item>,
    onRemoveItem: (Item) -> Unit,
    onUpdateQuantity: (Item) -> Unit,
) {
    val scrollableState = rememberLazyListState()
    LazyColumn(
        modifier =
        Modifier
            .weight(1f)
            .padding(horizontal = 8.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
        state = scrollableState,
    ) {
        itemsInvoice.forEach { item ->
            val sku = item.sku
            item(key = sku) {
                ItemSale(
                    name = item.name,
                    price = item.price,
                    quantity = item.quantity,
                    itemImageUrl = item.imageUrl ?: "",
                    onRemoveItem = { onRemoveItem(item) },
                    onUpdateQuantity = { onUpdateQuantity(item) },
                )
            }
        }
    }
}

@Composable
internal fun ItemSale(
    name: String,
    price: Double,
    quantity: Double,
    itemImageUrl: String,
    onRemoveItem: () -> Unit,
    onUpdateQuantity: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        leadingContent = { ItemIcon(itemImageUrl, modifier.size(64.dp)) },
        headlineContent = { Text(name, fontWeight = FontWeight.Bold) },
        supportingContent = {
            Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                val formattedQuantity =
                    stringResource(
                        com.casecode.pos.core.ui.R.string.core_ui_item_quantity_format,
                        quantity,
                    )
                Text(formattedQuantity)
                VerticalDivider(
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
                val formattedPrice =
                    DecimalFormat("#,###.##").format(price)
                Text(
                    stringResource(
                        com.casecode.pos.core.ui.R.string.core_ui_currency,
                        formattedPrice,
                    ),
                )
            }
        },
        trailingContent = {
            IconButton(onClick = { onRemoveItem() }) {
                Icon(
                    imageVector = PosIcons.Delete,
                    contentDescription = stringResource(R.string.feature_sale_dialog_delete_invoice_item_title),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = Modifier.clickable(onClick = { onUpdateQuantity() }),
    )
}

@Composable
private fun ItemIcon(
    topicImageUrl: String,
    modifier: Modifier = Modifier,
) {
    if (topicImageUrl.isEmpty()) {
        Icon(
            modifier =
            modifier
                .background(Color.Transparent)
                .padding(4.dp),
            imageVector = PosIcons.EmptyImage,
            // decorative image
            contentDescription = null,
        )
    } else {
        DynamicAsyncImage(
            imageUrl = topicImageUrl,
            contentDescription = null,
            modifier = modifier,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ItemDropMenuItemPreview() {
    POSTheme {
        ItemDropMenuItem(
            Item(
                name = "Iphone5",
                price = 202.0,
                quantity = 12222222220.0,
                sku = "12345678912345",
                unitOfMeasurement = null,
                imageUrl = null,
            ),
        )
    }
}

@Preview
@Composable
fun SaleItemPreview() {
    POSTheme {
        ItemSale("Item Name", 10.0, 5.0, "", {}, {})
    }
}