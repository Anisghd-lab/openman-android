package com.ghdinteractivestudio.openman.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ghdinteractivestudio.openman.data.api.FreeLlmClientFactory
import com.ghdinteractivestudio.openman.data.repository.SettingsRepository
import com.ghdinteractivestudio.openman.ui.components.GlowingPulseIndicator
import com.ghdinteractivestudio.openman.ui.components.LiquidGlassButton
import com.ghdinteractivestudio.openman.ui.components.LiquidGlassCard
import com.ghdinteractivestudio.openman.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    settingsRepository: SettingsRepository,
    onBack: () -> Unit
) {
    val baseUrl by settingsRepository.baseUrl.collectAsState()
    val apiKey by settingsRepository.apiKey.collectAsState()
    val selectedModel by settingsRepository.selectedModel.collectAsState()

    var testStatus by remember { mutableStateOf<String?>(null) }
    var isTesting by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val availableModels = listOf(
        "auto",
        "fusion",
        "gemini-3.8-flash",
        "glm-5.3-flash",
        "claude-opus-4-5",
        "claude-sonnet-4-5",
        "claude-haiku-4-5"
    )

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
            Text(
                text = "FreeLLMAPI & Agent Settings",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // FreeLLMAPI Configuration Card
            item {
                LiquidGlassCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "FreeLLMAPI Endpoint",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan
                        )
                        GlowingPulseIndicator(size = 8.dp, color = AccentEmerald)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Base URL",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0D111A))
                            .border(1.dp, GlassCardBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        BasicTextField(
                            value = baseUrl,
                            onValueChange = { settingsRepository.updateBaseUrl(it) },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            cursorBrush = SolidColor(AccentCyan)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Preset chips
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        PresetChip("Emulator (10.0.2.2)") {
                            settingsRepository.updateBaseUrl("http://10.0.2.2:3001")
                        }
                        PresetChip("Localhost (127.0.0.1)") {
                            settingsRepository.updateBaseUrl("http://127.0.0.1:3001")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Unified Bearer Token",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0D111A))
                            .border(1.dp, GlassCardBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        BasicTextField(
                            value = apiKey,
                            onValueChange = { settingsRepository.updateApiKey(it) },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            cursorBrush = SolidColor(AccentCyan)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Test connection button
                    LiquidGlassButton(
                        text = if (isTesting) "Pinging FreeLLMAPI..." else "Test Connection",
                        enabled = !isTesting,
                        accentColor = AccentElectricBlue,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Test",
                                tint = AccentCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        onClick = {
                            isTesting = true
                            testStatus = null
                            scope.launch {
                                try {
                                    val api = FreeLlmClientFactory.create(baseUrl, apiKey)
                                    val response = api.getModels()
                                    if (response.isSuccessful && response.body() != null) {
                                        val count = response.body()?.data?.size ?: 0
                                        testStatus = "[SUCCESS] Connected to FreeLLMAPI! ($count models ready)"
                                    } else {
                                        testStatus = "[ERROR] FreeLLMAPI returned HTTP ${response.code()}"
                                    }
                                } catch (e: Exception) {
                                    testStatus = "[FALLBACK] Server unreachable (${e.message}). Autonomous fallback simulation active."
                                } finally {
                                    isTesting = false
                                }
                            }
                        }
                    )

                    if (testStatus != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0B0F17))
                                .border(
                                    0.8.dp,
                                    if (testStatus!!.startsWith("[SUCCESS]")) AccentEmerald.copy(alpha = 0.5f) else AccentAmber.copy(alpha = 0.5f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(10.dp)
                        ) {
                            Text(
                                text = testStatus!!,
                                fontSize = 11.sp,
                                color = if (testStatus!!.startsWith("[SUCCESS]")) AccentEmerald else AccentAmber,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Model Selection
            item {
                LiquidGlassCard {
                    Text(
                        text = "Active LLM Routing Model",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan
                    )
                    Text(
                        text = "Select which model powers OpenMan autonomous reasoning.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        availableModels.forEach { model ->
                            val isSelected = model == selectedModel
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) AccentElectricBlue.copy(alpha = 0.2f) else Color(0xFF0E131F)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) AccentCyan.copy(alpha = 0.5f) else GlassCardBorder,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { settingsRepository.updateSelectedModel(model) }
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = model,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) TextPrimary else TextSecondary,
                                        fontFamily = FontFamily.Monospace
                                    )

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = AccentCyan,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun PresetChip(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF1A2234))
            .border(0.8.dp, GlassCardBorder, RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            color = AccentCyan,
            fontFamily = FontFamily.Monospace
        )
    }
}
