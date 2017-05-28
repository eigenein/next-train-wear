package me.eigenein.nexttrainwear.api

import org.simpleframework.xml.transform.Transform
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class DateFormatTransformer : Transform<Date> {

    private val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH)

    override fun read(value: String?): Date {
        return dateFormat.parse(value)
    }

    override fun write(value: Date?): String {
        throw NotImplementedError()
    }
}
