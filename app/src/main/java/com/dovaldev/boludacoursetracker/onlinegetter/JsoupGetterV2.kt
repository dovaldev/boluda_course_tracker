package com.dovaldev.boludacoursetracker.onlinegetter

import android.app.Activity
import android.content.Context
import android.os.StrictMode
import android.util.Log
import com.dovaldev.boludacoursetracker.database.base.CoursesEntity
import com.dovaldev.boludacoursetracker.database.tools.databaseInstaller
import com.dovaldev.boludacoursetracker.dovaltools.web_boluda
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements


/* updated version to scrap the boluda.com courses */
class JsoupGetterV2(var a: Activity? = null, var c: Context? = null) {


    private fun loadPolicy(){
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

    fun getCourseList(url: String): List<CoursesEntity> {
        //Log.i("getCourseList", "Start--> $url")
        // cargar policy para evitar que se suspenda la app mientras esta ejecutandose.
        loadPolicy()

        val coursesList = mutableListOf<CoursesEntity>()

        try {
            val document: Document = Jsoup
                .connect(url).timeout(10000)
                .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                .ignoreHttpErrors(true)
                .get()
            /* geticon */
            //Log.i("loadURLJsoupGetter", url)

            val courseEntry: Elements? = document.select("div.entry-content>li.page_item_has_children")

            // scrap all courses and chapters
            for(element in courseEntry!!){

                val urlCurso = element.select(">a").attr("href").toString()
                val tituloCurso = element.select(">a").text()

                //Log.i("JsoupGetterV2", "-$urlCurso || $tituloCurso")
                //Log.i("JsoupGetterV2-html", element.html())
                val singleCourse: Elements? = element.select("ul li")
                singleCourse?.forEachIndexed { index, chapter ->
                    val chapterUrl = chapter.select("a").attr("href").toString()
                    val chapterTitle = chapter.select("a").text()
                    //Log.i("JsoupGetterV2-chapter", "chapter-> $chapterUrl || $chapterTitle")

                    val course = CoursesEntity(tituloCurso, urlCurso, "", "", (index+1),
                        chapterTitle, chapterUrl)
                    if(a != null) {
                        coursesList.add(course)
                        databaseInstaller(a!!).directInstallInDatabase(course)
                    }
                }
            }
            // get all images and course information
            val basicList = getCourseListImageInfo(web_boluda)
            basicList.forEach {
                databaseInstaller(a!!).installImagesAndInfo(it)
            }






        } catch (e: Exception) {
            e.printStackTrace()
        }

        return coursesList.toList()
    }

    fun getCourseListImageInfo(url: String): List<CoursesEntity> {
        //Log.i("getCourseList", "Start--> $url")
        // cargar policy para evitar que se suspenda la app mientras esta ejecutandose.
        loadPolicy()

        val basicCursesList = mutableListOf<CoursesEntity>()

        try {
            val document: Document = Jsoup
                .connect(url).timeout(10000)
                .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                .ignoreHttpErrors(true)
                .get()
            /* geticon */
            //Log.i("loadURLJsoupGetter", url)

            val courseEntry: Elements? = document.select("div.cursosgrid a")

            for(element in courseEntry!!){
                val urlCurso = element.attr("href").toString()
                val tituloCurso = element.select("h3").text()
                val infoCurso = element.text().replace(tituloCurso.toRegex(), "")
                val imgCurso = element.select("img").attr("src").toString()
                val basicCourseList = CoursesEntity(tituloCurso, urlCurso, infoCurso, imgCurso,-1,
                        "", "")
                basicCursesList.add(basicCourseList)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return basicCursesList.toList()
    }



}