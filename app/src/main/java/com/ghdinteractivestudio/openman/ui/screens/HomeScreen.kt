package com.ghdinteractivestudio.openman.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ghdinteractivestudio.openman.data.model.Task
import com.ghdinteractivestudio.openman.data.model.TaskMode
import com.ghdinteractivestudio.openman.data.model.TaskStatus
import com.ghdinteractivestudio.openman.data.repository.AgentEngine
import com.ghdinteractivestudio.openman.data.repository.SettingsRepository
import com.ghdinteractivestudio.openman.data.repository.TaskRepository
import com.ghdinteractivestudio.openman.ui.components.*
import com.ghdinteractivestudio.openman.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    taskRepository: TaskRepository,
    settingsRepository: SettingsRepository,
    agentEngine: AgentEngine,
    onNavigateToTask: (String) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenSkills: () -> Unit
) {
    val tasks by taskRepository.tasks.collectAsState()
    val activeModel by settingsRepository.selectedModel.collectAsState()
    val scope = rememberCoroutineScope()

    var promptText by remember { mutableStateOf("") }
    var selectedMode by remember { mutableStateOf(TaskMode.AGENT) }

    val quickPrompts = listOf(
        "Build a responsive landing page with dark theme",
        "Deep research report on modern AI agent architectures",
        "Python script to scrape and parse financial reports",
        "Analyze GitHub repo structure and summarize dependencies"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBase)
            .statusBarsPadding()
    ) {
        BrandHeader(
            activeModel = activeModel,
            onOpenSettings = onOpenSettings,
            onOpenSkills = onOpenSkills
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mode selector capsule
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF131824))
                        .border(1.dp, GlassCardBorder, RoundedCornerShape(16.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ModeCapsuleItem(
                        title = "Agent",
                        subtitle = "Autonomous",
                        icon = Icons.Default.AutoAwesome,
                        isSelected = selectedMode == TaskMode.AGENT,
                        onClick = { selectedMode = TaskMode.AGENT },
                        modifier = Modifier.weight(1f)
                    )
                    ModeCapsuleItem(
                        title = "Chat",
                        subtitle = "Interactive",
                        icon = Icons.Default.ChatBubbleOutline,
                        isSelected = selectedMode == TaskMode.CHAT,
                        onClick = { selectedMode = TaskMode.CHAT },
                        modifier = Modifier.weight(1f)
                    )
                    ModeCapsuleItem(
                        title = "Plan",
                        subtitle = "Roadmap",
                        icon = Icons.Default.Map,
                        isSelected = selectedMode == TaskMode.PLAN,
                        onClick = { selectedMode = TaskMode.PLAN },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Task Composer Glass Card
            item {
                LiquidGlassCard {
                    Text(
                        text = "Assign a task to ${when (selectedMode) {
                            TaskMode.AGENT -> "Agent (Manus Core)"
                            TaskMode.CHAT -> "Chat Model"
                            TaskMode.PLAN -> "Strategic Planner"
                        }}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = AccentCyan
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0D111A))
                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        if (promptText.isEmpty()) {
                            Text(
                                text = "What would you like OpenMan to accomplish today? (e.g. build a website, research a topic, write code...)",
                                color = TextMuted,
                                fontSize = 14.sp
                            )
                        }
                        BasicTextField(
                            value = promptText,
                            onValueChange = { promptText = it },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(
                                color = TextPrimary,
                                fontSize = 14.sp
                            ),
                            cursorBrush = SolidColor(AccentCyan)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Model: $activeModel (FreeLLMAPI)",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            fontFamily = FontFamily.Monospace
                        )

                        LiquidGlassButton(
                            text = "Launch",
                            enabled = promptText.isNotBlank(),
                            accentColor = AccentCyan,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Run",
                                    tint = if (promptText.isNotBlank()) TextPrimary else TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            onClick = {
                                if (promptText.isNotBlank()) {
                                    val task = taskRepository.createTask(
                                        prompt = promptText.trim(),
                                        mode = selectedMode,
                                        model = activeModel
                                    )
                                    promptText = ""
                                    onNavigateToTask(task.id)
                                    scope.launch {
                                        agentEngine.executeTask(task)
                                    }
                                }
                            }
                        )
                    }
                }
            }

            // Quick Prompt Ideas
            item {
                Column {
                    Text(
                        text = "Suggested Tasks",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(quickPrompts) { prompt ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF141926))
                                    .border(1.dp, GlassCardBorder, RoundedCornerShape(12.dp))
                                    .clickable { promptText = prompt }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = prompt,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            // Tasks History Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Workspaces",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${tasks.size} tasks",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Tasks List
            items(tasks) { task ->
                TaskItemCard(
                    task = task,
                    onClick = { onNavigateToTask(task.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun ModeCapsuleItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgBrush = if (isSelected) {
        Brush.verticalGradient(
            colors = listOf(
                AccentElectricBlue.copy(alpha = 0.35f),
                AccentCyan.copy(alpha = 0.15f)
            )
        )
    } else {
        SolidColor(Color.Transparent)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgBrush)
            .border(
                if (isSelected) 1.dp else 0.dp,
                if (isSelected) AccentCyan.copy(alpha = 0.5f) else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) AccentCyan else TextMuted,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) TextPrimary else TextSecondary
                )
                Text(
                    text = subtitle,
                    fontSize = 9.sp,
                    color = if (isSelected) AccentCyan else TextMuted
                )
            }
        }
    }
}

@Composable
fun TaskItemCard(
    task: Task,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF131824))
            .border(1.dp, GlassCardBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status pill
                val (statusColor, statusText) = when (task.status) {
                    TaskStatus.COMPLETED -> AccentEmerald to "COMPLETED"
                    TaskStatus.RUNNING -> AccentCyan to "RUNNING"
                    TaskStatus.PLANNING -> AccentAmber to "PLANNING"
                    TaskStatus.WAITING_APPROVAL -> AccentPurple to "APPROVAL REQUIRED"
                    TaskStatus.FAILED -> AccentRose to "FAILED"
                    TaskStatus.PENDING -> TextSecondary to "QUEUED"
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (task.status == TaskStatus.RUNNING || task.status == TaskStatus.PLANNING) {
                        GlowingPulseIndicator(size = 8.dp, color = statusColor)
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(statusColor.copy(alpha = 0.15f))
                            .border(0.6.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = statusText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Text(
                    text = task.modelUsed,
                    fontSize = 11.sp,
                    color = TextMuted,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = task.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = task.prompt,
                fontSize = 12.sp,
                color = TextSecondary,
                maxLines = 2
            )
        }
    }
}
