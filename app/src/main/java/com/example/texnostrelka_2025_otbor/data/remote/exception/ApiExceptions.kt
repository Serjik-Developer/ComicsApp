package com.example.texnostrelka_2025_otbor.data.remote.exception

class NotAuthorizedException(message: String) : Exception(message)
class BadRequestException(message: String) : Exception(message)
class ApiException(message: String) : Exception(message)
class NetworkException(message: String) : Exception(message)
class NotFoundException(message: String) : Exception(message)
class ConflictException(message: String) : Exception(message)
class ForbiddenException(message: String) : Exception(message)
class TooManyRequests(message: String) : Exception(message)