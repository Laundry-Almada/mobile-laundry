package com.almalaundry.featured.order.presentation.components

import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

@Composable
fun BarcodeScanner(
    onBarcodeDetected: (String) -> Unit,
    onError: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }

            cameraProviderFuture.addListener({
                try {
                    val cameraProvider = cameraProviderFuture.get()

                    // Camera configuration
                    val resolutionSelector = ResolutionSelector.Builder()
                        .setResolutionFilter { supportedSizes, _ ->
                            supportedSizes.filter { size ->
                                // Filter resolusi dengan rasio aspek ~16:9 (1.777...)
                                val aspectRatio = size.width.toFloat() / size.height
                                (aspectRatio in 1.7f..1.8f) &&
                                        size.width <= 1280 && size.height <= 720
                            }.sortedBy { size ->
                                // Prioritaskan resolusi mendekati 1280x720
                                (1280 - size.width) + (720 - size.height)
                            }
                        }
                        .build()

                    val preview = Preview.Builder()
                        .setResolutionSelector(resolutionSelector)
                        .build()

                    val cameraSelector = CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setResolutionSelector(resolutionSelector)
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .apply {
                            setAnalyzer(
                                ContextCompat.getMainExecutor(ctx),
                                BarcodeAnalyzer(
                                    onSuccess = onBarcodeDetected,
                                    onError = { onError("Barcode detection failed") }
                                )
                            )
                        }

                    // Bind use cases
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                    preview.surfaceProvider = previewView.surfaceProvider
                } catch (e: Exception) {
                    onError("Camera initialization failed: ${e.localizedMessage}")
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        }
    )
}

class BarcodeAnalyzer(
    private val onSuccess: (String) -> Unit,
    private val onError: () -> Unit
) : ImageAnalysis.Analyzer {
    private val scanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()
    )

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }

        val image = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                barcodes.firstOrNull()?.rawValue?.let { value ->
                    onSuccess(value)
                }
            }
            .addOnFailureListener {
                onError()
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}