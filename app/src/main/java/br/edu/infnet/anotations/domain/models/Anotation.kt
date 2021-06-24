package br.edu.infnet.anotations.domain.models

import android.graphics.Bitmap

class Anotation(
    var title: String? = null,
    var description: String? = null,
    val date: String? = null,
    val img: Bitmap? = null
)