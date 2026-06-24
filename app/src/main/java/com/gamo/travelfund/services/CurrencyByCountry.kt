package com.gamo.travelfund.services

object CurrencyByCountry {
    private val map = mapOf(
        "mx" to "MXN", "us" to "USD", "gb" to "GBP", "eu" to "EUR",
        "de" to "EUR", "fr" to "EUR", "es" to "EUR", "it" to "EUR",
        "pt" to "EUR", "nl" to "EUR", "be" to "EUR", "at" to "EUR",
        "jp" to "JPY", "cn" to "CNY", "kr" to "KRW", "in" to "INR",
        "br" to "BRL", "ar" to "ARS", "co" to "COP", "cl" to "CLP",
        "pe" to "PEN", "uy" to "UYU", "bo" to "BOB", "py" to "PYG",
        "ve" to "VES", "ec" to "USD", "pa" to "USD", "cr" to "CRC",
        "gt" to "GTQ", "hn" to "HNL", "ni" to "NIO", "sv" to "USD",
        "do" to "DOP", "cu" to "CUP", "ca" to "CAD", "au" to "AUD",
        "nz" to "NZD", "sg" to "SGD", "hk" to "HKD", "th" to "THB",
        "id" to "IDR", "my" to "MYR", "ph" to "PHP", "vn" to "VND",
        "tr" to "TRY", "sa" to "SAR", "ae" to "AED", "eg" to "EGP",
        "za" to "ZAR", "ng" to "NGN", "ke" to "KES", "ma" to "MAD",
        "ru" to "RUB", "pl" to "PLN", "cz" to "CZK", "hu" to "HUF",
        "ro" to "RON", "se" to "SEK", "no" to "NOK", "dk" to "DKK",
        "ch" to "CHF", "il" to "ILS", "pk" to "PKR", "bd" to "BDT",
    )

    fun fromCountryCode(code:String?): String? = map[code?.lowercase()]
}