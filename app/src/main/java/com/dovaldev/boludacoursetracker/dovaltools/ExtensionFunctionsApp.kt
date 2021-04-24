package com.dovaldev.boludacoursetracker.dovaltools

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.dovaldev.boludacoursetracker.R
import com.dovaldev.boludacoursetracker.dovaltools.anko.Internals
import com.dovaldev.boludacoursetracker.dovaltools.anko.doAsync
import com.dovaldev.boludacoursetracker.dovaltools.anko.uiThread


// go to selected activity
inline fun <reified T : Activity> Activity.goToActivity(noinline init: Intent.() -> Unit = {}) {
    val intent = Intent(this, T::class.java)
    intent.init()
    startActivity(intent)
}

inline fun <reified T: Any> Context.intentFor(vararg params: Pair<String, Any?>): Intent =
    Internals.createIntent(this, T::class.java, params)

// null checker
fun Any?.notNull(f: ()-> Unit){
    if (this != null){
        f()
    }
}

// Dialogs
// install courses
fun Activity.dialogInstallCourses(function: () ->(Unit)) {
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
fun Activity.dialogDownloadingCourses(title: String? = null, msg:String? = null): AlertDialog {
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
fun Context.loadURL(url: String){
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(url)
    startActivity(intent)
}