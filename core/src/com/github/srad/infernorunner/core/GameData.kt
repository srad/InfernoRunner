package com.github.srad.infernorunner.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.IntSet
import com.github.srad.infernorunner.entity.PhysicalModelInstance
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

class Window(var width: Float = 0f, var height: Float = 0f, var midX: Float = 0f, var midY: Float = 0f)

sealed class InputInfo

class MouseInfo(var screenX: Int = 0, var screenY: Int = 0, var pointer: Int = 0, var button: Int = 0, var down: Boolean = false) : InputInfo() {
    var vector3
        get() = Vector3(screenX.toFloat(), screenY.toFloat(), 0f)
        set(v) {
            screenX = v.x.toInt()
            screenY = v.y.toInt()
        }
}

class KeyInfo(val code: IntSet = IntSet(), var down: Boolean = false) : InputInfo() {
    fun pressed(vararg codes: Int): Boolean = codes.any { Gdx.input.isKeyPressed(it) }
}

class AxisInfo(var active: Boolean = false, var value: Float = 0f) : InputInfo() {
    val scale: Float get() = (1 + Math.abs(value))
    val scaleWithSign: Float get() = (1 + Math.abs(value)) * Math.signum(value)
}

class AnalogStick(var horizontal: AxisInfo = AxisInfo(), var vertical: AxisInfo = AxisInfo()) {
    companion object {
        const val noise = 0.1f
        const val AXIS_LY = 0 // -1 is up   | +1 is down
        const val AXIS_LX = 1 // -1 is left | +1 is right
        const val AXIS_RY = 2 // -1 is up   | +1 is down
        const val AXIS_RX = 3 // -1 is left | +1 is right
    }

    val left get() = horizontal.active && (horizontal.value < 0f)
    val right get() = horizontal.active && (horizontal.value > 0f)
    val up get() = vertical.active && (vertical.value < 0f)
    val down get() = vertical.active && (vertical.value > 0f)

    val isMoving get() = left || right || up || down
}

class ControllerInfo(
        var connected: Boolean = false,

        // A standard double analog stick is expected.
        val analogLeft: AnalogStick = AnalogStick(),
        val analogRight: AnalogStick = AnalogStick(),

        // Gamepad buttons.
        var y: Boolean = false,
        var x: Boolean = false,
        var a: Boolean = false,
        var b: Boolean = false,
        var l: Boolean = false,
        var r: Boolean = false,

        var start: Boolean = false,
        var select: Boolean = false,

        var button: Int = 0,
        var down: Boolean = false) : InputInfo()

class GameInfo(val window: Window = Window(),
               val mouse: MouseInfo = MouseInfo(),
               val key: KeyInfo = KeyInfo(),
               val controller: ControllerInfo = ControllerInfo())

class Mask(val bits: Int) {
    companion object {
        const val none = 0
        private var bitMask = 1
        /** Masks are dynamically assigned during runtime. */
        fun next(): Mask {
            synchronized(bitMask) {
                val current = bitMask
                bitMask = bitMask.shl(1)
                return Mask(current)
            }
        }
    }

    private fun intToBin(n: Int): String = String.format("%16s", Integer.toBinaryString(n)).replace(' ', '0')
    override fun toString() = "Mask(${intToBin(bits)})"
}

class CollisionMasks(vararg val masks: KClass<*>)

class MaskInfo(val mask: Mask, private val collidesWith: CollisionMasks) {
    // Union of all bits is the final collision bit-mask: 0010 || 0100 -> 0110
    val collisionMask: Mask by lazy { Mask(collidesWith.masks.fold(0) { finalMask, mask -> finalMask or PhysicalModelInstance.createMask(mask).bits }) }

    override fun toString() = "MaskInfo($mask, collisionMask=$collisionMask)"
}

class GameStatistics(var jumps: AtomicInteger = AtomicInteger(0),
                     var deaths: AtomicInteger = AtomicInteger(0),
                     var lives: AtomicInteger = AtomicInteger(0),
                     var purchases: AtomicInteger = AtomicInteger(0),
                     var hits: AtomicInteger = AtomicInteger(0),
                     var gameOvers: AtomicInteger = AtomicInteger(0)) {
    fun reset() {
        jumps.set(0)
        deaths.set(0)
        lives.set(0)
        purchases.set(0)
        hits.set(0)
        gameOvers.set(0)
    }
}