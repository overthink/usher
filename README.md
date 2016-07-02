# Usher

[![Build Status](https://travis-ci.org/overthink/usher.svg?branch=master)](https://travis-ci.org/overthink/usher)

```scala
// good example TBD; here's a WIP
val h: Handler = GET("/foo/:id") { req =>
  val body = Route.get(req, "id")
  Response(body = s"id was $body")
}
```

Usher is a simple routing library for use with
[Circlet](https://github.com/overthink/circlet).  It's heavily inspired by
[Compojure](https://github.com/weavejester/compojure).

## Try it

No snapshots yet -- soon!

## Project Goals

In priority order:

1. Composable routes
1. Maintainable code
1. Type safe

## License

Copyright &copy; 2016 Mark Feeney

Released under the MIT license.

