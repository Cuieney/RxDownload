package zlc.season.rxdownload3.core


class IllegalUrlException(message: String) : RuntimeException(message)

class MissionExitsException : RuntimeException("Mission exists")

class MissionStoppedException : RuntimeException("Mission stopped")

class MissionNotExistsException : RuntimeException("Mission not exists")

class MissionNotCreateException : RuntimeException("Mission not create")

class MissionAlreadyStartException : RuntimeException("Mission already start")

class MissionNotStartException : RuntimeException("Mission not started")