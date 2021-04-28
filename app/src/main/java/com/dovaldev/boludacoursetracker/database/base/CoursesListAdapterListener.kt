package com.dovaldev.boludacoursetracker.database.base


interface CoursesListAdapterListener {
    fun onClick(coursesEntity: CoursesEntity, position: Int)
    fun onClickWatched(coursesEntity: CoursesEntity, position: Int)
    fun onClickFav(coursesEntity: CoursesEntity, position: Int)
    fun onClickTime(coursesEntity: CoursesEntity, position: Int)
}