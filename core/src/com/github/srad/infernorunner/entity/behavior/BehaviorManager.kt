package com.github.srad.infernorunner.entity.behavior

import com.github.srad.infernorunner.core.GameInfo
import com.github.srad.infernorunner.core.ILoggable
import com.github.srad.infernorunner.entity.base.AbstractEntity
import com.github.srad.infernorunner.entity.player.behavior.PlayerBehavior
import java.util.*

abstract class Behavior<T : AbstractEntity>(val parent: T) {
    open fun handleInput(gameInfo: GameInfo, delta: Float) {}
    open fun update(delta: Float) {}
    open fun enter() {}
    open fun exit() {}
}

private enum class InternalBehavior { None }

class BehaviorManager<T : AbstractEntity> : ILoggable {
    private var currentBehaviors = ArrayList<Behavior<T>>()
    private var currentBehavior: Enum<*> = InternalBehavior.None
    private val behaviors = HashMap<Enum<*>, ArrayList<Behavior<T>>>()
    private val behaviorStack = Stack<Enum<*>>()

    fun add(enum: Enum<*>, vararg behaviors: Behavior<T>) {
        this.behaviors[enum] = arrayListOf(*behaviors)
    }

    var pushBehavior: Enum<*>
        set(enum) {
            behaviorStack.push(currentBehavior)
            behavior = enum
        }
        get() = behavior

    var behavior: Enum<*>
        set(enum) {
            if (currentBehavior == enum) {
                return
            }
            if (!behaviors.containsKey(enum)) {
                logError("BehaviorManager", "Missing Behavior: ${enum.name}")
                return
            }
            currentBehaviors.forEach { logDebug("Exiting behavior: ${it::class.simpleName}"); it.exit() }
            currentBehavior = enum
            currentBehaviors = behaviors[enum]!!
            currentBehaviors.forEach { logDebug("Entering behavior: ${it::class.simpleName}"); it.enter() }
        }
        get() = currentBehavior

    fun popBehavior() {
        if (behaviorStack.size > 0) {
            behavior = behaviorStack.pop()
        }
    }

    fun any(vararg behaviors: PlayerBehavior): Boolean {
        for (behavior in behaviors) {
            for (c in currentBehaviors) {
                if (c == behavior) {
                    return true
                }
            }
        }
        return false
    }

    fun clear() {
        currentBehaviors.clear()
    }

    fun handleInput(gameInfo: GameInfo, delta: Float) {
        currentBehaviors.forEach { it.handleInput(gameInfo, delta) }
    }

    fun update(delta: Float) {
        currentBehaviors.forEach { it.update(delta) }
    }
}