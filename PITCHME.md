--- Essence

``` Kotlin
data class User(
  val name: String = "Danny", 
  val isLoggedIn: Boolean = false
)
```
@[3](When the user is not logged in we show a login screen)
@[3](When the user is logged in we show a home screen)

Note:
In Redux you create an immutable object that reflects the state of your application.
- When the user is not logged in we show a login screen.
- When the user is logged in we show a home screen.
