package com.polware.sophosmobileapp.data.models

class NewDocument(
    idType: String,
    id: String,
    name: String,
    lastName: String,
    city: String,
    email: String,
    fileType: String,
    attachedImage: String
) {
    var TipoId: String? = idType
    var Identificacion: String? = id
    var Nombre: String? = name
    var Apellido: String? = lastName
    var Ciudad: String? = city
    var Correo: String? = email
    var TipoAdjunto: String? = fileType
    var Adjunto: String? = attachedImage
}