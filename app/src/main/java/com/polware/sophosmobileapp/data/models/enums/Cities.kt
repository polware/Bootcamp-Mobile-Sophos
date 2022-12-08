package com.polware.sophosmobileapp.data.models.enums

enum class Cities(val city: String, val idOffice: Int) {
    MEDELLIN1("Medellín S1", 1),
    MEDELLIN2("Medellín S2", 2),
    BOGOTA1("Bogotá S1", 3),
    BOGOTA2("Bogotá S2", 4),
    MEXICO("México", 5),
    PANAMA("Panamá", 6),
    CHILE("Chile", 7),
    USA("Estados Unidos", 8);

    companion object {
        fun getIndex(nameCity: String): Int {
            var index = 0
            for (office in values()) {
                if (nameCity == office.city) index = office.idOffice
            }
            return index
        }

        fun getCities(): Array<String?> {
            val result = arrayOfNulls<String>(values().size)
            for (city in values()) {
                result[city.ordinal] = city.city
            }
            return result
        }
    }

}