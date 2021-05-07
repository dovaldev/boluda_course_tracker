package com.dovaldev.boludacoursetracker.database.base

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.launch

// Class extends AndroidViewModel and requires application as a parameter.
class CoursesViewModel(application: Application) : AndroidViewModel(application) {

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: CoursesRepository
    // LiveData gives us updated words when they change.
    val allEntities: LiveData<List<CoursesEntity>>
    val allEntities_courses: LiveData<List<CoursesEntity>>

    private val seletedCourse: MutableLiveData<String> = MutableLiveData()
    private var advancedSearch = false

    init {
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val timeDao = CoursesRoomDatabase.getDatabase(application).coursesDao()
        repository = CoursesRepository(timeDao)
        allEntities = repository.allEntities
        allEntities_courses = repository.allEntities_cursos

        // course selected
        seletedCourse.value = ""
        advancedSearch = false
    }

    fun getCoursesList(fav: Boolean): LiveData<List<CoursesEntity>>? {
        val list_seartch = Transformations.switchMap(seletedCourse) { searchCourse ->
            repository.getCourseSearched(searchCourse, fav, advancedSearch)
        }
        return list_seartch
    }

    fun get(courseSearched: String, advanced: Boolean = false){
        Log.i("search", courseSearched)
        seletedCourse.value = courseSearched
        advancedSearch = advanced
    }

    fun getCourseChapterList(): LiveData<List<CoursesEntity>>? {
        val list_seartch = Transformations.switchMap(seletedCourse) { searchCourse ->
            repository.getCourseSelected(searchCourse)
        }
        return list_seartch
    }

    fun getCourseChapter(courseSearched: String){
        Log.i("search", courseSearched)
        seletedCourse.value = courseSearched
    }



    /**
     * The implementation of insert() in the database is completely hidden from the UI.
     * Room ensures that you're not doing any long running operations on
     * the main thread, blocking the UI, so we don't need to handle changing Dispatchers.
     * ViewModels have a coroutine scope based on their lifecycle called
     * viewModelScope which we can use here.
     */
    fun insert(coursesEntity: CoursesEntity) = viewModelScope.launch {
        repository.insert(coursesEntity)
    }

    fun update(coursesEntity: CoursesEntity) = viewModelScope.launch {
        repository.update(coursesEntity)
    }

    // delete info
    fun delete(coursesEntity: CoursesEntity) = viewModelScope.launch {
        repository.delete(coursesEntity)
    }

    // delete all courses
    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }


}