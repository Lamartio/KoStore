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
data class LoginAction(val name: String, val pass:String

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
@[7](The middleware function is called)
@[12](The middleware can call the reducer as many times as it needs)

Note:
Within the middleware
- Asynchronisity like persisting or networking
- Logging: Check the state before and after dispatching
- Edit the current action or dispatch new actions

