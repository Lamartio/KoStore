``` Kotlin
data class User(
  val name: String = "Danny", 
  val isLoggedIn: Boolean = false
)
```
@[3](When the user is not logged in we show the login screen, otherwise we show the home screen.)

Note:
In Redux you create an immutable object that reflects the state of your application. This state can be used to render the UI:
- When the user is not logged in we show a login screen.
- When the user is logged in we show a home screen.

---
``` Kotlin
data class LoginAction(val name: String, val pass:String)

class Store(
  var state: User = User()
) {

  fun dispatch(action: Any) { 
    // calls the middleware, reducer and observer
  }

}
```
@[7-9](Calling `dispatch` with a `LoginAction` instance should login the user.)

Note: 
The state is kept within a `Store` object that can render a new state whenever an action is given to  the `dispatch` function. In order the create a new state, `dispatch` will call the `middleware`, which will call the `reducer`, which will notify to observer. 

How this calling is done, will be explained further in this presentation.

---
``` Kotlin
class Store(
  var state: User = User()
  var reducer: (state: User, action: Any) -> User
) {

  fun dispatch(action: Any) {
    state = reducer(state, action)
  }

}
```
@[3](A reducer function is added as parameter of the `Store`)
@[6-8](The reducer is called whenever an action is dispatched)

Note:
A reducer is a function that has as input the current state and the action and returns a new state. Such function is often simple and pure and thereby very easy to test.

---
``` Kotlin
class Store(
  var state: User = User()
  var reducer: (state: User, action: Any) -> User
  var middleware: (action: Any, next: (action: Any) -> Unit) -> Unit
) {

  fun dispatch(action: Any) {
    middleware(action, ::next)
  }
  
  private fun next(action: Any) {
    state = reducer(state, action) 
  }

}
```
@[4](A middleware function is added to the store)
@[7-9](The middleware function is called instead of the reducer)
@[8,11-13](The middleware can call the reducer as many times as it needs)

---
``` Kotlin
fun middleware(
  getState: () -> User, 
  dispatch: (action: Any) -> Unit, 
  action: Any, 
  next: (action: Any) -> Unit) {
  // black magic
}
```
@[2](Will return the current state held by the store. Often used to validate the state after something asynchronous.)
@[3](Will call `Store.dispatch` so that all the reducers and middlewares get called.)
@[5](Will update the state by calling the reducer with the given action.)

Note:
The past slide showed a simplified version of the middleware with only two parameters. These two parameters cover the essence of a middleware, but in some occasions you need more than those.

---
``` Kotlin
data class LoginAction(val name: String, val pass:String)
object LoadingAction
object SuccessAction
object FailureAction

private fun login(name: String, pass: String, onSuccess: () -> Unit, onError: () -> Unit) {
 // login magic
}

fun middleware(getState: () -> User, dispatch: (Any) -> Unit, action: Any, next: (Any) -> Unit) {
  when(action) {
    is LoginAction -> {
      next(LoadingAction)
      login(
        action.name, 
        action.pass, 
        { next(SuccessAction) }, 
        { next(FailureAction) }
      )
    }
    else -> next(action)
  }
}

fun reducer(state: User, action: Any) {
  when(action) {
    is SuccessAction -> state.copy(isLoggedIn = true)
    is FailuerAction -> state.copy(isLoggedIn = false)
    else -> state
  }
}
```

@[1-4](Define some actions that will be sent to the reducer.)
@[6-8](Define a function that can handle the asyncronous network call.)
@[10,13](Before the login: Send the loading action)
@[14,10](Call the login function)
@[17-18](After the login: Send either the success or the failure action)
@[25-31](The action are received in the reducer)
@[27-28](For now we only handle success and failure)
