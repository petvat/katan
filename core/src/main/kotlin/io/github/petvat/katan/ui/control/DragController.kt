package io.github.petvat.katan.ui.control

import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.Viewport

class DragCameraController(
    private val camera: OrthographicCamera,
    private val viewport: Viewport,
    private val onTap: (screenX: Int, screenY: Int) -> Unit,  // fired only on genuine taps
    private val boardExtent: Float = 300f,
    private val sensitivity: Float = 1f,
    private val minZoom: Float = 0.2f,
    private val maxZoom: Float = 3f,
    private val zoomSensitivity: Float = 0.1f,
    private val dragThreshold: Float = 8f  // pixels of movement before it counts as a drag
) : InputAdapter() {

    private var lastX = 0f
    private var lastY = 0f
    private var touchDownX = 0f
    private var touchDownY = 0f
    private var isDragging = false

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
            camera.position.x = (camera.position.x + deltaX).coerceIn(-boardExtent..boardExtent)
            camera.position.y = (camera.position.y + deltaY).coerceIn(-boardExtent..boardExtent)
            camera.update()
        }

        lastX = screenX.toFloat()
        lastY = screenY.toFloat()
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (!isDragging) {
            onTap(screenX, screenY)  // finger lifted without meaningful movement -- genuine tap
        }
        isDragging = false
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        camera.zoom = (camera.zoom + amountY * zoomSensitivity).coerceIn(minZoom, maxZoom)
        camera.update()
        return false
    }
}
