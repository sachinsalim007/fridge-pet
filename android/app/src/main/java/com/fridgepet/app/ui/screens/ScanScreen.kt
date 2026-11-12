package com.fridgepet.app.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.fridgepet.app.ui.viewmodel.FridgeUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File

@Composable
fun ScanScreen(
    state: FridgeUiState,
    onAnalyzeImage: (imageBase64: String, mimeType: String, scanMode: String) -> Unit,
    onRunSampleScan: (scanMode: String) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var scanMode by remember { mutableStateOf("receipt") }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        hasCameraPermission = granted
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val base64 = encodeUriToJpegBase64(context, uri)
                if (base64 != null) {
                    onAnalyzeImage(base64, "image/jpeg", scanMode)
                }
            }
        }
    }

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Scan Groceries",
            style = MaterialTheme.typography.headlineMedium,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = scanMode == "receipt",
                onClick = { scanMode = "receipt" },
                label = { Text("Receipt (AR / EN)") },
            )
            FilterChip(
                selected = scanMode == "fridge",
                onClick = { scanMode = "fridge" },
                label = { Text("Fridge Photo") },
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(Color(0xFF141D1A)),
            contentAlignment = Alignment.Center,
        ) {
            if (hasCameraPermission) {
                CameraPreviewWithGuide(
                    scanMode = scanMode,
                    onImageCaptureReady = { imageCapture = it },
                )
            } else {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Camera permission lets you snap UAE receipts or open fridge shelves.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                    )
                    Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                        Text("Enable Camera")
                    }
                }
            }

            if (state.isScanning) {
                Surface(
                    color = Color.Black.copy(alpha = 0.72f),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        CircularProgressIndicator(color = Color.White)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Gemini is reading items & shelf life...",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Button(
                enabled = hasCameraPermission && !state.isScanning && imageCapture != null,
                onClick = {
                    val capture = imageCapture ?: return@Button
                    val photoFile = File(context.cacheDir, "scan_${System.currentTimeMillis()}.jpg")
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                    capture.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                scope.launch {
                                    val base64 = encodeFileToJpegBase64(photoFile)
                                    photoFile.delete()
                                    if (base64 != null) {
                                        onAnalyzeImage(base64, "image/jpeg", scanMode)
                                    }
                                }
                            }

                            override fun onError(exception: ImageCaptureException) {
                                photoFile.delete()
                            }
                        },
                    )
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
            ) {
                Icon(Icons.Rounded.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.size(6.dp))
                Text("Capture")
            }

            OutlinedButton(
                enabled = !state.isScanning,
                onClick = { galleryLauncher.launch("image/*") },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
            ) {
                Icon(Icons.Rounded.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.size(6.dp))
                Text("Gallery")
            }
        }

        OutlinedButton(
            enabled = !state.isScanning,
            onClick = { onRunSampleScan(scanMode) },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(Icons.Rounded.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.size(6.dp))
            Text(
                if (scanMode == "receipt") "Try Sample UAE Bilingual Receipt" else "Try Sample Fridge Photo"
            )
        }
    }
}

@Composable
private fun CameraPreviewWithGuide(
    scanMode: String,
    onImageCaptureReady: (ImageCapture) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }
                    val capture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()
                    onImageCaptureReady(capture)
                    runCatching {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            capture,
                        )
                    }
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            },
        )

        // Guide frame brackets
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val left = if (scanMode == "receipt") w * 0.14f else w * 0.08f
            val right = w - left
            val top = h * 0.10f
            val bottom = h * 0.90f
            val corner = 38f
            val stroke = 6f
            val guideColor = Color(0xFFA7F3D0)

            // Top-left
            drawLine(guideColor, Offset(left, top), Offset(left + corner, top), stroke, StrokeCap.Round)
            drawLine(guideColor, Offset(left, top), Offset(left, top + corner), stroke, StrokeCap.Round)
            // Top-right
            drawLine(guideColor, Offset(right, top), Offset(right - corner, top), stroke, StrokeCap.Round)
            drawLine(guideColor, Offset(right, top), Offset(right, top + corner), stroke, StrokeCap.Round)
            // Bottom-left
            drawLine(guideColor, Offset(left, bottom), Offset(left + corner, bottom), stroke, StrokeCap.Round)
            drawLine(guideColor, Offset(left, bottom), Offset(left, bottom - corner), stroke, StrokeCap.Round)
            // Bottom-right
            drawLine(guideColor, Offset(right, bottom), Offset(right - corner, bottom), stroke, StrokeCap.Round)
            drawLine(guideColor, Offset(right, bottom), Offset(right, bottom - corner), stroke, StrokeCap.Round)
        }

        Surface(
            color = Color.Black.copy(alpha = 0.55f),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 14.dp),
        ) {
            Text(
                text = if (scanMode == "receipt") {
                    "Align Arabic / English receipt inside frame"
                } else {
                    "Center open fridge shelf inside frame"
                },
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            )
        }
    }
}

private suspend fun encodeUriToJpegBase64(context: Context, uri: Uri): String? =
    withContext(Dispatchers.IO) {
        runCatching {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val bmp = BitmapFactory.decodeStream(stream) ?: return@withContext null
                compressBitmapToBase64(bmp)
            }
        }.getOrNull()
    }

private suspend fun encodeFileToJpegBase64(file: File): String? =
    withContext(Dispatchers.IO) {
        runCatching {
            val bmp = BitmapFactory.decodeFile(file.absolutePath) ?: return@withContext null
            compressBitmapToBase64(bmp)
        }.getOrNull()
    }

private fun compressBitmapToBase64(bitmap: Bitmap): String {
    val maxDim = 1280
    val scale = minOf(1f, maxDim.toFloat() / maxOf(bitmap.width, bitmap.height))
    val resized = if (scale < 1f) {
        Bitmap.createScaledBitmap(
            bitmap,
            (bitmap.width * scale).toInt().coerceAtLeast(1),
            (bitmap.height * scale).toInt().coerceAtLeast(1),
            true,
        )
    } else {
        bitmap
    }
    val out = ByteArrayOutputStream()
    resized.compress(Bitmap.CompressFormat.JPEG, 82, out)
    return Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP)
}
