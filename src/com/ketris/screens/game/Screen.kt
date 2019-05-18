package com.ketris.screens.game

import com.ketris.framework.engine.Game
import com.ketris.framework.engine.GameScreen
import com.ketris.framework.io.IListenToKeyboard
import com.ketris.framework.io.KeyManager
import com.ketris.screens.game.Config.SIDEBAR_WIDTH
import com.ketris.screens.game.Config.WAR_ZONE_WIDTH
import com.ketris.screens.game.playfield.Playfield
import com.ketris.screens.game.sidebar.Sidebar
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage

class Screen : GameScreen, IListenToKeyboard {
  private var nextFall = 0L
  private var wasAnimating = false
  override val painter = Painter(SIDEBAR_WIDTH + WAR_ZONE_WIDTH, Game.height)
  private var playerIsFalling = false
  private val playfield = Playfield(WAR_ZONE_WIDTH, Game.height)
  private val sidebar = Sidebar(SIDEBAR_WIDTH, Game.height, playfield.nextPlayer)

  init {
    KeyManager.addListener(this)
  }

  override fun update(dt: Int) {
    if (playfield.animating) {
      wasAnimating = true
    } else if (wasAnimating) {
      nextFall = System.currentTimeMillis() + playfield.fallRate
      wasAnimating = false
    }
    if (!wasAnimating) {
      applyGravity()
    }
  }

  private fun applyGravity() {
    val time = System.currentTimeMillis()
    if (time >= nextFall) {
      nextFall = time + playfield.fallRate
      handlePlayerFalling()
    }
  }

  private fun handlePlayerFalling() {
    if (playerIsFalling) {
      return
    }
    playerIsFalling = true
    val weHaveANewPlayer = playfield.fallDown()
    if (weHaveANewPlayer) {
      sidebar.nextPlayer = playfield.nextPlayer
    }
    playerIsFalling = false
  }

  override fun keyPressed(e: KeyEvent) {
    when (e.keyCode) {
      KeyEvent.VK_W -> playfield.rotatePlayer()
      KeyEvent.VK_S -> handlePlayerFalling()
      KeyEvent.VK_A -> playfield.moveLeft()
      KeyEvent.VK_D -> playfield.moveRight()
      KeyEvent.VK_R -> playfield.restart()
      KeyEvent.VK_I -> playfield.inspect = true
      KeyEvent.VK_P -> playfield.animating = !playfield.animating
    }
  }

  override fun paint() {
    playfield.paint()
    sidebar.paint()
  }

  override fun paintCanvas(): BufferedImage {
    val playfieldBuffer = playfield.paintCanvas()
    val sidebarBuffer = sidebar.paintCanvas()

    painter.g.drawImage(sidebarBuffer, 0, 0, SIDEBAR_WIDTH, Game.height, null)
    painter.g.drawImage(playfieldBuffer, SIDEBAR_WIDTH, 0, WAR_ZONE_WIDTH, Game.height, null)

    return painter.canvas()
  }
}
