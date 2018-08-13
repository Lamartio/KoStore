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

@[7-9](Calling `dispatch` with a `LoginAction` instance should login the user)

Note: 
The state is kept within a `Store` object that can render a new state whenever an action is given to  the `dispatch` function. In order the create a new state, `dispatch` will call the `middleware`, which will call the `reducer`, which will notify to observer. How this calling is done, will be explained further in this presentation.

--- Reducer
``` Kotlin
class Store(
  var state: User = User()
  var reducer: (state: User, action: Any) -> User
) {

  fun dispatch(action: Any) {
    state = reducer(state,action)
  }

}
```
@[3](A reducer function is added as parameter of the `Store`)
@[6-8](The reducer is called whenever an action is dispatched)

Note:
A reducer is a function that has as input the current state and an action and returns a new state. Such function is often simple and pure and thereby very easy to test.

--- Middleware
Note:
A middleware function is the place in which all the side effeet can be handled. These can be for instance networking, persistence, logging. 
