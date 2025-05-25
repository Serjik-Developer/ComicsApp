package com.example.texnostrelka_2025_otbor.data.remote.exception

class NotAuthorizedException(message: String) : Exception(message) //401
class BadRequestException(message: String) : Exception(message) //400
class ApiException(message: String) : Exception(message) //500
class NetworkException(message: String) : Exception(message) //IO EXCEPTION
class NotFoundException(message: String) : Exception(message) //404
class ConflictException(message: String) : Exception(message) //409
class ForbiddenException(message: String) : Exception(message) //403
class TooManyRequests(message: String) : Exception(message) //429
class InvalidPasswordException(message: String) : Exception(message) //INVALID PASSWORD