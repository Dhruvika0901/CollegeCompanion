//package com.example.collegecompanion.ui.screens.dashboard
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.collegecompanion.data.repository.TaskRepository
//import com.example.collegecompanion.domain.model.Task
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.*
//import javax.inject.Inject
//
//data class DashboardUiState(
//    val userName: String         = "Alex",
//    val streakDays: Int          = 0,
//    val dueTodayCount: Int       = 0,
//    val dueWeekCount: Int        = 0,
//    val completionPercent: Int   = 0,
//    val upcomingTasks: List<Task> = emptyList(),
//    val nextClassName: String    = "No class today 🎉",
//    val nextClassRoom: String    = "",
//    val nextClassTime: String    = ""
//)
//
//@HiltViewModel
//class DashboardViewModel @Inject constructor(
//    private val taskRepository: TaskRepository
//) : ViewModel() {
//
//    private val _uiState = MutableStateFlow(DashboardUiState())
//    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
//
//    init {
//        observeTasks()
//    }
//
//    private fun observeTasks() {
//        taskRepository.getAllTasks()
//            .onEach { tasks ->
//                val today        = System.currentTimeMillis()
//                val weekMs       = 7L * 24 * 60 * 60 * 1000
//                val dueToday     = tasks.count { it.dueDate?.let { d -> isSameDay(d, today) } == true && !it.isCompleted }
//                val dueThisWeek  = tasks.count { it.dueDate?.let { d -> d > today && d < today + weekMs } == true && !it.isCompleted }
//                val completed    = tasks.count { it.isCompleted }
//                val pct          = if (tasks.isEmpty()) 100 else (completed * 100 / tasks.size)
//                val upcoming     = tasks
//                    .filter { !it.isCompleted }
//                    .sortedBy { it.dueDate ?: Long.MAX_VALUE }
//
//                _uiState.update {
//                    it.copy(
//                        dueTodayCount     = dueToday,
//                        dueWeekCount      = dueThisWeek,
//                        completionPercent = pct,
//                        upcomingTasks     = upcoming
//                    )
//                }
//            }
//            .launchIn(viewModelScope)
//    }
//
//    private fun isSameDay(ts1: Long, ts2: Long): Boolean {
//        val cal1 = java.util.Calendar.getInstance().apply { timeInMillis = ts1 }
//        val cal2 = java.util.Calendar.getInstance().apply { timeInMillis = ts2 }
//        return cal1.get(java.util.Calendar.YEAR)         == cal2.get(java.util.Calendar.YEAR) &&
//                cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR)
//    }
//}