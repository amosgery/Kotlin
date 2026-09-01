package com.example.dbsample.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.dbsample.data.CustomerRepository
import com.example.dbsample.navigation.AddEditCustomerKey
import com.example.dbsample.navigation.CustomerListKey
import com.example.dbsample.navigation.DBSampleNavKey
import com.example.dbsample.ui.addedit.AddEditCustomerScreen
import com.example.dbsample.ui.addedit.AddEditCustomerViewModel
import com.example.dbsample.ui.customerlist.CustomerListScreen
import com.example.dbsample.ui.customerlist.CustomerListViewModel
import androidx.compose.runtime.mutableStateListOf

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun DBSampleApp(repository: CustomerRepository) {
    val backStack = remember { mutableStateListOf<Any>(CustomerListKey) }
    val windowAdaptiveInfo = currentWindowAdaptiveInfo()
    val directive = remember(windowAdaptiveInfo) {
        calculatePaneScaffoldDirective(windowAdaptiveInfo)
            .copy(horizontalPartitionSpacerSize = 0.dp)
    }
    val listDetailStrategy = rememberListDetailSceneStrategy<Any>(directive = directive)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        sceneStrategy = listDetailStrategy,
        entryProvider = entryProvider {
            entry<CustomerListKey>(
                metadata = ListDetailSceneStrategy.listPane(
                    detailPlaceholder = {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Select a customer to edit or tap + to add new", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                )
            ) {
                val viewModel: CustomerListViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return CustomerListViewModel(repository) as T
                        }
                    }
                )
                CustomerListScreen(
                    viewModel = viewModel,
                    onAddCustomer = {
                        backStack.add(AddEditCustomerKey())
                    },
                    onEditCustomer = { customer ->
                        backStack.add(AddEditCustomerKey(customer.id))
                    }
                )
            }
            entry<AddEditCustomerKey>(
                metadata = ListDetailSceneStrategy.detailPane()
            ) { key ->
                val viewModel: AddEditCustomerViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            val savedStateHandle = androidx.lifecycle.SavedStateHandle(
                                mapOf("customerId" to key.customerId)
                            )
                            return AddEditCustomerViewModel(repository, savedStateHandle) as T
                        }
                    }
                )
                AddEditCustomerScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
        }
    )
}
