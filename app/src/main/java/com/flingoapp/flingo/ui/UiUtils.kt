package com.flingoapp.flingo.ui

import android.graphics.BlurMaskFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.Log
import androidx.annotation.RawRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isUnspecified
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.compose.LocalLifecycleOwner
import app.rive.runtime.kotlin.RiveAnimationView
import app.rive.runtime.kotlin.controllers.RiveFileController
import app.rive.runtime.kotlin.core.Alignment
import app.rive.runtime.kotlin.core.Fit
import app.rive.runtime.kotlin.core.Loop
import app.rive.runtime.kotlin.core.PlayableInstance
import com.flingoapp.flingo.ui.theme.FlingoColors

/**
 * Custom preview to show the background and change the device to being a tablet
 *
 * @constructor Create empty Custom preview
 */
@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
annotation class CustomPreview

/**
 * Helper function to convert dp to px
 *
 */
@Composable
fun Dp.toPx() = with(LocalDensity.current) { this@toPx.toPx() }

/**
 * Helper function to convert px to dp
 *
 */
@Composable
fun Int.toDp() = with(LocalDensity.current) { this@toDp.toDp() }

/**
 * Helper function to convert sp to dp
 *
 */
@Composable
fun TextUnit.toDp() = with(LocalDensity.current) { this@toDp.toDp() }

/**
 * Helper function to darken a color
 *
 * @param factor
 * @return
 */
fun Color.darken(factor: Float): Color {
    return Color(ColorUtils.blendARGB(this.toArgb(), Color.Black.toArgb(), factor))
}

/**
 * Helper function to lighten a color
 *
 * @param factor
 * @return
 */
fun Color.lighten(factor: Float): Color {
    return Color(ColorUtils.blendARGB(this.toArgb(), Color.White.toArgb(), factor))
}

/**
 * Helper function to create an inner shadow for given object
 *
 * @param shape
 * @param color
 * @param blur
 * @param offsetY
 * @param offsetX
 * @param spread
 */
fun Modifier.innerShadow(
    shape: Shape,
    color: Color,
    blur: Dp,
    offsetY: Dp,
    offsetX: Dp,
    spread: Dp
) = drawWithContent {
    drawContent()

    val rect = Rect(Offset.Zero, size)
    val paint = Paint().apply {
        this.color = color
        this.isAntiAlias = true
    }

    val shadowOutline = shape.createOutline(size, layoutDirection, this)

    drawIntoCanvas { canvas ->
        canvas.saveLayer(rect, paint)
        canvas.drawOutline(shadowOutline, paint)
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        if (blur.toPx() > 0) {
            frameworkPaint.maskFilter = BlurMaskFilter(blur.toPx(), BlurMaskFilter.Blur.NORMAL)
        }

        paint.color = Color.Black

        val spreadOffsetX =
            offsetX.toPx() + if (offsetX.toPx() < 0) -spread.toPx() else spread.toPx()
        val spreadOffsetY =
            offsetY.toPx() + if (offsetY.toPx() < 0) -spread.toPx() else spread.toPx()

        canvas.translate(spreadOffsetX, spreadOffsetY)
        canvas.drawOutline(shadowOutline, paint)
        canvas.restore()
    }
}

/**
 * Helper function to get bounding boxes of a text composable
 *
 * @param start
 * @param end
 * @return
 */
fun TextLayoutResult.getBoundingBoxesForRange(start: Int, end: Int): List<Rect> {
    var prevRect: Rect? = null
    var firstLineCharRect: Rect? = null
    val boundingBoxes = mutableListOf<Rect>()
    for (i in start..end) {
        val rect = getBoundingBox(i)
        val isLastRect = i == end

        // single char case
        if (isLastRect && firstLineCharRect == null) {
            firstLineCharRect = rect
            prevRect = rect
        }

        // `rect.right` is zero for the last space in each line
        // looks like an issue to me, reported: https://issuetracker.google.com/issues/197146630
        if (!isLastRect && rect.right == 0f) continue

        if (firstLineCharRect == null) {
            firstLineCharRect = rect
        } else if (prevRect != null) {
            if (prevRect.bottom != rect.bottom || isLastRect) {
                boundingBoxes.add(
                    firstLineCharRect.copy(right = prevRect.right)
                )
                firstLineCharRect = rect
            }
        }
        prevRect = rect
    }
    return boundingBoxes
}

