package com.github.srad.infernorunner.core

import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerListener
import com.badlogic.gdx.controllers.PovDirection
import com.badlogic.gdx.math.Vector3

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
        gameInfo.controller.down = false
        gameInfo.controller.y = false
        gameInfo.controller.x = false
        gameInfo.controller.a = false
        gameInfo.controller.b = false
        gameInfo.controller.l = false
        gameInfo.controller.r = false
        gameInfo.controller.start = false
        gameInfo.controller.select = false
        return true
    }

    override fun ySliderMoved(controller: Controller?, sliderCode: Int, value: Boolean): Boolean {
        return true
    }

    override fun accelerometerMoved(controller: Controller?, accelerometerCode: Int, value: Vector3?): Boolean {
        return true
    }

    override fun axisMoved(controller: Controller, axisCode: Int, value: Float): Boolean {
        gameInfo.controller.connected = true
        val noise = AnalogStick.noise

        gameInfo.controller.analogLeft.horizontal.active = Math.abs(controller.getAxis(AnalogStick.AXIS_LX)) > noise
        gameInfo.controller.analogLeft.vertical.active = Math.abs(controller.getAxis(AnalogStick.AXIS_LY)) > noise
        gameInfo.controller.analogRight.horizontal.active = Math.abs(controller.getAxis(AnalogStick.AXIS_RX)) > noise
        gameInfo.controller.analogRight.vertical.active = Math.abs(controller.getAxis(AnalogStick.AXIS_RY)) > noise

        gameInfo.controller.analogLeft.horizontal.value = controller.getAxis(AnalogStick.AXIS_LX)
        gameInfo.controller.analogLeft.vertical.value = controller.getAxis(AnalogStick.AXIS_LY)
        gameInfo.controller.analogRight.horizontal.value = controller.getAxis(AnalogStick.AXIS_RX)
        gameInfo.controller.analogRight.vertical.value = controller.getAxis(AnalogStick.AXIS_RY)

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
        gameInfo.controller.connected = true
        gameInfo.controller.button = buttonCode
        gameInfo.controller.down = true
        gameInfo.controller.y = buttonCode == 3
        gameInfo.controller.x = buttonCode == 2
        gameInfo.controller.a = buttonCode == 0
        gameInfo.controller.b = buttonCode == 1
        gameInfo.controller.l = buttonCode == 4
        gameInfo.controller.r = buttonCode == 5
        gameInfo.controller.start = buttonCode == 7
        gameInfo.controller.select = buttonCode == 6
        return true
    }

    fun clearInput() {
        gameInfo.key.down = false
        gameInfo.key.code.clear()
    }
}