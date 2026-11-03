
package com.example.zbudget.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class CategoryOption(
    val name: String,
    val icon: String
)

@Composable
fun CategorySelectionContent(
    categories: List<CategoryOption>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredCategories = remember(categories, searchQuery) {
        categories.filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "Chọn danh mục",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Chọn danh mục phù hợp với giao dịch",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Tìm kiếm danh mục")
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )

        if (filteredCategories.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Không tìm thấy danh mục",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
                    .padding(top = 16.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = filteredCategories.distinctBy {it.name},
                    key = { it.name }
                ) { category ->
                    val isSelected = category.name == selectedCategory

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(
                                        alpha = 0.45f
                                    )
                                },
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                onCategorySelected(category.name)
                            }
                            .padding(
                                vertical = 16.dp,
                                horizontal = 6.dp
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box {
                            Text(
                                text = category.icon,
                                style = MaterialTheme.typography.headlineMedium
                            )

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Đã chọn",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(start = 28.dp)
                                )
                            }
                        }

                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            },
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelectionSheet(
    categories: List<CategoryOption>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        CategorySelectionContent(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = {
                onCategorySelected(it)
                onDismiss()
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CategorySelectionContentPreview() {
    MaterialTheme {
        CategorySelectionContent(
            categories = listOf(
                CategoryOption("Ăn uống", "🍜"),
                CategoryOption("Di chuyển", "🚗"),
                CategoryOption("Mua sắm", "🛍️"),
                CategoryOption("Học tập", "📚"),
                CategoryOption("Giải trí", "🎮"),
                CategoryOption("Gia đình", "🏠")
            ),
            selectedCategory = "Ăn uống",
            onCategorySelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CategorySelectionSheetPreview() {
    MaterialTheme {
        CategorySelectionSheet(
            categories = listOf(
                CategoryOption("Ăn uống", "🍜"),
                CategoryOption("Di chuyển", "🚗"),
                CategoryOption("Mua sắm", "🛍️"),
                CategoryOption("Học tập", "📚"),
                CategoryOption("Giải trí", "🎮"),
                CategoryOption("Gia đình", "🏠")
            ),
            selectedCategory = "Ăn uống",
            onCategorySelected = {},
            onDismiss = {}
        )
    }
}
