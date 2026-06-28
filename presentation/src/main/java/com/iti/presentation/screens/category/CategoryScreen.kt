package com.iti.presentation.screens.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.presentation.screens.category.components.CategoryCard
import com.iti.presentation.screens.category.components.CategoryCardShimmer
import com.iti.presentation.screens.category.components.CategorySearchBar
import com.iti.presentation.screens.category.components.CategoryTopAppBar
import com.iti.presentation.util.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    viewModel: CategoryViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CategoryTopAppBar(
                onDrawerClick = { },
                onCartClick = { }
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            CategorySearchBar(
                query = state.searchQuery,
                onQueryChange = {
                    viewModel.sendIntent(CategoryContract.Intent.SearchQueryChanged(it))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = Constants.CATEGORY_SECTION_TITLE,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (state.isLoading) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 16.dp)
                ) {
                    items(6) {
                        CategoryCardShimmer()
                    }
                }
            } else {
                val filteredCategories = remember(state.categories, state.searchQuery) {
                    if (state.searchQuery.isEmpty()) {
                        state.categories
                    } else {
                        state.categories.filter {
                            it.title.contains(state.searchQuery, ignoreCase = true)
                        }
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 16.dp)
                ) {
                    items(
                        items = filteredCategories,
                        key = { it.id }
                    ) { category ->
                        CategoryCard(
                            category = category,
                            onClick = {
                                viewModel.sendIntent(CategoryContract.Intent.CategoryClicked(category.id))
                            }
                        )
                    }
                }
            }
        }
    }
}
