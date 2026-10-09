package io.github.petvat.katan.ui.control

import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.Viewport
import kotlin.math.roundToInt

class DragCameraController(
    private val camera: OrthographicCamera,
    private val viewport: Viewport,
    private val onTap: (screenX: Int, screenY: Int) -> Unit,  // fired only on genuine taps
    private val boardExtent: Float = 300f,
    private val sensitivity: Float = 1f,
    private val minZoom: Float = 0.5f,
    private val maxZoom: Float = 3f,
    private val zoomStepRatio: Float = 1.25f,
    private val zoomSensitivity: Float = 0.1f,
    private val dragThreshold: Float = 8f  // pixels of movement before it counts as a drag
) : InputAdapter() {

    private var lastX = 0f
    private var lastY = 0f
    private var touchDownX = 0f
    private var touchDownY = 0f
    private var isDragging = false

    private var zoomSteps: FloatArray = floatArrayOf(1f)   // fallback until first onResize()
    private var stepIndex = 0
    private var targetZoom: Float = 1f

    private val worldPerScreen: Float
        get() = viewport.worldWidth / viewport.screenWidth

    /**
     * Rebuild the zoom ladder from the viewport's actual scale (world units per screen pixel).
     *
     * Safe "anchor" zooms in *effective* (texel:pixel) space:
     *  - zoom-in:  r = 1, 2, 3 ...      (integer magnification, no uneven pixels)
     *  - zoom-out: r = 1/2, 1/4, 1/8 ... (exact mip-level boundaries, no dropped texels)
     *
     * `zoomStepRatio` densifies the ladder with geometric fillers between anchors.
     * All values are divided by `s` to convert effective ratio -> camera zoom.
     * Call on resize (never at construction — viewport has no dimensions yet).
     */
    fun onResize() {
        val screenW = viewport.screenWidth
        if (screenW <= 0) return            // pre-resize / minimized; keep fallback ladder
        val s = viewport.worldWidth / screenW
        if (!s.isFinite() || s <= 0f) return

        val steps = mutableListOf<Float>()

        // --- Zoom-in: effective ratios 1 .. maxZoom, geometric with zoomStepRatio ---
        var r = 1f
        while (r <= maxZoom) {
            steps.add(r / s)
            r *= zoomStepRatio
        }

        // --- Zoom-out: exact mip anchors (1/2, 1/4, ... down to minZoom) ---
        var anchor = 0.5f
        while (anchor >= minZoom * s) {
            steps.add(anchor / s)
            anchor /= 2f
        }

        // --- Zoom-out fillers: one geometric step between consecutive mip anchors ---
        if (zoomStepRatio < 2f) {
            anchor = 0.5f
            while (true) {
                val filler = anchor / zoomStepRatio          // between anchor and anchor/2
                if (filler <= anchor / 2f) break             // gap too small to hold a filler
                if (filler / s >= minZoom * s) steps.add(filler / s)
                anchor /= 2f
            }
        }

        // --- Exact 1:1 (in case the geometric loop skipped it) ---
        steps.add(1f / s)

        if (steps.isEmpty()) steps.add(1f)   // degenerate-bounds guard

        zoomSteps = steps.sortedDescending().distinct().toFloatArray()
        stepIndex = zoomSteps.indexOfFirst { it >= 1f }.coerceAtLeast(0)
        targetZoom = zoomSteps[stepIndex]
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        lastX = screenX.toFloat()
        lastY = screenY.toFloat()
        touchDownX = screenX.toFloat()
        touchDownY = screenY.toFloat()
        isDragging = false
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        val dx = screenX - touchDownX
        val dy = screenY - touchDownY
        if (!isDragging && dx * dx + dy * dy > dragThreshold * dragThreshold) {
            isDragging = true  // crossed the threshold -- this is a drag, not a tap
        }

        if (isDragging) {
            val scaleX = viewport.worldWidth / viewport.screenWidth
            val scaleY = viewport.worldHeight / viewport.screenHeight
            val deltaX = (lastX - screenX) * scaleX * sensitivity
            val deltaY = (screenY - lastY) * scaleY * sensitivity
            camera.position.x = (camera.position.x + deltaX).coerceIn(-boardExtent, boardExtent) // .. -> ,
            camera.position.y = (camera.position.y + deltaY).coerceIn(-boardExtent, boardExtent)
            camera.update()
        }

        lastX = screenX.toFloat()
        lastY = screenY.toFloat()
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (!isDragging) {
            onTap(screenX, screenY)  // finger lifted without meaningful movement -- genuine tap
        } else {
            snapPosition()
        }
        isDragging = false
        return false
    }

//    override fun scrolled(amountX: Float, amountY: Float): Boolean {
//        camera.zoom = (camera.zoom + amountY * zoomSensitivity).coerceIn(minZoom, maxZoom)
//        camera.update()
//        return false
//    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        stepIndex = (stepIndex + if (amountY > 0) 1 else -1)
            .coerceIn(0, zoomSteps.size - 1)
        targetZoom = zoomSteps[stepIndex]
        return true
    }

    /** Call once per frame from the screen's render loop. */
    fun update() {
        if (camera.zoom != targetZoom) {
            camera.zoom += (targetZoom - camera.zoom) * 0.2f
            if (kotlin.math.abs(targetZoom - camera.zoom) < 0.001f) {
                camera.zoom = targetZoom
                snapPosition()
            }
            camera.update()
        }
    }

    /** Snap camera position to the texel grid for the current zoom. Call after drags complete (touchUp) and after zoom settles. */
    fun snapPosition() {
        val grid = worldPerScreen * camera.zoom
        camera.position.x = (camera.position.x / grid).roundToInt() * grid
        camera.position.y = (camera.position.y / grid).roundToInt() * grid
        camera.update()
    }


}
