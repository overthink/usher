# Usher

[![Build Status](https://travis-ci.org/overthink/usher.svg?branch=master)](https://travis-ci.org/overthink/usher)

Usher is a simple routing library for Scala, and is built on
[Circlet](https://github.com/overthink/circlet).  Routes are just Handler
functions, and are easily composed.  Usher is heavily inspired by
[Compojure](https://github.com/weavejester/compojure).

```scala
val app: Handler = routes(
  GET("/") { _ => Response(body = "Usher demo\n") },
  POST("/widget") { req => ... },             // C
  GET("/widget/:id{\\d+}") { req => ... },    // R
  PUT("/widget/:id{\\d+}") { req => ... },    // U
  DELETE("/widget/:id{\\d+}") { req => ... }, // D
  notFound("Computer said no.\n")
)
JettyAdapter.run(app)
```

See the full [example app](https://github.com/overthink/usher-example).

## Try it

Latest release:

```scala
libraryDependencies ++= Seq(
  "com.markfeeney" % "usher_2.11" % "0.2.0"
)
```

## Project Goals

In priority order:

1. Composable routes
1. Maintainable code
1. Type safe
1. Fast enough

## TODO

* fancy type safe routes idea
* `context` for nesting routes a la Compojure - https://github.com/weavejester/compojure/wiki/Nesting-routes
* option to index routes (trie?) for faster matching
* middleware to automatically generate [Allow header](https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.7) (?)

## License

Copyright &copy; 2016 Mark Feeney

Released under the MIT license.