/**
 * Helper function to inflate the rectangle of a given boundingBox
 *
 * @param verticalDelta
 * @param horizontalDelta
 */
fun Rect.inflate(verticalDelta: Float, horizontalDelta: Float) =
    Rect(
        left = left - horizontalDelta,
        top = top - verticalDelta,
        right = right + horizontalDelta,
        bottom = bottom + verticalDelta,
    )

/**
 * Helper function to create a custom outline, which allows changing the width of the individual corners
 *
 * @param outlineColor
 * @param surfaceColor
 * @param startOffset
 * @param outlineWidth
 * @param radius
 */
@Composable
fun Modifier.customOutline(
    outlineColor: Color,
    surfaceColor: Color,
    startOffset: Dp,
    outlineWidth: Dp,
    radius: Dp = 1.dp
) = drawBehind {
    val startOffsetPx = startOffset.toPx()
    val outlineWidthPx = outlineWidth.toPx()
    val radiusPx = radius.toPx()
    drawRoundRect(
        color = outlineColor,
        topLeft = Offset(0f, 0f),
        size = size,
        cornerRadius = CornerRadius(radiusPx, radiusPx),
        style = Fill
    )
    drawRoundRect(
        color = surfaceColor,
        topLeft = Offset(startOffsetPx, outlineWidthPx),
        size = Size(size.width - startOffsetPx - outlineWidthPx, size.height - outlineWidthPx * 2),
        cornerRadius = CornerRadius(radiusPx - outlineWidthPx, radiusPx - outlineWidthPx),
        style = Fill
    )
}

@Composable
fun Modifier.animatedBorder(
    strokeWidth: Dp,
    shape: Shape,
    colors: List<Color> = listOf(
        Color.Transparent,
        FlingoColors.Primary
    ),
    brush: (Size) -> Brush = {
        Brush.sweepGradient(colors)
    },
    wantedCycles: Int = 1, //negative wanted cycles == infinite
    durationMillis: Int
) = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val degrees by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    var cycleCompletionCount by remember {
        mutableIntStateOf(-1)
    }

    LaunchedEffect(degrees) {
        if (degrees <= 0f) cycleCompletionCount++
    }

    val isFinished by remember {
        derivedStateOf {
            if (wantedCycles <= 0) {
                false
            } else {
                cycleCompletionCount >= wantedCycles
            }
        }
    }

    Modifier
        .clip(shape)
        .drawWithCache {
            val strokeWidthPx = strokeWidth.toPx()
            val outline: Outline = shape.createOutline(size, layoutDirection, this)
            onDrawWithContent {
                drawContent()
                with(drawContext.canvas.nativeCanvas) {
                    val checkPoint = saveLayer(null, null)
                    drawOutline(
                        outline = outline,
                        color = Color.Gray,
                        style = Stroke(strokeWidthPx * 2)
                    )

                    rotate(if (isFinished) 0f else degrees) {
                        if (isFinished) {
                            drawCircle(
                                color = FlingoColors.Success,
                                radius = (size.width),
                                blendMode = androidx.compose.ui.graphics.BlendMode.SrcIn
                            )
                        } else {
                            //TODO: needs to be improved
                            drawCircle(
                                brush = brush(size),
                                radius = (size.width),
                                blendMode = androidx.compose.ui.graphics.BlendMode.SrcIn
                            )
                        }
                    }

                    restoreToCount(checkPoint)
                }
            }
        }
}

