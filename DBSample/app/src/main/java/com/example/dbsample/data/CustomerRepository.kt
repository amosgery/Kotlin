package com.example.dbsample.data

import kotlinx.coroutines.flow.Flow

class CustomerRepository(private val customerDao: CustomerDao) {
    fun getAllCustomers(): Flow<List<Customer>> = customerDao.getAllCustomers()

    fun searchCustomers(query: String): Flow<List<Customer>> = 
        customerDao.searchCustomers("%$query%")

    suspend fun insertCustomer(customer: Customer) = customerDao.insertCustomer(customer)

    suspend fun updateCustomer(customer: Customer) = customerDao.updateCustomer(customer)

    suspend fun deleteCustomer(customer: Customer) = customerDao.deleteCustomer(customer)
}