package com.dovaldev.boludacoursetracker.dovaltools

import android.app.Activity
import android.content.Context
import android.util.Log

class Prefs (activity: Activity) {

    // SharedPreferences
    private val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)

    // active the advanced searched mode
    var advanced_search: Boolean
        get() = sharedPref.getBoolean("advanced_search", false)
        set(value) = sharedPref.edit().putBoolean("advanced_search", value).apply()


    fun advanced_search_switch(): Boolean{
        advanced_search = advanced_search.not()

        Log.i("advancedSearched", advanced_search.toString())
        return advanced_search
    }
}