//text which automatically resizes to fit the size of container
@Composable
fun AutoResizableText(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    fontSize: TextUnit = style.fontSize,
    color: Color = style.color,
    textAlign: TextAlign = TextAlign.Start
) {
    var resizableTextStyle by remember {
        mutableStateOf(style.copy(textAlign = textAlign, fontSize = fontSize))
    }
    var shouldDraw by remember {
        mutableStateOf(false)
    }

    val defaultFontSize = MaterialTheme.typography.bodyMedium.fontSize

    Text(
        text = text,
        color = color,
        modifier = modifier.drawWithContent {
            if (shouldDraw) {
                drawContent()
            }
        },
        softWrap = false,
        style = resizableTextStyle,
        onTextLayout = { result ->
            if (result.didOverflowWidth) {
                if (fontSize.isUnspecified) {
                    resizableTextStyle = resizableTextStyle.copy(
                        fontSize = defaultFontSize
                    )
                }

                resizableTextStyle = resizableTextStyle.copy(
                    fontSize = resizableTextStyle.fontSize * 0.95
                )

                Log.e("UIUtils", "current fontsize: ${resizableTextStyle.fontSize}")
            } else {
                shouldDraw = true
            }
        }
    )
}

@Composable
fun RiveAnimation(
    modifier: Modifier = Modifier,
    @RawRes resId: Int,
    autoplay: Boolean = true,
    artboardName: String? = null,
    animationName: String? = null,
    stateMachineName: String? = null,
    fit: Fit = Fit.CONTAIN,
    alignment: Alignment = Alignment.CENTER,
    loop: Loop = Loop.AUTO,
    notifyLoop: ((PlayableInstance) -> Unit)? = null,
    notifyPause: ((PlayableInstance) -> Unit)? = null,
    notifyPlay: ((PlayableInstance) -> Unit)? = null,
    notifyStateChanged: ((String, String) -> Unit)? = null,
    notifyStop: ((PlayableInstance) -> Unit)? = null,
    update: (RiveAnimationView) -> Unit = { _ -> }
) {
    var riveAnimationView: RiveAnimationView? = null
    var listener: RiveFileController.Listener? = null
    val lifecycleOwner = LocalLifecycleOwner.current

    if (LocalInspectionMode.current) { // For Developing only,
//        Image(
//            modifier = modifier.size(100.dp),
//            painter = painterResource(id = R.drawable.rive_logo), //any image
//            contentDescription = contentDescription
//        )
    } else {
        listener = object : RiveFileController.Listener {
            override fun notifyLoop(animation: PlayableInstance) {
                notifyLoop?.invoke(animation)
            }

            override fun notifyPause(animation: PlayableInstance) {
                notifyPause?.invoke(animation)
            }

            override fun notifyPlay(animation: PlayableInstance) {
                notifyPlay?.invoke(animation)
            }

            override fun notifyStateChanged(
                stateMachineName: String,
                stateName: String
            ) {
                notifyStateChanged?.invoke(stateMachineName, stateName)
            }

            override fun notifyStop(animation: PlayableInstance) {
                notifyStop?.invoke(animation)
            }
        }.takeIf {
            (notifyLoop != null) || (notifyPause != null) ||
                    (notifyPlay != null) || (notifyStateChanged != null) ||
                    (notifyStop != null)
        }

        AndroidView(
            modifier = modifier
                .clipToBounds(),
            factory = { context ->
                riveAnimationView = RiveAnimationView(context).apply {
                    setRiveResource(
                        resId,
                        artboardName,
                        animationName,
                        stateMachineName,
                        autoplay,
                        fit,
                        alignment,
                        loop
                    )
                }
                listener?.let {
                    riveAnimationView?.registerListener(it)
                }
                riveAnimationView!!
            },
            update = {
                update.invoke(it)
            }
        )

        DisposableEffect(lifecycleOwner) {
            onDispose {
                listener?.let {
                    riveAnimationView?.unregisterListener(it)
                }
            }
        }
    }
}