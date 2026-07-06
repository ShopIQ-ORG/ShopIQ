package com.iti.data.sources.remote.currency

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.iti.domain.models.Currency
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import java.text.SimpleDateFormat
import java.util.Locale

class CurrencyRemoteDataSourceImpl(
    private val client: HttpClient
) : CurrencyRemoteDataSource {
    override suspend fun fetchExchangeRates(baseCurrency: String): List<Currency> {
        val ratesResponse = client.get("https://api.exchangerate-api.com/v4/latest/$baseCurrency")
        val ratesJson = Gson().fromJson(ratesResponse.bodyAsText(), JsonObject::class.java)
        val ratesObject = ratesJson.getAsJsonObject("rates")

        val updatedList = mutableListOf<Currency>()
        for (code in ratesObject.keySet()) {
            val rate = ratesObject.get(code).asDouble
            var name = code
            var symbol = code
            try {
                val javaCurrency = java.util.Currency.getInstance(code)
                name = javaCurrency.displayName
                symbol = javaCurrency.symbol
            } catch (e: Exception) {
                // Ignore unknown currencies
            }
            updatedList.add(Currency(code, name, symbol, rate))
        }
        return updatedList
    }

    override suspend fun getExchangeRateHistory(
        currencyCode: String,
        startDate: String,
        endDate: String,
        baseCurrency: String
    ): List<Pair<String, Double>> {
        val url = "https://api.frankfurter.app/$startDate..$endDate?from=$baseCurrency&to=$currencyCode"
        val response = client.get(url)
        val body = response.bodyAsText()
        val jsonObject = Gson().fromJson(body, JsonObject::class.java)
        val ratesObject = jsonObject.getAsJsonObject("rates")
        val history = mutableListOf<Pair<String, Double>>()

        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val outputFormat = SimpleDateFormat("EEE", Locale.US)

        for (dateKey in ratesObject.keySet()) {
            val rateVal = ratesObject.getAsJsonObject(dateKey).get(currencyCode).asDouble
            val date = inputFormat.parse(dateKey)
            val dayLabel = if (date != null) outputFormat.format(date) else dateKey
            history.add(dayLabel to rateVal)
        }
        return history
    }
}
