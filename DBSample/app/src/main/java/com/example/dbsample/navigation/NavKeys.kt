package com.example.dbsample.navigation

sealed interface DBSampleNavKey

object CustomerListKey : DBSampleNavKey

data class AddEditCustomerKey(val customerId: Int? = null) : DBSampleNavKey
