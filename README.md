# KoStore
[![](https://jitpack.io/v/lamartio/kostore.svg)](https://jitpack.io/#lamartio/kostore)

A functional implementation of Redux for Kotlin. 
## Example
Imagine creating an app where the user should login. When the user is not logged in, he should use his username and password to login. The credentials are send to a server and it will returns us a token or an error.


### 1. Create the reducer 
There are three states involved:

```kotlin
sealed class LoginState {
    data class NotLoggedIn(val reason: String? = null) : LoginState()
    object LoggingIn : LoginState()
    data class LoggedIn(val token: String) : LoginState()
}
```
Next step is to inventorise how the state can transition to another. The object necessary to do so, is called the action.
```kotlin
sealed class LoginAction {
    data class Login(val name: String, val pass: String) : LoginAction()
    data class Success(val token: String) : LoginAction()
    data class Failure(val error: String) : LoginAction()
    object Logout : LoginAction()
}
```
To transition from one state to another, we will create a `Reducer<T>`. This is a `typealias`, which is a readable name for a function that has the signature: `(State,Action) -> State`.
```kotlin
val userReducer: Reducer<LoginState> = { state: LoginState, action: Any ->
    when (action) {
        is LoginAction.Login -> LoginState.LoggingIn
        is LoginAction.Success -> LoginState.LoggedIn(action.token)
        is LoginAction.Failure -> LoginState.NotLoggedIn(action.error)
        is LoginAction.Logout -> LoginState.NotLoggedIn()
        else -> state
    }
}
```
This reducer is not entirely correct, since the state can transition from `LoggedOut` to `LoggedIn` without first going to `LoggingIn`. To create for ourselves an overview of all possible transitions, one can make a state table:

| current     	| action  	| next        	|
|-------------	|---------	|-------------	|
| NotLoggedIn 	| Login   	| LoggingIn   	|
| LoggingIn   	| Success 	| LoggedIn    	|
| LoggingIn   	| Failure 	| NotLoggedIn 	|
| LoggedIn    	| Logout  	| NotLoggedIn 	| 

With KoStore it is easy to convert such table to code with the use of a `TableReducer`.

```kotlin
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
```
### 2. Create the middleware
Whenever the login action is received, it has to do a (asynchronous) network operation. During the operation the state is 'LoggingIn'. These side effects are done within the middleware function. The example below checks whether the action is `LoginAction.Login` and when that's true it will emit the action and either failure or success. 

NOTE: In the example threading is not handled, but it is good practices to call `next()` always on the same thread on which the `Store` is used.
```kotlin
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
```
Calling `next` basically passes it parameter to the `reducer`. Whenever state is `LoggedIn` or `NotLoggedIn` it needs to be persisted.
```kotlin
fun saveMiddleware(save: (LoginState) -> Unit): Middleware<LoginState> =
        afterNext { getState: () -> LoginState, dispatch: (Any) -> Unit, action: Any, next: (Any) -> Unit ->
            val state = getState()

            if (state !== LoginState.LoggingIn)
                save(state)

        }
``` 
These two middlewares represent the middleware necessary to change the `LoginState`. To make these easier to (re)use, we can bundle them into one `middleware`:
```kotlin
fun loginMiddleware(
        networkOperation: NetworkOperation = { _, _, _ -> }, // stub
        persist: (LoginState) -> Unit = {} // stub
): Middleware<LoginState> =
        arrayOf(
                networkMiddleware(networkOperation),
                persistMiddleware(persist)
        ).reduce(::combine)
```
### 3. Create a store
The goal of using Redix is having an object to which you can send actions to and receive state changes through an observer. The object that facilitates this, is called the 'Store'.

Usually a store is a composition of multiple (sub-)states. Therefore the `Store` gives access to a initialization DSL in which you can install multiple reducers and middlewares.
```kotlin
data class AppState(val state: LoginState = LoginState.NotLoggedIn())

val store: Store<AppState> = Store(AppState()) {

    // compose facilitates the working between a state (AppState) and it's substate (LoginState)
    compose({ it.state }, { copy(state = it) }) {
        addReducer(loginReducer)
        addMiddleware(loginMiddleware())
    }

}
```
For convience there are also `compose` functions for working with `Collection`, `List` and `Map`. 