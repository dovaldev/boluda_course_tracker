package com.dovaldev.boludacoursetracker.database.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dovaldev.boludacoursetracker.R
import com.dovaldev.boludacoursetracker.database.tools.DatabaseFunctions
import com.dovaldev.boludacoursetracker.dovaltools.anko.doAsync
import com.dovaldev.boludacoursetracker.dovaltools.loadGlide
import com.dovaldev.boludacoursetracker.dovaltools.anko.uiThread

class CoursesListAdapter internal constructor(
    var context: Context,
    var coursesListAdapterListener: CoursesListAdapterListener
) : RecyclerView.Adapter<CoursesListAdapter.CourseViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var coursesEntity = emptyList<CoursesEntity>() // Cached copy of words

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        /* val tvStreamer: TextView = itemView.findViewById(R.id.gvTV_Streamer)
         val ivIMGStreamer: ImageView = itemView.findViewById(R.id.gvIV_Streamer)
         val ivOnline: ImageView = itemView.findViewById(R.id.gvIv_Online)
 */
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvSummary: TextView = itemView.findViewById(R.id.tvSummary)
        val ivSrc: ImageView = itemView.findViewById(R.id.ivSrc)
        val btnView: Button = itemView.findViewById(R.id.btnView)
        val btnMark: Button = itemView.findViewById(R.id.btnMark)
        val btnFav: Button = itemView.findViewById(R.id.btnFav)
        val view = itemView.rootView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val itemView = inflater.inflate(R.layout.item_layout_course, parent, false)
        return CourseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val item = coursesEntity[position]
        // load the course title
        holder.tvTitle.text = item.nombreCurso
        // load the course summary
        holder.tvSummary.text = item.infoCurso
        // load image from url
        if (item.imgCurso.isNotEmpty()) {
            holder.ivSrc.loadGlide(item.imgCurso)
        }
        holder.view.setOnClickListener { coursesListAdapterListener.onClick(item, position) }
        holder.btnView.setOnClickListener { coursesListAdapterListener.onClick(item, position) }
        holder.btnMark.setOnClickListener {
            coursesListAdapterListener.onClickWatched(
                item,
                position
            )
        }
        holder.btnFav.setOnClickListener {
            coursesListAdapterListener.onClickFav(
                    item,
                    position
            )
        }
        doAsync {
            val cursoVisto = DatabaseFunctions(context).getCursoVisto(item)
            uiThread {
                when (cursoVisto) {
                    true -> {
                        holder.btnMark.text = context.getString(R.string.chapter_watched_yes)
                    }
                    else -> {
                        holder.btnMark.text = context.getString(R.string.chapter_watched_no)
                    }
                }
            }
        }
        doAsync {
            val cursoFav = DatabaseFunctions(context).getCursoFavorito(item)
            uiThread {
                when (cursoFav) {
                    true -> {
                        holder.btnFav.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.drawable.ic_fav_yes), null, null, null)
                    }
                    else -> {
                        holder.btnFav.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.drawable.ic_fav_no), null, null, null)
                    }
                }
            }
        }


    }

    internal fun setCourses(coursesList: List<CoursesEntity>) {
        this.coursesEntity = coursesList
        notifyDataSetChanged()
    }

    override fun getItemCount() = coursesEntity.size

}