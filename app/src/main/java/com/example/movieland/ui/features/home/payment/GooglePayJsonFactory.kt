package com.example.movieland.ui.features.home.payment

import org.json.JSONArray
import org.json.JSONObject
object GooglePayJsonFactory {
    fun getPaymentDataRequest(price: String): JSONObject? {
        return try {
            JSONObject().apply {
                put("apiVersion", 2)
                put("apiVersionMinor", 0)
                put("allowedPaymentMethods", JSONArray().put(
                    JSONObject().apply {
                        put("type", "CARD")
                        put("parameters", JSONObject().apply {
                            put("allowedAuthMethods", JSONArray().put("PAN_ONLY").put("CRYPTOGRAM_3DS"))
                            put("allowedCardNetworks", JSONArray().put("VISA").put("MASTERCARD"))
                        })
                        put("tokenizationSpecification", JSONObject().apply {
                            put("type", "PAYMENT_GATEWAY")
                            put("parameters", JSONObject().apply {
                                put("gateway", "example")
                                put("gatewayMerchantId", "exampleGatewayMerchantId")
                            })
                        })
                    }
                ))

                put("transactionInfo", JSONObject().apply {
                    put("totalPriceStatus", "FINAL")
                    put("totalPrice", price)
                    put("currencyCode", "VND")
                    put("countryCode", "VN")
                })

                put("merchantInfo", JSONObject().apply {
                    put("merchantName", "Demo Merchant")
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
