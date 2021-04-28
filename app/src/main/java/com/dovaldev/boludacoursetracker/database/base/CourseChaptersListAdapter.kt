package com.dovaldev.boludacoursetracker.database.base

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dovaldev.boludacoursetracker.R
import com.dovaldev.boludacoursetracker.dovaltools.setTwoDigitsFullTime

class CourseChaptersListAdapter internal constructor(
    var context: Context,
    var coursesListAdapterListener: CoursesListAdapterListener
) : RecyclerView.Adapter<CourseChaptersListAdapter.CourseViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var coursesEntity = emptyList<CoursesEntity>() // Cached copy of words

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val btnView: Button = itemView.findViewById(R.id.btnView)
        val btnMark: Button = itemView.findViewById(R.id.btnMark)
        val btnTime: Button = itemView.findViewById(R.id.btnTime)
        val view = itemView.rootView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val itemView = inflater.inflate(R.layout.item_layout_course_chapter, parent, false)
        return CourseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val item = coursesEntity[position]
        // load the course title
        holder.tvTitle.text = item.capituloCurso.removeInitialTitle()

        // buttons
        //holder.view.setOnClickListener {  coursesListAdapterListener.onClick(item, position)}
        holder.btnView.setOnClickListener {  coursesListAdapterListener.onClick(item, position)}
        holder.btnMark.setOnClickListener {  coursesListAdapterListener.onClickWatched(
            item,
            position
        )}
        when(item.capituloVisto){
            true -> {
                holder.btnMark.text = context.getString(R.string.chapter_watched_yes)
                holder.tvTitle.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            }
            else -> {
                holder.btnMark.text = context.getString(R.string.chapter_watched_no)
                holder.tvTitle.paintFlags = Paint.LINEAR_TEXT_FLAG
            }
        }
        holder.btnTime.setOnClickListener {
            coursesListAdapterListener.onClickTime(item, position)
        }
        holder.btnTime.text = item.tiempoGuardado.setTwoDigitsFullTime()


    }

    internal fun setCourses(coursesList: List<CoursesEntity>) {
        this.coursesEntity = coursesList
        notifyDataSetChanged()
    }

    override fun getItemCount() = coursesEntity.size


    private fun String.removeInitialTitle(): String {
        if(!this.startsWith("#")){
            val char = this.indexOf("#")
            return this.substring(char, this.length).trim()
        } else {
            return this
        }
    }
}