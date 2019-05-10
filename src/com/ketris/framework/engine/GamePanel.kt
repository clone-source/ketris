package com.ketris.framework.engine

import com.ketris.framework.io.KeyManager
import com.ketris.framework.io.MouseManager
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Toolkit
import java.util.concurrent.TimeUnit
import javax.swing.JPanel

const val FRAMES_PER_SECOND = 60
val REFRESH_INTERVAL_MS: Long = TimeUnit.SECONDS.toMillis(1) / FRAMES_PER_SECOND

class GamePanel(width: Int, height: Int, var screen: GameScreen) : JPanel() {
  private var fps = GameFPS()

  init {
    preferredSize = Dimension(width, height)
    isFocusable = true
    addMouseListener(MouseManager)
    addKeyListener(KeyManager)
  }

  override fun paintComponent(g: Graphics) {
    super.paintComponent(g)
    Game.clearRedrawLock()
    val layers = listOf(
      // layer 1 : current screen
      screen,
      // layer 2 overlay on top to show information about the game
      Overlay(width, height)
    )
    // render all game layers
    layers.forEach { layer -> g.drawImage(layer.paintCanvas(), 0, 0, width, height, null) }

    fps.increment()

    Toolkit.getDefaultToolkit().sync()
    g.dispose()
  }
}
