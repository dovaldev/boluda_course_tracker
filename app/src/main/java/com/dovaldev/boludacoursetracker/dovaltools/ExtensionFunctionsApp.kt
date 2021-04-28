package com.dovaldev.boludacoursetracker.dovaltools

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.dovaldev.boludacoursetracker.R
import com.dovaldev.boludacoursetracker.database.base.CoursesDao
import com.dovaldev.boludacoursetracker.database.base.CoursesEntity
import com.dovaldev.boludacoursetracker.dovaltools.anko.Internals
import com.dovaldev.boludacoursetracker.dovaltools.anko.doAsync
import com.dovaldev.boludacoursetracker.dovaltools.anko.uiThread
import com.google.android.material.textfield.TextInputEditText


// go to selected activity
inline fun <reified T : Activity> Activity.goToActivity(noinline init: Intent.() -> Unit = {}) {
    val intent = Intent(this, T::class.java)
    intent.init()
    startActivity(intent)
}

inline fun <reified T : Any> Context.intentFor(vararg params: Pair<String, Any?>): Intent =
        Internals.createIntent(this, T::class.java, params)

// null checker
fun Any?.notNull(f: () -> Unit) {
    if (this != null) {
        f()
    }
}

// Dialogs
// install courses
fun Activity.dialogInstallCourses(function: () -> (Unit)) {
    with(this) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(
                getString(R.string.dialog_msg_install)
        )
                .setPositiveButton(getString(R.string.dialog_msg_install_ok)) { _, _ ->
                    function()
                }
                .setNegativeButton(getString(R.string.dialog_msg_install_cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
        // Create the AlertDialog object and return it
        builder.create().show()
    }
}

// set time left
fun Activity.dialogSetTimeStatus(entity: CoursesEntity, dao: CoursesDao) {
    val time = entity.getTime()
    val builder = AlertDialog.Builder(this)
    // Get the layout inflater
    val inflater = layoutInflater
    val view = inflater.inflate(R.layout.dialog_time_lapse, null)
    val etMinutes = view.findViewById<TextInputEditText>(R.id.etMinutes)
    val etSeconds = view.findViewById<TextInputEditText>(R.id.etSeconds)
    when{
        time.min == "00" -> {
            etMinutes.hint = time.min
            etSeconds.hint = time.sec
        }
        time.min != "00" -> {
            etMinutes.setText(time.min)
            etSeconds.setText(time.sec)
        }
    }


    builder.setView(view)
    builder.setCancelable(false)
    builder
            .setPositiveButton(android.R.string.ok) { _, _ ->
                // update time entity
                if(etMinutes.text!!.isNotEmpty() && etSeconds.text!!.isNotEmpty()){
                    entity.tiempoGuardado = timeOfET(etMinutes, etSeconds)
                    // save into dao
                    doAsync {
                        dao.update(entity)
                        uiThread {
                            showToast(getString(R.string.toast_updated_time))
                        }
                    }
                }
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }

    val dialog = builder.create()
    dialog.show()
}

// dialog to show a message while the courses are installing
fun Activity.dialogInstallingDatabase(): AlertDialog {
    val builder = AlertDialog.Builder(this)
    // Get the layout inflater
    val inflater = layoutInflater
    val view = inflater.inflate(R.layout.dialog_doing_action, null)
    val message = view.findViewById<TextView>(R.id.dialog_msg)
    message.text = getString(R.string.dialog_msg_instalando_los_cursos)

    builder.setView(view)
            .setTitle(getString(R.string.dialog_msg_instalando_los_cursos_title))
    builder.setCancelable(false)
    val dialog = builder.create()
    return dialog
}

// custom dialog to show a message while the courses are installing
fun Activity.dialogDownloadingCourses(title: String? = null, msg: String? = null): AlertDialog {
    val builder = AlertDialog.Builder(this)
    // Get the layout inflater
    val inflater = layoutInflater
    val view = inflater.inflate(R.layout.dialog_doing_action, null)
    val message = view.findViewById<TextView>(R.id.dialog_msg)

    when (msg) {
        null -> message.text = getString(R.string.dialog_msg_descargando_los_cursos)
        else -> message.text = msg
    }

    builder.setView(view)

    when (title) {
        null -> builder.setTitle(getString(R.string.dialog_msg_descargando_los_cursos_title))
        else -> builder.setTitle(title)
    }

    builder.setCancelable(false)
    val dialog = builder.create()
    return dialog

}

// Toast
fun Context.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

// Image load
fun ImageView.loadGlide(url: String) {
    try {
        Glide.with(this).load(url).into(this)
    } catch (e: Exception) {
    }
}

// Intents
fun Context.loadURL(url: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(url)
    startActivity(intent)
}


// database functions
fun CoursesEntity.getTime(): CourseTime{
    val time = this.tiempoGuardado
    return if(time.isNotEmpty() && time.contains(":")) {
        CourseTime(time.split(":")[0].setTwoDigits(), time.split(":")[1].setTwoDigits())
    } else {
        CourseTime("00", "00")
    }
}

fun timeOfET(etMin: TextInputEditText, etSec: TextInputEditText): String{
    return if(etMin.text!!.isNotEmpty() && etSec.text!!.isNotEmpty()){
        "${etMin.text}:${etSec.text}"
    } else {
        "00:00"
    }
}
fun String.setTwoDigits(): String {
    return if(this.length == 1) {
        "0$this"
    } else {
        this
    }
}
fun String.setTwoDigitsFullTime(): String {
    return if(this.contains(":")){
        val time = this.split(":")
        "${time[0].setTwoDigits()}:${time[1].setTwoDigits()}"
    } else {
        "00:00"
    }
}

data class CourseTime(var min: String, var sec: String)