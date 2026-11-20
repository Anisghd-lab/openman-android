package com.ghdinteractivestudio.openman

import android.app.Application
import com.ghdinteractivestudio.openman.data.repository.AgentEngine
import com.ghdinteractivestudio.openman.data.repository.SettingsRepository
import com.ghdinteractivestudio.openman.data.repository.TaskRepository

class OpenManApp : Application() {
    lateinit var taskRepository: TaskRepository
        private set

    lateinit var settingsRepository: SettingsRepository
        private set

    lateinit var agentEngine: AgentEngine
        private set

    override fun onCreate() {
        super.onCreate()
        settingsRepository = SettingsRepository()
        taskRepository = TaskRepository()
        agentEngine = AgentEngine(taskRepository, settingsRepository)
    }
}
