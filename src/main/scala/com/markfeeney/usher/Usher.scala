package com.markfeeney.usher

import com.markfeeney.circlet.Circlet.handler
import com.markfeeney.circlet._

import scala.util.Try

object Usher{

  case class Params(params: Map[String, Vector[String]]) {
    /**
     * Helper to get first element from value named `paramNam`.
     */
    def getStr(paramName: String): Option[String] = {
      params.get(paramName).flatMap(_.headOption)
    }

    /**
     * Helper to get first element from value named `paramNam` as an Int.
     */
    def getInt(paramName: String): Option[Int] = {
      getStr(paramName).flatMap(x => Try(x.toInt).toOption)
    }
 }

  /** Return parsed route params stored on `req`, if any. */
  def get(req: Request): Option[Params] = {
    Try(req.attrs("routeParams").asInstanceOf[Params]).toOption
  }

  /** Set `params` to be the route params on `req`. */
  def set(req: Request, params: Params): Request = {
    req.updated("routeParams", params)
  }

  /**
   * Merge `incoming` route params into any existing route params that may
   * already be on `req`.
   */
  private def mergeParams(req: Request, incoming: Map[String, Vector[String]]): Request = {
    val params: Params = get(req) match {
      case Some(prev) => prev.copy(params = prev.params ++ incoming)
      case _ => Params(incoming)
    }
    set(req, params)
  }

  /**
   * Middleware that calls handler with route params extracted if `route`
   * matches request.
   */
  def ifRoute(route: Route): Middleware = handler => req => k => {
    Route.parse(route, req.uri) match {
      case Some(incoming) => handler(mergeParams(req, incoming))(k)
      case None => k(None)
    }
  }

  /**
   * Call handler if request method matches `method`, otherwise return no
   * response.
   */
  private def ifMethod(method: HttpMethod): Middleware = handler => req => k => {
    if (req.requestMethod == method) {
      handler(req)(k)
    } else {
      k(None)
    }
  }

  /**
   * Middleware that will call handler with route params extracted if the
   * request method matches `method` and the request matches `route`.
   */
  private def ifMatches(method: HttpMethod, route: Route): Middleware = {
    ifMethod(method).andThen(ifRoute(route))
  }

  /**
   * Return a handler that tries calling `candidate`, but falls back to
   * `fallback` if the former returns no response.
   */
  private def tryHandler(candidate: Handler, fallback: Handler): Handler = req => k => {
    candidate(req) {
      case resp @ Some(_) => k(resp)
      case None => fallback(req)(k)
    }
  }

  /**
   * Sequentially try each handler in `hs` until one returns a response.  If
   * none of the `hs` return a response, the returned handler won't either.
   * Used to be called `firstOf`, but marketing demanded a rename.
   */
  def routes(hs: Handler*): Handler = hs.reduceLeft(tryHandler)

  def GET(r: Route): Middleware = ifMatches(HttpMethod.Get, r)
  def POST(r: Route): Middleware = ifMatches(HttpMethod.Post, r)
  def PUT(r: Route): Middleware = ifMatches(HttpMethod.Put, r)
  def DELETE(r: Route): Middleware = ifMatches(HttpMethod.Delete, r)
  def HEAD(r: Route): Middleware = ifMatches(HttpMethod.Head, r)
  def OPTIONS(r: Route): Middleware = ifMatches(HttpMethod.Options, r)
  def ANY(r: Route): Middleware = ifRoute(r)

  def context(r: Route): Middleware = ifRoute(r)

  /** Helper for very simple 404 pages */
  def notFound(msg: String): Handler = handler { req => Response(status = 404, body = msg) }

  // Feature request from the marketing dept.
  // allows `GET("/a/:id") { req => ... }`
  // vs. `GET("/a/:id") { req => k => ... }` (have to deal with the continuation)
  // vs. `GET("/a/:id")(handler { req => ... })` (extra parens ugly)
  type SimpleMiddleware = (Request => Option[Response]) => Handler

  /**
   * Variations of the main HTTP verb matchers that hide continuations.
   */
  object Simple {
    def GET(r: Route): SimpleMiddleware = f => Usher.GET(r)(handler(f))
    def POST(r: Route): SimpleMiddleware = f => Usher.POST(r)(handler(f))
    def PUT(r: Route): SimpleMiddleware = f => Usher.PUT(r)(handler(f))
    def DELETE(r: Route): SimpleMiddleware = f => Usher.DELETE(r)(handler(f))
    def HEAD(r: Route): SimpleMiddleware = f => Usher.HEAD(r)(handler(f))
    def OPTIONS(r: Route): SimpleMiddleware = f => Usher.OPTIONS(r)(handler(f))
    def ANY(r: Route): SimpleMiddleware = f => ifRoute(r)(handler(f))

    def context(r: Route): SimpleMiddleware = f => ifRoute(r)(handler(f))
  }

}
