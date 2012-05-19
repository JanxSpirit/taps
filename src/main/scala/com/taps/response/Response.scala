package com.taps.response

/**
 * @author chris_carrier
 * @version 8/19/11
 */


abstract class Response(version: Long, request: String)

case class ErrorResponse(version: Long, request: String, errors: List[String]) extends Response(version, request)