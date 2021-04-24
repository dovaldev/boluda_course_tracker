package com.dovaldev.boludacoursetracker.onlinegetter

import android.app.Activity
import android.content.Context
import android.os.StrictMode
import android.util.Log
import com.dovaldev.boludacoursetracker.database.base.ChaptersEntity
import com.dovaldev.boludacoursetracker.database.base.CoursesEntity
import com.dovaldev.boludacoursetracker.database.tools.databaseInstaller
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements


/* first version of scrapper boluda.com */
class JsoupGetter(var a: Activity? = null, var c: Context? = null) {


    private fun loadPolicy(){
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

    fun getCourseList(url: String): List<CoursesEntity> {
        Log.i("getCourseList", "Start--> $url")
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
            Log.i("loadURLJsoupGetter", url)

            val courseEntry: Elements? = document.select("div.cursosgrid a")

            for(element in courseEntry!!){
                val urlCurso = element.attr("href").toString()
                val tituloCurso = element.select("h3").text()
                val infoCurso = element.text().replace(tituloCurso.toRegex(), "")
                val imgCurso = element.select("img").attr("src").toString()
                getCourseChaptersList(urlCurso).forEachIndexed { index, chaptersEntity ->
                    val course = CoursesEntity(tituloCurso, urlCurso, infoCurso, imgCurso, (index+1),
                        chaptersEntity.nombreCapitulo, chaptersEntity.URLCapitulo)
                    if(a != null) {
                        databaseInstaller(a!!).directInstallInDatabase(course)
                    }
                    //coursesList.add(course)
                    Log.i("courseEntry", "$tituloCurso | $infoCurso | $imgCurso | #${index+1} ->${chaptersEntity.nombreCapitulo} |--> $urlCurso")
                }


            }



        } catch (e: Exception) {
            e.printStackTrace()
        }

        return coursesList.toList()
    }

    private fun getCourseChaptersList(url: String): List<ChaptersEntity> {
        Log.i("getCourseChaptersList", "Start--> $url")

        val chaptersList = mutableListOf<ChaptersEntity>()

        try {
            val document: Document = Jsoup
                .connect(url).timeout(10000)
                .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                .ignoreHttpErrors(true)
                .get()
            /* geticon */
            Log.i("loadURLJsoupGetter", url)

            val courseEntry: Elements? = document.select("ul.capitulos li a")

            for(element in courseEntry!!){
                val urlChapter = element.attr("href").toString()
                val tituloChapter = element.text()
                Log.i("courseEntry", "$tituloChapter |--> $urlChapter")
                chaptersList.add(ChaptersEntity(tituloChapter, urlChapter))
            }



        } catch (e: Exception) {
            e.printStackTrace()
        }

        return chaptersList.toList()
    }



}