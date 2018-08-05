package com.github.srad.infernorunner.entity.state

import com.github.srad.infernorunner.core.GameInfo
import com.github.srad.infernorunner.entity.AModelInstance

abstract class State<T : AModelInstance>(val parent: T) {
    open fun handleInput(gameInfo: GameInfo, delta: Float) {}
    open fun update(delta: Float) {}
    open fun enter() {}
    open fun exit() {}
}

class VoidState<T : AModelInstance>(parent: T) : State<T>(parent)

class StateManager<T : AModelInstance> {
    private var currentStates = ArrayList<State<T>>()
    private lateinit var currentStateName: Enum<*>
    private val states = HashMap<String, ArrayList<State<T>>>()

    fun addState(enum: Enum<*>, vararg states: State<T>) {
        this.states[enum.name] = arrayListOf(*states)
    }

    var state: Enum<*>
        set(enum) {
            currentStates.forEach { it.exit() }
            currentStateName = enum
            currentStates = states[enum.name]!!
            currentStates.forEach { it.enter() }
        }
        get() = currentStateName

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