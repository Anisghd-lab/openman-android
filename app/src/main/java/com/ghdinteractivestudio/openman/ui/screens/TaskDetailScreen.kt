package com.ghdinteractivestudio.openman.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ghdinteractivestudio.openman.data.model.*
import com.ghdinteractivestudio.openman.data.repository.AgentEngine
import com.ghdinteractivestudio.openman.data.repository.TaskRepository
import com.ghdinteractivestudio.openman.ui.components.*
import com.ghdinteractivestudio.openman.ui.theme.*
import kotlinx.coroutines.launch

enum class DetailTab {
    WORK_LOG,
    COMPUTER,
    ARTIFACTS
}

@Composable
fun TaskDetailScreen(
    taskId: String,
    taskRepository: TaskRepository,
    agentEngine: AgentEngine,
    onBack: () -> Unit
) {
    val tasks by taskRepository.tasks.collectAsState()
    val allSteps by taskRepository.steps.collectAsState()
    val allArtifacts by taskRepository.artifacts.collectAsState()
    val allMessages by taskRepository.messages.collectAsState()

    val task = tasks.find { it.id == taskId } ?: return
    val steps = allSteps[taskId] ?: emptyList()
    val artifacts = allArtifacts[taskId] ?: emptyList()
    val messages = allMessages[taskId] ?: emptyList()

    var selectedTab by remember { mutableStateOf(DetailTab.WORK_LOG) }
    var followUpText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBase)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val statusColor = when (task.status) {
                        TaskStatus.COMPLETED -> AccentEmerald
                        TaskStatus.RUNNING -> AccentCyan
                        TaskStatus.PLANNING -> AccentAmber
                        else -> TextSecondary
                    }
                    if (task.status == TaskStatus.RUNNING || task.status == TaskStatus.PLANNING) {
                        GlowingPulseIndicator(size = 6.dp, color = statusColor)
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = task.status.name,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = " • ${task.modelUsed}",
                        fontSize = 10.sp,
                        color = TextMuted,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Tab Selector (Work Log, Computer, Artifacts)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF131824))
                .border(1.dp, GlassCardBorder, RoundedCornerShape(12.dp))
                .padding(3.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DetailTabItem(
                title = "Work Log",
                icon = Icons.Default.ListAlt,
                isSelected = selectedTab == DetailTab.WORK_LOG,
                badgeCount = steps.size,
                onClick = { selectedTab = DetailTab.WORK_LOG },
                modifier = Modifier.weight(1f)
            )
            DetailTabItem(
                title = "Computer",
                icon = Icons.Default.DesktopWindows,
                isSelected = selectedTab == DetailTab.COMPUTER,
                badgeCount = if (task.liveUrl != null) 1 else null,
                onClick = { selectedTab = DetailTab.COMPUTER },
                modifier = Modifier.weight(1f)
            )
            DetailTabItem(
                title = "Artifacts",
                icon = Icons.Default.Description,
                isSelected = selectedTab == DetailTab.ARTIFACTS,
                badgeCount = artifacts.size,
                onClick = { selectedTab = DetailTab.ARTIFACTS },
                modifier = Modifier.weight(1f)
            )
        }

        // Main Tab Content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            when (selectedTab) {
                DetailTab.WORK_LOG -> WorkLogTabContent(task, steps, messages)
                DetailTab.COMPUTER -> ComputerTabContent(task, steps)
                DetailTab.ARTIFACTS -> ArtifactsTabContent(artifacts, context)
            }
        }

        // Bottom Follow-up composer bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF131824))
                .border(1.dp, GlassCardBorder, RoundedCornerShape(16.dp))
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                ) {
                    if (followUpText.isEmpty()) {
                        Text(
                            text = "Send follow-up instruction to OpenMan...",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    }
                    BasicTextField(
                        value = followUpText,
                        onValueChange = { followUpText = it },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = TextPrimary, fontSize = 13.sp),
                        cursorBrush = SolidColor(AccentCyan)
                    )
                }

                IconButton(
                    onClick = {
                        if (followUpText.isNotBlank()) {
                            val text = followUpText.trim()
                            followUpText = ""
                            taskRepository.addMessage(task.id, "user", text)
                            scope.launch {
                                agentEngine.executeTask(task.copy(prompt = text))
                            }
                        }
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(AccentElectricBlue)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = TextPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DetailTabItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    badgeCount: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) AccentElectricBlue.copy(alpha = 0.25f) else Color.Transparent)
            .border(
                if (isSelected) 1.dp else 0.dp,
                if (isSelected) AccentCyan.copy(alpha = 0.4f) else Color.Transparent,
                RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) AccentCyan else TextSecondary,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) TextPrimary else TextSecondary
            )
            if (badgeCount != null && badgeCount > 0) {
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isSelected) AccentCyan else Color(0xFF263045))
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = badgeCount.toString(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) TextDark else TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun WorkLogTabContent(
    task: Task,
    steps: List<Step>,
    messages: List<ChatMessage>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            LiquidGlassCard {
                Text(
                    text = "Goal Statement",
                    fontSize = 12.sp,
                    color = AccentCyan,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.prompt,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
            }
        }

        if (steps.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        GlowingPulseIndicator(size = 14.dp, color = AccentCyan)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Orchestrating agent plan...",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        items(steps) { step ->
            StepCardItem(step = step)
        }

        if (task.summary != null) {
            item {
                LiquidGlassCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Done",
                            tint = AccentEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Executive Summary",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentEmerald
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = task.summary,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun StepCardItem(step: Step) {
    var expanded by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF131824))
            .border(1.dp, GlassCardBorder, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    when (step.status) {
                        StepStatus.DONE -> Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Done",
                            tint = AccentEmerald,
                            modifier = Modifier.size(16.dp)
                        )
                        StepStatus.ACTIVE -> GlowingPulseIndicator(size = 8.dp, color = AccentCyan)
                        StepStatus.ERROR -> Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            tint = AccentRose,
                            modifier = Modifier.size(16.dp)
                        )
                        StepStatus.PENDING -> Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .border(1.dp, TextMuted, CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "${step.stepIndex}. ${step.title}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }

                // Tool badge
                val (toolLabel, toolColor) = when (step.toolType) {
                    ToolType.BROWSER -> "🌐 Browser" to AccentCyan
                    ToolType.TERMINAL -> "💻 Terminal" to AccentElectricBlue
                    ToolType.SEARCH -> "🔍 Search" to AccentAmber
                    ToolType.FILE_WRITER -> "📄 File" to AccentEmerald
                    ToolType.PLANNER -> "🗺️ Plan" to AccentPurple
                    ToolType.REASONING -> "🧠 Think" to TextSecondary
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(toolColor.copy(alpha = 0.15f))
                        .border(0.6.dp, toolColor.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .clickable { expanded = !expanded }
                ) {
                    Text(
                        text = toolLabel,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = toolColor,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    if (!step.thinking.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0D111A))
                                .padding(8.dp)
                        ) {
                            Row {
                                Text(
                                    text = "Thinking: ",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentCyan,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = step.thinking,
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    if (!step.toolOutput.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0A0D14))
                                .border(0.5.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = step.toolOutput,
                                fontSize = 11.sp,
                                color = TextPrimary,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ComputerTabContent(
    task: Task,
    steps: List<Step>
) {
    val currentUrl = task.liveUrl ?: "https://openman.local/desktop"
    val browserStep = steps.findLast { it.toolType == ToolType.BROWSER }
    val terminalStep = steps.findLast { it.toolType == ToolType.TERMINAL }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Simulated Browser Card
            LiquidGlassCard {
                // Browser URL bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0D111A))
                        .border(1.dp, GlassCardBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "SSL",
                        tint = AccentEmerald,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = currentUrl,
                        fontSize = 11.sp,
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Viewport Preview Frame
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF080B10))
                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "RENDERED DOM VIEWPORT",
                                fontSize = 9.sp,
                                color = AccentCyan,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "1920x1080 @ 60fps",
                                fontSize = 9.sp,
                                color = TextMuted,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = browserStep?.toolOutput ?: "Autonomous browser ready. Inspecting elements, capturing snapshots, and interacting with forms on user command.",
                            fontSize = 12.sp,
                            color = TextPrimary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        item {
            // Simulated Terminal Shell Card
            LiquidGlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AccentRose))
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AccentAmber))
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AccentEmerald))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "openman-sandbox@root:~",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Text(
                        text = "BASH 5.2",
                        fontSize = 10.sp,
                        color = AccentElectricBlue,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF06080D))
                        .padding(10.dp)
                ) {
                    Text(
                        text = """$ ${terminalStep?.toolInput ?: "python3 -m openman.agent --status"}
${terminalStep?.toolOutput ?: "[OK] Sandbox online. 4 execution worker threads ready."}
$ echo 'Execution pipeline healthy.'
Execution pipeline healthy.
""",
                        fontSize = 11.sp,
                        color = Color(0xFF67E8F9),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
fun ArtifactsTabContent(
    artifacts: List<Artifact>,
    context: Context
) {
    if (artifacts.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No artifacts generated yet. When the agent compiles code, reports, or websites, they will appear here.",
                fontSize = 13.sp,
                color = TextSecondary
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(artifacts) { artifact ->
                ArtifactCardItem(artifact = artifact, context = context)
            }
        }
    }
}

@Composable
fun ArtifactCardItem(artifact: Artifact, context: Context) {
    LiquidGlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = when (artifact.type) {
                        ArtifactType.MARKDOWN -> Icons.Default.Description
                        ArtifactType.CODE -> Icons.Default.Code
                        ArtifactType.WEBSITE_HTML -> Icons.Default.Language
                        ArtifactType.JSON_DATA -> Icons.Default.DataObject
                    },
                    contentDescription = "Artifact",
                    tint = AccentCyan,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = artifact.fileName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = artifact.type.name,
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
            }

            IconButton(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText(artifact.fileName, artifact.content))
                    Toast.makeText(context, "Copied ${artifact.fileName} to clipboard", Toast.LENGTH_SHORT).show()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy",
                    tint = AccentCyan,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF090D15))
                .border(0.8.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            Text(
                text = artifact.content,
                fontSize = 11.sp,
                color = TextPrimary,
                fontFamily = FontFamily.Monospace,
                lineHeight = 16.sp
            )
        }
    }
}
