package com.exchange.app.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.exchange.app.data.repository.USER_NAME
import com.exchange.app.domain.entity.Balance
import com.exchange.app.domain.entity.User
import com.exchange.app.domain.model.SubmitState
import com.exchange.app.domain.repository.CurrencyExchangeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val DEFAULT_CURRENCY_EUR_TYPE = "EUR"
private const val DEFAULT_CURRENCY_EUR_AMOUNT = 1000.0
private const val REQUEST_INTERVAL = 5000L

class ExchangeViewModel @Inject constructor(
    private val repository: CurrencyExchangeRepository,
) : ViewModel() {
    var sellAmount: Double? = null
        set(value) {
            field = value
            calculateReceiveAmount()
        }

    var sellCurrencyType: String? = null
        set(value) {
            field = value
            calculateReceiveAmount()
        }

    var receiveCurrencyType: String? = null
        set(value) {
            field = value
            calculateReceiveAmount()
        }

    private var isViewModelActive = true

    private val _balances = MutableLiveData<List<Balance>>()
    val balances: LiveData<List<Balance>>
        get() = _balances

    private val _currencyTypes = MutableLiveData<List<String>>()
    val currencyTypes: LiveData<List<String>>
        get() = _currencyTypes

    private val _receiveAmount = MutableLiveData(0.0)
    val receiveAmount: LiveData<Double>
        get() = _receiveAmount

    private val _submitState = MutableLiveData<SubmitState>()
    val submitState: LiveData<SubmitState>
        get() = _submitState

    init {
        setDefaultUser()
        setDefaultBalance()
        fetchBalances()
        fetchCurrencyRates()
        fetchCurrencyTypes()
    }

    fun isConnect() = repository.isConnect()

    private fun setDefaultUser() {
        viewModelScope.launch {
            if (repository.isUsersEmpty()) {
                repository.saveUser(
                    User(USER_NAME)
                )
            }
        }
    }

    private fun setDefaultBalance() {
        viewModelScope.launch {
            if (repository.isBalancesEmpty()) {
                repository.saveBalance(
                    Balance(
                        DEFAULT_CURRENCY_EUR_TYPE,
                        DEFAULT_CURRENCY_EUR_AMOUNT
                    )
                )
            }
        }
    }

    private fun fetchBalances() {
        viewModelScope.launch {
            repository.getAllBalances().collect { balanceList ->
                _balances.value = balanceList
            }
        }
    }

    private fun fetchCurrencyRates() {
        viewModelScope.launch {
            while (isViewModelActive) {
                repository.fetchCurrencyRates()
                delay(REQUEST_INTERVAL)
            }
        }
    }

    private fun fetchCurrencyTypes() {
        viewModelScope.launch {
            val currencyTypesFlow = repository.getAllCurrencyTypes()
            currencyTypesFlow.collect { currencyTypes ->
                if (currencyTypes.isNotEmpty()) {
                    _currencyTypes.value = currencyTypes
                }
            }
        }
    }

    private fun calculateReceiveAmount() {
        if (!checkExchangeConditions()) return
        viewModelScope.launch {
            _receiveAmount.value = repository.calculateReceiveAmount(
                sellAmount ?: 0.0,
                sellCurrencyType!!,
                receiveCurrencyType!!
            )
        }
    }

    fun onSubmitExchange() {
        if (!checkExchangeConditions()) {
            _submitState.value = SubmitState.NoTypes()
            return
        }
        viewModelScope.launch {
            val sellMoney = Balance(sellCurrencyType!!, sellAmount ?: 0.0)
            val receiveMoney = Balance(receiveCurrencyType!!, _receiveAmount.value!!)
            _submitState.value = repository.submitExchange(sellMoney, receiveMoney)
        }
    }

    private fun checkExchangeConditions(): Boolean {
        return !(sellCurrencyType == null
                || receiveCurrencyType == null)
    }

    override fun onCleared() {
        super.onCleared()
        isViewModelActive = false
    }

    class Factory(
        private val repository: CurrencyExchangeRepository,
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ExchangeViewModel(repository) as T
    }

}