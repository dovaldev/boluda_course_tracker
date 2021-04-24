package com.dovaldev.boludacoursetracker.database.tools

import android.app.Activity
import android.util.Log
import com.dovaldev.boludacoursetracker.R
import com.dovaldev.boludacoursetracker.database.base.CoursesViewModel
import com.dovaldev.boludacoursetracker.dovaltools.*
import com.dovaldev.boludacoursetracker.dovaltools.anko.doAsync
import com.dovaldev.boludacoursetracker.dovaltools.anko.uiThread
import com.dovaldev.boludacoursetracker.onlinegetter.JsoupGetter
import com.dovaldev.boludacoursetracker.onlinegetter.JsoupGetterV2

class Downloader(var a: Activity, var courseViewModel: CoursesViewModel) {

    /* The basic version to download is more slow because open each course web to scrap the chapters information*/
    fun downloadOnlineCourses() {
        //Log.i("Downloader", "executing")
        // get the favourites and watched chapters
        val dAfterInstall = a.dialogDownloadingCourses(a.getString(R.string.dialog_obtaining),
            a.getString(R.string.dialog_obtaining_msg))
        doAsync {
            databaseInstaller(a).beforeInstall()
            uiThread {
                // cancel dialog doing
                dAfterInstall.cancel()
                // get the list and install the courses
                val dInstalling = a.dialogDownloadingCourses()
                dInstalling.show()
                doAsync {
                    // delete courses
                    courseViewModel.deleteAll()
                    // get and intall courses
                    JsoupGetter(a).getCourseList(web_boluda)
                    uiThread {
                        // cancel dialog install courses
                        dInstalling.cancel()
                        // update the list with last viewed courses and favourites
                        val dBeforeInstall = a.dialogDownloadingCourses(a.getString(R.string.dialog_updating),
                            a.getString(R.string.dialog_updating_msg))
                        doAsync {
                            databaseInstaller(a).afterInstall()
                            uiThread {
                                dBeforeInstall.cancel()
                                a.showToast(a.getString(R.string.toast_all_data_updated))
                            }
                        }

                    }
                }
            }
        }
    }

    /* New version for download the courses more fast */
    fun downloadOnlineCoursesV2() {
        // get the favourites and watched chapters
        val dAfterInstall = a.dialogDownloadingCourses(a.getString(R.string.dialog_obtaining),
            a.getString(R.string.dialog_obtaining_msg))
        doAsync {
            databaseInstaller(a).beforeInstall()
            uiThread {
                // cancel dialog doing
                dAfterInstall.cancel()
                // get the list and install the courses
                val dInstalling = a.dialogDownloadingCourses()
                dInstalling.show()
                doAsync {
                    // delete courses
                    courseViewModel.deleteAll()
                    // get and intall courses
                    JsoupGetterV2(a).getCourseList(web_boluda_cursos)
                    uiThread {
                        // cancel dialog install courses
                        dInstalling.cancel()
                        // update the list with last viewed courses and favourites
                        val dBeforeInstall = a.dialogDownloadingCourses(a.getString(R.string.dialog_updating),
                            a.getString(R.string.dialog_updating_msg))
                        doAsync {
                            databaseInstaller(a).afterInstall()
                            uiThread {
                                dBeforeInstall.cancel()
                                a.showToast(a.getString(R.string.toast_all_data_updated))
                            }
                        }

                    }
                }
            }
        }
    }
}