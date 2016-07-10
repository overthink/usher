package com.markfeeney.usher

import scala.language.implicitConversions
import org.antlr.v4.runtime.misc.ParseCancellationException
import org.scalatest.FunSuite

class RouteTest extends FunSuite {

  test("empty route is invalid") {
    intercept[ParseCancellationException](Route.compile(""))
  }

  test("invalid chars in route") {
    intercept[ParseCancellationException](Route.compile("/a b/c"))
    intercept[ParseCancellationException](Route.compile("/a\tb"))
  }

  test("static routes") {
    def t(path: String) = Route.parse(Route.compile(path), path)
    assert(t("/").contains(Map.empty))
    assert(t("/foo").contains(Map.empty))
    assert(t("/foo/bar").contains(Map.empty))
    assert(t("/foo/bar.html").contains(Map.empty))
  }

  test("finding route param names") {
    def t(path: String): Vector[String] = {
      val r = Route.compile(path)
      assert(r.path == path)
      r.paramNames
    }
    assert(t("/") == Vector.empty)
    assert(t("/foo") == Vector.empty)
    assert(t("/foo/bar") == Vector.empty)
    assert(t("/foo/:id") == Vector("id"))
    assert(t("/foo/:id/bar") == Vector("id"))
    assert(t("/foo/:id/bar/:bar-id") == Vector("id", "bar-id"))
    assert(t("/foo/*/bar/:id/*/:id2/b") == Vector("*", "id", "*", "id2"))
    assert(t("/:x/:x/:y/:x") == Vector("x", "x", "y", "x"))
    assert(t("/foo/:x.y") == Vector("x.y"))
    assert(t("/:ä/:ユニコード") == Vector("ä", "ユニコード"), "Supports unicode param names")
    assert(t("/foo%20bar/:baz") == Vector("baz"))
  }

  test("extracting param values") {
    def t(path: String, url: String) = Route.parse(Route.compile(path), url)
    def m(pairs: (String, Vector[String])*) = Some(Map(pairs: _*)) // allows ParamValue implicits to work below
    implicit def doNotDoThis(s: String): Vector[String] = Vector(s) // TODO: shim while I refactor (he claims)

    assert(t("/", "/").contains(Map.empty))
    assert(t("/foo", "/foo").contains(Map.empty))
    assert(t("/foo", "/bar").isEmpty)
    assert(t("/:x", "/foo") == m("x" -> "foo"))
    assert(t("/:x", "/foo/bar").isEmpty)
    assert(t("/:x/:y", "/foo/bar") == m("x" -> "foo", "y" -> "bar"))
    assert(t("/:x/*/:y", "/foo/whatever/bar") == m("x" -> "foo", "*" -> "whatever", "y" -> "bar"))
    withClue("repeated param names") {
      assert(t("/:x/:x", "/foo/bar") == m("x" -> Vector("foo", "bar")))
      assert(t("/:x/:x/foo/:y/42", "/a/b/foo/c/42") == m("x" -> Vector("a", "b"), "y" -> "c"))
    }
  }

  test("inline regular expressions routes") {
    def t(path: String, url: String) = Route.parse(Route.compile(path), url)
    assert(t("/foo/:id{\\d+}", "/foo/42").contains(Map("id" -> Vector("42"))))
    assert(t("/foo/:id{\\d+}", "/foo/bar").isEmpty)
    assert(t("/foo/:id{ba*r}", "/foo/bar").contains(Map("id" -> Vector("bar"))))
    assert(t("/foo/:id{ba*r}", "/foo/baaaaar").contains(Map("id" -> Vector("baaaaar"))))
    assert(t("/foo/:id{ba*r}", "/foo/quux").isEmpty)
    assert(t("/:id{[abc]}", "/a").contains(Map("id" -> Vector("a"))))
    assert(t("/:id{[abc]}", "/d").isEmpty)
    assert(t("/:id{fo?}", "/f").contains(Map("id" -> Vector("f"))))
    assert(t("/:id{fo?}", "/fo").contains(Map("id" -> Vector("fo"))))
    assert(t("/:id{fo.}", "/fox").contains(Map("id" -> Vector("fox"))))
    assert(t("/:id{fo.}", "/fo").isEmpty)
    withClue("alternates work") {
      assert(t("/blah/:id{all|[1-9]\\d+}", "/blah/one").isEmpty)
      assert(t("/blah/:id{all|[1-9]\\d+}", "/blah/all").contains(Map("id" -> Vector("all"))))
      assert(t("/blah/:id{all|[1-9]\\d+}", "/blah/42").contains(Map("id" -> Vector("42"))))
    }
  }

  test("nested braces in regex work") {
    def t(path: String, url: String) = Route.parse(Route.compile(path), url)
    assert(t("/blah/:id{\\d{3,5}}", "/blah/9000").contains(Map("id" -> Vector("9000"))))
    assert(t("/blah/:id{\\d{3,5}}", "/blah/42").isEmpty)
  }

  test("empty regex works, matches empty string") {
    def t(path: String, url: String) = Route.parse(Route.compile(path), url)
    assert(t("/blah/:id{}", "/blah/42").isEmpty)
    assert(t("/blah/:id{}", "/blah/").contains(Map("id" -> Vector(""))))
  }

  test("route matches against raw URI") {
    def t(path: String, url: String) = Route.parse(Route.compile(path), url)
    // ignore... should this be allowed to work?
    assert(t("/blah/:name{. .}", "/blah/a%20b").isEmpty)
  }

}
