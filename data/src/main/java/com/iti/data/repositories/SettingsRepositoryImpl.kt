package com.iti.data.repositories

import com.iti.data.sources.local.currency.CurrencyLocalDataSource
import com.iti.data.sources.local.onboarding.OnboardingLocalDataSource
import com.iti.data.sources.remote.currency.CurrencyRemoteDataSource
import com.iti.domain.models.Currency
import com.iti.domain.repositories.settings.SettingsRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class SettingsRepositoryImpl(
    private val remoteDataSource: CurrencyRemoteDataSource,
    private val currencyLocalDataSource: CurrencyLocalDataSource,
    private val localDataSource: OnboardingLocalDataSource
) : SettingsRepository {

private val fallbackCurrencies = listOf(Currency("USD", "US Dollar", "$", 1.0))
    private val _currenciesState = MutableStateFlow<List<Currency>>(emptyList())

    override fun getSelectedCurrency(): Flow<Currency> {
        return currencyLocalDataSource.getSelectedCurrency()
            .map { it ?: fallbackCurrencies.first() }
            .onStart { emit(fallbackCurrencies.first()) }
    }

    override fun getPopularCurrencies(): Flow<List<Currency>> {
        return _currenciesState.map { list ->
            val popularCodes = listOf("USD", "EUR", "GBP", "INR", "AED", "SAR", "EGP")
            list.filter { it.code in popularCodes }
        }
    }

    override fun getExchangeRateHistory(currencyCode: String): Flow<List<Pair<String, Double>>> = flow {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val calendar = Calendar.getInstance()
        val endDate = sdf.format(calendar.time)
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val startDate = sdf.format(calendar.time)

        try {
            val history = remoteDataSource.getExchangeRateHistory(currencyCode, startDate, endDate, "USD")
            emit(history)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun fetchExchangeRates() {
        try {
            val updatedList = remoteDataSource.fetchExchangeRates("USD")
            _currenciesState.value = updatedList
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun changeSelectedCurrency(code: String) {
        val currency = getCurrencyByCode(code)
        currencyLocalDataSource.saveSelectedCurrency(currency)
    }

    private fun getCurrencyByCode(code: String): Currency {
        val list = _currenciesState.value
        return list.firstOrNull { it.code == code }
            ?: Currency("USD", "US Dollar", "$", 1.0)
    }

override fun isOnboardingCompleted(): Flow<Boolean> {
        return localDataSource.isOnboardingCompleted()
    }

    override suspend fun setOnboardingCompleted() {
        localDataSource.setOnboardingCompleted()
    }
}
