package ru.iitp.brighttickets

import android.net.Uri
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

private val gson: Gson = GsonBuilder()
    .registerTypeAdapter(Uri::class.java, UriTypeAdapter()) // Register Uri TypeAdapter
    .create()

fun savePhotos(context: Context, photoList: List<Photo>) {
    val preferences = context.getSharedPreferences("PhotoApp", Context.MODE_PRIVATE)
    val editor = preferences.edit()

    val json = gson.toJson(photoList)
    editor.putString("photos", json)
    editor.apply()
}

fun loadPhotos(context: Context): List<Photo> {
    val preferences = context.getSharedPreferences("PhotoApp", Context.MODE_PRIVATE)
    val json = preferences.getString("photos", null)

    return if (json != null) {
        // Convert JSON back to List<Photo>
        val type = object : TypeToken<List<Photo>>() {}.type
        gson.fromJson(json, type)
    } else {
        emptyList() // Return an empty list if no data is found
    }
}

class UriTypeAdapter : TypeAdapter<Uri>() {
    override fun write(out: JsonWriter, value: Uri?) {
        out.value(value.toString()) // Serialize Uri to string
        Log.d("cromtus", value.toString())
    }

    override fun read(`in`: JsonReader): Uri {
        return Uri.parse(`in`.nextString()) // Deserialize string back to Uri
    }
}
