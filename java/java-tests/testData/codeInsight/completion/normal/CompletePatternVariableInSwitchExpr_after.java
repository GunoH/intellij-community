
class Main {
  int f(Object o) {
    return switch(o) {
        case Integer integer -> <caret>
    }
  }
}