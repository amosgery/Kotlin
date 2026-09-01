package com.example.dbsample.ui.addedit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dbsample.data.Customer
import com.example.dbsample.data.CustomerRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AddEditCustomerViewModel(
    private val repository: CustomerRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var phone by mutableStateOf("")
    var status by mutableStateOf("Active")

    var isEditMode by mutableStateOf(false)
    private var currentCustomerId: Int? = null

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        // Assume customerId is passed as an argument "customerId"
        val customerId: Int? = savedStateHandle["customerId"]
        if (customerId != null && customerId != -1) {
            isEditMode = true
            currentCustomerId = customerId
            loadCustomer(customerId)
        }
    }

    private fun loadCustomer(id: Int) {
        viewModelScope.launch {
            // Since getAllCustomers returns a Flow, we can filter for the specific customer
            // Or we could have added a getCustomerById to the DAO.
            // Let's assume for simplicity we fetch all and find the one.
            val customer = repository.getAllCustomers().first().find { it.id == id }
            customer?.let {
                name = it.name
                email = it.email
                phone = it.phone
                status = it.status
            }
        }
    }

    fun saveCustomer() {
        if (name.isBlank() || email.isBlank()) {
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.ShowSnackbar("Name and Email are required"))
            }
            return
        }

        viewModelScope.launch {
            val customer = Customer(
                id = currentCustomerId ?: 0,
                name = name,
                email = email,
                phone = phone,
                status = status
            )

            if (isEditMode) {
                repository.updateCustomer(customer)
            } else {
                repository.insertCustomer(customer)
            }
            _uiEvent.emit(UiEvent.NavigateBack)
        }
    }

    fun deleteCustomer() {
        currentCustomerId?.let { id ->
            viewModelScope.launch {
                val customer = repository.getAllCustomers().first().find { it.id == id }
                customer?.let {
                    repository.deleteCustomer(it)
                    _uiEvent.emit(UiEvent.NavigateBack)
                }
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object NavigateBack : UiEvent()
    }
}