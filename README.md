# Usher

[![Build Status](https://travis-ci.org/overthink/usher.svg?branch=master)](https://travis-ci.org/overthink/usher)

Usher is a simple routing library for use with
[Circlet](https://github.com/overthink/circlet).  It's heavily inspired by
[Compojure](https://github.com/weavejester/compojure).

```scala
val app: Handler = routes(
  GET("/") { _ => Response(body = "Usher demo\n") },
  GET("/widget/:id") { req => ... },
  POST("/widget") { req => ... },
  PUT("/widget/:id") { req => ... },
  notFound("Computer said no.\n")
)
JettyAdapter.run(app)
```

See the full [example app](https://github.com/overthink/usher-example).

## Try it

Latest release:

```scala
libraryDependencies ++= Seq(
  "com.markfeeney" % "usher_2.11" % "0.1.0"
)
```

Next version snapshots:

```scala
resolvers += Opts.resolver.sonatypeSnapshots
libraryDependencies ++= Seq(
  "com.markfeeney" % "usher_2.11" % "0.2.0-SNAPSHOT"
)
```

## Project Goals

In priority order:

1. Composable routes
1. Maintainable code
1. Type safe
1. Fast enough

## TODO

* regex constraints on route params, e.g. something like `/foo/:id{\\d+}`
* fancy type safe routes idea

## License

Copyright &copy; 2016 Mark Feeney

Released under the MIT license.
