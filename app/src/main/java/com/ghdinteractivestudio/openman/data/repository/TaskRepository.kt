package com.ghdinteractivestudio.openman.data.repository

import com.ghdinteractivestudio.openman.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class TaskRepository {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _steps = MutableStateFlow<Map<String, List<Step>>>(emptyMap())
    val steps: StateFlow<Map<String, List<Step>>> = _steps.asStateFlow()

    private val _artifacts = MutableStateFlow<Map<String, List<Artifact>>>(emptyMap())
    val artifacts: StateFlow<Map<String, List<Artifact>>> = _artifacts.asStateFlow()

    private val _messages = MutableStateFlow<Map<String, List<ChatMessage>>>(emptyMap())
    val messages: StateFlow<Map<String, List<ChatMessage>>> = _messages.asStateFlow()

    private val _skills = MutableStateFlow<List<Skill>>(defaultSkills)
    val skills: StateFlow<List<Skill>> = _skills.asStateFlow()

    init {
        seedSampleTask()
    }

    fun createTask(prompt: String, mode: TaskMode, model: String): Task {
        val title = prompt.take(45).let { if (prompt.length > 45) "$it..." else it }
        val newTask = Task(
            id = UUID.randomUUID().toString(),
            title = title,
            prompt = prompt,
            mode = mode,
            status = TaskStatus.PENDING,
            modelUsed = model,
            createdAt = System.currentTimeMillis()
        )
        _tasks.value = listOf(newTask) + _tasks.value
        _steps.value = _steps.value + (newTask.id to emptyList())
        _artifacts.value = _artifacts.value + (newTask.id to emptyList())
        _messages.value = _messages.value + (newTask.id to listOf(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                taskId = newTask.id,
                sender = "user",
                content = prompt
            )
        ))
        return newTask
    }

    fun updateTaskStatus(taskId: String, status: TaskStatus, summary: String? = null, liveUrl: String? = null) {
        _tasks.value = _tasks.value.map { task ->
            if (task.id == taskId) {
                task.copy(
                    status = status,
                    summary = summary ?: task.summary,
                    liveUrl = liveUrl ?: task.liveUrl,
                    completedAt = if (status == TaskStatus.COMPLETED) System.currentTimeMillis() else task.completedAt
                )
            } else task
        }
    }

    fun setSteps(taskId: String, newSteps: List<Step>) {
        _steps.value = _steps.value + (taskId to newSteps)
    }

    fun updateStep(taskId: String, updatedStep: Step) {
        val current = _steps.value[taskId] ?: emptyList()
        val modified = current.map { if (it.id == updatedStep.id) updatedStep else it }
        _steps.value = _steps.value + (taskId to modified)
    }

    fun addArtifact(taskId: String, artifact: Artifact) {
        val current = _artifacts.value[taskId] ?: emptyList()
        _artifacts.value = _artifacts.value + (taskId to current + artifact)
    }

    fun addMessage(taskId: String, sender: String, content: String) {
        val current = _messages.value[taskId] ?: emptyList()
        val msg = ChatMessage(
            id = UUID.randomUUID().toString(),
            taskId = taskId,
            sender = sender,
            content = content
        )
        _messages.value = _messages.value + (taskId to current + msg)
    }

    fun toggleSkill(skillId: String) {
        _skills.value = _skills.value.map {
            if (it.id == skillId) it.copy(isEnabled = !it.isEnabled) else it
        }
    }

    private fun seedSampleTask() {
        val sampleId = "sample-manus-001"
        val sampleTask = Task(
            id = sampleId,
            title = "Agent Framework Comparison 2026",
            prompt = "Pick three widely used open-source agent frameworks and give me a one-page Markdown comparison.",
            mode = TaskMode.AGENT,
            status = TaskStatus.COMPLETED,
            modelUsed = "gemini-3.8-flash",
            createdAt = System.currentTimeMillis() - 3600000,
            completedAt = System.currentTimeMillis() - 3540000,
            summary = "Autonomous research completed. Extracted metrics for MetaGPT, CrewAI and LangGraph with star history and architecture analysis.",
            liveUrl = "https://github.com/langchain-ai/langgraph"
        )
        _tasks.value = listOf(sampleTask)

        val sampleSteps = listOf(
            Step(
                id = "step-1",
                taskId = sampleId,
                stepIndex = 1,
                title = "Shortlist top agent frameworks",
                description = "Querying tech repositories and ecosystem benchmarks",
                status = StepStatus.DONE,
                toolType = ToolType.SEARCH,
                toolInput = "best open-source AI agent frameworks 2026",
                toolOutput = "Identified MetaGPT (71.4k stars), CrewAI (61.1k stars), and LangGraph (45.1k stars).",
                thinking = "LangGraph leads for deterministic state machines, CrewAI for multi-role workflows, and MetaGPT for full-cycle software simulation."
            ),
            Step(
                id = "step-2",
                taskId = sampleId,
                stepIndex = 2,
                title = "Inspect GitHub repositories & APIs",
                description = "Executing curl script to query GitHub REST endpoints",
                status = StepStatus.DONE,
                toolType = ToolType.TERMINAL,
                toolInput = "python3 -c 'import urllib.request; print(\"Fetching GitHub stars...\")'",
                toolOutput = "MetaGPT: 71,351 stars | CrewAI: 61,088 stars | LangGraph: 45,136 stars",
                thinking = "Collected star velocity, MIT licenses verified on all three projects."
            ),
            Step(
                id = "step-3",
                taskId = sampleId,
                stepIndex = 3,
                title = "Navigate repo docs via Browser",
                description = "Simulating browser visit to langchain-ai/langgraph",
                status = StepStatus.DONE,
                toolType = ToolType.BROWSER,
                toolInput = "https://github.com/langchain-ai/langgraph",
                toolOutput = "Successfully extracted README architecture diagrams and core abstractions.",
                thinking = "Extracted StateGraph, Command, and MemorySaver patterns."
            ),
            Step(
                id = "step-4",
                taskId = sampleId,
                stepIndex = 4,
                title = "Synthesize Markdown comparison artifact",
                description = "Compiling one-page comparison table and executive brief",
                status = StepStatus.DONE,
                toolType = ToolType.FILE_WRITER,
                toolInput = "write_file: agent-frameworks-comparison.md",
                toolOutput = "Artifact agent-frameworks-comparison.md created (1,420 words).",
                thinking = "Generated comparative markdown document with pros, cons and recommended decision matrix."
            )
        )
        _steps.value = mapOf(sampleId to sampleSteps)

        val sampleArtifact = Artifact(
            id = "art-001",
            taskId = sampleId,
            title = "Framework Comparison Report",
            type = ArtifactType.MARKDOWN,
            fileName = "agent-frameworks-comparison.md",
            language = "markdown",
            content = """# Three Popular Open-Source AI Agent Frameworks (2026)

**Ecosystem Snapshot:** GitHub stars on core repositories reflect adoption:
- **MetaGPT**: 71.4k stars (Role-based software simulation)
- **CrewAI**: 61.1k stars (Collaborative crews + deterministic flows)
- **LangGraph**: 45.1k stars (Stateful computational graphs with human-in-the-loop)

## 1. MetaGPT — Software Company in a Box
MetaGPT simulates multi-agent collaboration modelled after a modern tech enterprise (Product Manager, Architect, Project Manager, Engineer).
- **Key strength**: Pre-defined Standard Operating Procedures (SOPs).
- **Best for**: Rapid prototyping of software requirements and code scaffolds.

## 2. CrewAI — Role-Playing & Flow Control
CrewAI pairs autonomous role-playing agents with structured state flows.
- **Key strength**: Easy declarative setup, tool delegation, and hierarchical task passing.
- **Best for**: Business process automation, marketing research, and team simulations.

## 3. LangGraph — Granular State Machines
LangGraph models execution as a cyclic directed graph of shared state.
- **Key strength**: Absolute control over transitions, persistence, rewind/replay, and human-in-the-loop approvals.
- **Best for**: Production enterprise agent systems requiring guaranteed determinism.
"""
        )
        _artifacts.value = mapOf(sampleId to listOf(sampleArtifact))

        _messages.value = mapOf(
            sampleId to listOf(
                ChatMessage("m1", sampleId, "user", "Pick three widely used open-source agent frameworks and give me a one-page Markdown comparison."),
                ChatMessage("m2", sampleId, "assistant", "I have surveyed the open-source agent landscape, analyzed GitHub metrics for MetaGPT, CrewAI and LangGraph, and compiled an in-depth comparison artifact for you.")
            )
        )
    }

    companion object {
        val defaultSkills = listOf(
            Skill("s1", "Browser Automation", "Autonomous navigation, DOM parsing, and interactive forms", "Web", true, "🌐"),
            Skill("s2", "Code Interpreter", "Sandboxed execution for Python, Bash, Node.js and Kotlin", "Runtime", true, "💻"),
            Skill("s3", "Web Research", "Deep multi-engine search across live web sources", "Intelligence", true, "🔍"),
            Skill("s4", "Document Compiler", "Markdown, HTML5, LaTeX and PDF artifact publishing", "Artifacts", true, "📄"),
            Skill("s5", "MCP Connectors", "Model Context Protocol bridge for external data stores", "Integration", true, "🔌")
        )
    }
}
