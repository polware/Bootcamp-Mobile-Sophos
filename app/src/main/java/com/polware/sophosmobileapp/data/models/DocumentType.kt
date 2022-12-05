package com.polware.sophosmobileapp.data.models

enum class DocumentType(val type: String, val text: String) {
    CEDULA("CC", "Cédula"),
    TARJETA_ID("TI", "Tarjeta identidad"),
    PASAPORTE("PA", "Pasaporte"),
    CED_EXTRANJERA("CE", "Cédula extranjería");

    companion object {

        fun getText(documentType: String): String {
            var documentName = ""
            for (item in values()) {
                if (documentType == item.type) documentName = item.text
            }
            return documentName
        }

        fun getTypes(): Array<String?> {
            val result = arrayOfNulls<String>(values().size)
            for (item in values()) {
                result[item.ordinal] = item.type
            }
            return result
        }
    }
}