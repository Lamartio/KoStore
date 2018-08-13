package io.lamart.kostore.examples

import io.lamart.kostore.*
import io.lamart.kostore.utility.TableReducer
import io.lamart.kostore.utility.creates

sealed class LoginState {
    data class NotLoggedIn(val reason: String? = null) : LoginState()
    object LoggingIn : LoginState()
    data class LoggedIn(val token: String) : LoginState()
}

sealed class LoginAction {
    data class Login(val name: String, val pass: String) : LoginAction()
    data class Success(val token: String) : LoginAction()
    data class Failure(val error: String) : LoginAction()
    object Logout : LoginAction()
}

val loginReducer: Reducer<LoginState> = { state: LoginState, action: Any ->
    when (action) {
        is LoginAction.Login -> LoginState.LoggingIn
        is LoginAction.Success -> LoginState.LoggedIn(action.token)
        is LoginAction.Failure -> LoginState.NotLoggedIn(action.error)
        is LoginAction.Logout -> LoginState.NotLoggedIn()
        else -> state
    }
}

val loginTableReducer: Reducer<LoginState> = TableReducer<LoginState> {

    state<LoginState.NotLoggedIn>()
            .withAction<LoginAction.Login>()
            .creates { LoginState.LoggingIn }

    state<LoginState.LoggingIn>()
            .withAction<LoginAction.Success>()
            .creates { action -> LoginState.LoggedIn(action.token) }

    state<LoginState.LoggingIn>()
            .withAction<LoginAction.Failure>()
            .creates { action -> LoginState.NotLoggedIn(action.error) }

    state<LoginState.LoggedIn>()
            .withAction<LoginAction.Logout>()
            .creates { LoginState.NotLoggedIn() }

}


typealias Callback = (error: Exception?, token: String?) -> Unit
typealias NetworkOperation = (name: String, pass: String, callback: Callback) -> Unit

fun networkMiddleware(networkOperation: NetworkOperation): Middleware<LoginState> =
        { getState: () -> LoginState, dispatch: (Any) -> Unit, action: Any, next: (Any) -> Unit ->

            if (action is LoginAction.Login) {
                next(action)
                networkOperation(action.name, action.pass) { error, token ->
                    if (error != null) {
                        next(LoginAction.Failure(error.message!!))
                    } else {
                        next(LoginAction.Success(token!!))
                    }
                }
            } else {
                next(action)
            }

        }


fun persistMiddleware(persist: (LoginState) -> Unit): Middleware<LoginState> =
        { getState: () -> LoginState, dispatch: (Any) -> Unit, action: Any, next: (Any) -> Unit ->
            next(action)
            val state = getState()

            if (state !== LoginState.LoggingIn)
                persist(state)

        }

// the parameters are stubbed for this example
fun loginMiddleware(
        networkOperation: NetworkOperation = { _, _, _ -> },
        persist: (LoginState) -> Unit = {}
): Middleware<LoginState> =
        arrayOf(
                networkMiddleware(networkOperation),
                persistMiddleware(persist)
        ).reduce(::combine)


data class AppState(val state: LoginState = LoginState.NotLoggedIn())

val store: Store<AppState> = Store(AppState()) {

    // compose facilitates the working between a state (AppState) and a substate (LoginState)
    compose({ state }, { copy(state = it) }) {
        addReducer(loginReducer)
        addMiddleware(loginMiddleware())
    }

}