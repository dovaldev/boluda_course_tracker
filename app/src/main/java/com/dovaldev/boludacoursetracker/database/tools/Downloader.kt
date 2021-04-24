package com.dovaldev.boludacoursetracker.database.tools

import android.app.Activity
import com.dovaldev.boludacoursetracker.database.base.CoursesViewModel
import com.dovaldev.boludacoursetracker.dovaltools.*
import com.dovaldev.boludacoursetracker.onlinegetter.JsoupGetter
import com.dovaldev.boludacoursetracker.onlinegetter.JsoupGetterV2

class Downloader(var a: Activity, var courseViewModel: CoursesViewModel) {

    /* The basic version to download is more slow because open each course web to scrap the chapters information*/
    fun downloadOnlineCourses() {
        // get the favourites and watched chapters
        val dAfterInstall = a.dialogDownloadingCourses("Obteniendo", "Información de la base de datos, favoritos y capítulos vistos...")
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
                        val dBeforeInstall = a.dialogDownloadingCourses("Actualizando", "Información de la base de datos, favoritos y capítulos vistos...")
                        doAsync {
                            databaseInstaller(a).afterInstall()
                            uiThread {
                                dBeforeInstall.cancel()
                                a.showToast("Todos los datos se han actualizado correctamente...")
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
        val dAfterInstall = a.dialogDownloadingCourses("Obteniendo", "Información de la base de datos, favoritos y capítulos vistos...")
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
                        val dBeforeInstall = a.dialogDownloadingCourses("Actualizando", "Información de la base de datos, favoritos y capítulos vistos...")
                        doAsync {
                            databaseInstaller(a).afterInstall()
                            uiThread {
                                dBeforeInstall.cancel()
                                a.showToast("Todos los datos se han actualizado correctamente...")
                            }
                        }

                    }
                }
            }
        }
    }
}