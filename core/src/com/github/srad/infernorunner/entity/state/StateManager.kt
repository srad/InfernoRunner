package com.github.srad.infernorunner.entity.state

import com.github.srad.infernorunner.core.GameInfo
import com.github.srad.infernorunner.core.ILoggable
import com.github.srad.infernorunner.entity.AbstractModelInstance
import com.github.srad.infernorunner.entity.player.PlayerState
import java.util.*

abstract class State<T : AbstractModelInstance>(val parent: T) {
    open fun handleInput(gameInfo: GameInfo, delta: Float) {}
    open fun update(delta: Float) {}
    open fun enter() {}
    open fun exit() {}
}

private enum class InternalState { None }

class StateManager<T : AbstractModelInstance> : ILoggable {
    private var currentStates = ArrayList<State<T>>()
    private var currentStateName: Enum<*> = InternalState.None
    private val states = HashMap<Enum<*>, ArrayList<State<T>>>()
    private val stateStack = Stack<Enum<*>>()

    fun addState(enum: Enum<*>, vararg states: State<T>) {
        this.states[enum] = arrayListOf(*states)
    }

    var pushSetState: Enum<*>
        set(enum) {
            stateStack.push(currentStateName)
            state = enum
        }
        get() = state

    var state: Enum<*>
        set(enum) {
            if (currentStateName == enum) {
                return
            }
            if (!states.containsKey(enum)) {
                logError("StateManager", "Missing State: ${enum.name}")
                return
            }
            currentStates.forEach { logDebug("Exiting state: ${it::class.simpleName}"); it.exit() }
            currentStateName = enum
            currentStates = states[enum]!!
            currentStates.forEach { logDebug("Entering state: ${it::class.simpleName}"); it.enter() }
        }
        get() = currentStateName

    fun popState() {
        if (stateStack.size > 0) {
            state = stateStack.pop()
        }
    }

    fun any(vararg states: PlayerState): Boolean {
        for (state in states) {
            for (c in currentStates) {
                if (c == state) {
                    return true
                }
            }
        }
        return false
    }

    fun clear() {
        currentStates.clear()
    }

    fun handleInput(gameInfo: GameInfo, delta: Float) {
        currentStates.forEach { it.handleInput(gameInfo, delta) }
    }

    fun update(delta: Float) {
        currentStates.forEach { it.update(delta) }
    }
}