package com.github.srad.infernorunner.core

import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerListener
import com.badlogic.gdx.controllers.PovDirection
import com.badlogic.gdx.math.Vector3

/**
 * Combines the Keyboard and controller input into one object {@class GameInfo}
 * for the game-engine to process.
 */
class InputManager : InputProcessor, ControllerListener {
    val gameInfo = GameInfo()

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        gameInfo.mouse.screenX = screenX
        gameInfo.mouse.screenY = screenY
        gameInfo.mouse.pointer = pointer
        gameInfo.mouse.button = button
        gameInfo.mouse.down = false

        return true
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        gameInfo.mouse.screenX = screenX
        gameInfo.mouse.screenY = screenY

        return true
    }

    override fun keyTyped(character: Char): Boolean {
        gameInfo.key.down = true
        gameInfo.key.code.add(character.toInt())
        return true
    }

    override fun scrolled(amount: Int): Boolean {
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        gameInfo.key.code.remove(keycode)
        gameInfo.key.down = gameInfo.key.code.size > 0

        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        gameInfo.mouse.screenX = screenX
        gameInfo.mouse.screenY = screenY
        gameInfo.mouse.pointer = pointer
        gameInfo.mouse.down = true
        return true
    }

    override fun keyDown(keycode: Int): Boolean {
        gameInfo.key.down = true
        gameInfo.key.code.add(keycode)

        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        gameInfo.mouse.screenX = screenX
        gameInfo.mouse.screenY = screenY
        gameInfo.mouse.pointer = pointer
        gameInfo.mouse.button = button
        gameInfo.mouse.down = true

        return true
    }

    override fun connected(controller: Controller?) {
        gameInfo.controller.connected = true
    }

    override fun buttonUp(controller: Controller?, buttonCode: Int): Boolean {
        gameInfo.controller.apply {
            down = false
            y = false
            x = false
            a = false
            b = false
            l = false
            r = false
            start = false
            select = false
        }
        return true
    }

    override fun ySliderMoved(controller: Controller?, sliderCode: Int, value: Boolean): Boolean {
        return true
    }

    override fun accelerometerMoved(controller: Controller?, accelerometerCode: Int, value: Vector3?): Boolean {
        return true
    }

    override fun axisMoved(controller: Controller, axisCode: Int, value: Float): Boolean {
        val noise = AnalogStick.noise

        gameInfo.controller.apply {
            connected = true

            analogLeft.horizontal.active = Math.abs(controller.getAxis(AnalogStick.AXIS_LX)) > noise
            analogLeft.vertical.active = Math.abs(controller.getAxis(AnalogStick.AXIS_LY)) > noise
            analogRight.horizontal.active = Math.abs(controller.getAxis(AnalogStick.AXIS_RX)) > noise
            analogRight.vertical.active = Math.abs(controller.getAxis(AnalogStick.AXIS_RY)) > noise

            analogLeft.horizontal.value = controller.getAxis(AnalogStick.AXIS_LX)
            analogLeft.vertical.value = controller.getAxis(AnalogStick.AXIS_LY)
            analogRight.horizontal.value = controller.getAxis(AnalogStick.AXIS_RX)
            analogRight.vertical.value = controller.getAxis(AnalogStick.AXIS_RY)
        }

        return true
    }

    override fun disconnected(controller: Controller?) {
        gameInfo.controller.connected = false
    }

    override fun xSliderMoved(controller: Controller?, sliderCode: Int, value: Boolean): Boolean {
        return true
    }

    override fun povMoved(controller: Controller?, povCode: Int, value: PovDirection?): Boolean {
        return true
    }

    override fun buttonDown(controller: Controller?, buttonCode: Int): Boolean {
        gameInfo.controller.apply {
            connected = true
            button = buttonCode
            down = true
            y = buttonCode == 3
            x = buttonCode == 2
            a = buttonCode == 0
            b = buttonCode == 1
            l = buttonCode == 4
            r = buttonCode == 5
            start = buttonCode == 7
            select = buttonCode == 6
        }
        return true
    }

    fun clearInput() {
        gameInfo.key.down = false
        gameInfo.key.code.clear()
    }
}