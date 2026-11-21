package com.zaurh.notefirecloud.core.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Model-View-Intent base:
 *  - the View renders a single immutable [State] from [state];
 *  - every user action is sent as an [Intent] to [onIntent] (the only entry point);
 *  - one-off events (navigation, snackbars) are delivered once through [effects].
 */
abstract class MviViewModel<State, Intent, Effect>(initialState: State) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _effects = Channel<Effect>(Channel.BUFFERED)
    val effects: Flow<Effect> = _effects.receiveAsFlow()

    protected val currentState: State get() = _state.value

    abstract fun onIntent(intent: Intent)

    protected fun setState(reducer: State.() -> State) = _state.update(reducer)

    protected fun sendEffect(effect: Effect) {
        viewModelScope.launch { _effects.send(effect) }
    }
}
