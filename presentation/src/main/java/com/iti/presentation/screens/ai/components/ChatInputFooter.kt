package com.iti.presentation.screens.ai.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.stringResource
import com.iti.presentation.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

enum class RecordingState {
    IDLE,
    RECORDING
}

@Composable
fun ChatInputFooter(
    isDark: Boolean,
    onAttachmentClick: () -> Unit,
    onSendMessage: (String) -> Unit,
    onSendVoiceMessage: (String, String) -> Unit
) {
    val context = LocalContext.current
    val containerBg = if (isDark) Color(0xFF1E242B) else Color(0xFFF9FAFB)
    val borderColor = if (isDark) Color(0xFF2E3844) else Color(0xFFE5E7EB)
    
    var inputText by remember { mutableStateOf("") }
    var recordingState by remember { mutableStateOf(RecordingState.IDLE) }
    var speechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }

    // Launcher for requesting record audio permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Start listening directly or wait for next click
        } else {
            Toast.makeText(context, context.getString(R.string.ai_permission_record_audio_required), Toast.LENGTH_SHORT).show()
        }
    }

    fun startListening() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        
        if (hasPermission) {
            recordingState = RecordingState.RECORDING
            val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer = recognizer
            
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                // Set primary language to Arabic as requested, and fallback to default or English
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-EG")
                putExtra(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES, arrayOf("ar-EG", "en-US"))
            }
            
            recognizer.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {
                    recordingState = RecordingState.IDLE
                }
                override fun onError(error: Int) {
                    recordingState = RecordingState.IDLE
                    recognizer.destroy()
                }
                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val text = matches?.firstOrNull() ?: ""
                    if (text.isNotBlank()) {
                        onSendVoiceMessage(text, "0:05")
                    }
                    recognizer.destroy()
                }
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
            
            recognizer.startListening(intent)
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        speechRecognizer?.destroy()
        speechRecognizer = null
        recordingState = RecordingState.IDLE
    }

    // Auto-clean up speech recognizer on dispose
    DisposableEffect(Unit) {
        onDispose {
            speechRecognizer?.destroy()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HorizontalDivider(color = borderColor)
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, borderColor, RoundedCornerShape(24.dp))
                    .background(containerBg, RoundedCornerShape(24.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                TextField(
                    value = inputText,
                    onValueChange = { newValue ->
                        if (newValue.length <= 500 && newValue.count { it == '\n' } <= 4) {
                            inputText = newValue
                        }
                    },
                    placeholder = {
                        Text(
                            text = if (recordingState == RecordingState.RECORDING) {
                                stringResource(id = R.string.ai_input_listening)
                            } else {
                                stringResource(id = R.string.ai_input_placeholder)
                            },
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    maxLines = 4,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 14.sp
                    )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onAttachmentClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "Add photo or attachment",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Animating wave mic button for recording state
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    if (recordingState == RecordingState.RECORDING) Color(0xFF6F32E5) else Color(0xFFE8DDFF),
                                    shape = CircleShape
                                )
                                .clickable {
                                    if (recordingState == RecordingState.RECORDING) {
                                        stopListening()
                                    } else {
                                        startListening()
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (recordingState == RecordingState.RECORDING) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val transition = rememberInfiniteTransition(label = "mic_waves")
                                    val heights = listOf(6, 12, 16, 12, 6)
                                    heights.forEachIndexed { idx, h ->
                                        val animScale by transition.animateFloat(
                                            initialValue = 0.4f,
                                            targetValue = 1.0f,
                                            animationSpec = infiniteRepeatable(
                                                animation = tween(300 + idx * 80, easing = LinearEasing),
                                                repeatMode = RepeatMode.Reverse
                                            ),
                                            label = "wave_$idx"
                                        )
                                        Box(
                                            modifier = Modifier
                                                .width(2.dp)
                                                .height((h * animScale).dp)
                                                .background(Color.White, shape = CircleShape)
                                        )
                                    }
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Voice search",
                                    tint = Color(0xFF6F32E5),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        
                        IconButton(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    onSendMessage(inputText.trim())
                                    inputText = ""
                                }
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    if (inputText.isNotBlank()) Color(0xFF6F32E5) else Color.Transparent,
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send message",
                                tint = if (inputText.isNotBlank()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun ChatInputFooterPreview() {
    MaterialTheme {
        ChatInputFooter(
            isDark = false,
            onAttachmentClick = {},
            onSendMessage = {},
            onSendVoiceMessage = { _, _ -> }
        )
    }
}
