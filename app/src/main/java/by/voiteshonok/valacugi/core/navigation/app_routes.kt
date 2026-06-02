package by.voiteshonok.valacugi.core.navigation

object AppRoutes {
    const val Boot: String = "boot"
    const val Access: String = "access"
    const val Shell: String = "shell"
    const val Trips: String = "trips"
    const val Directory: String = "directory"
    const val Identity: String = "identity"
    const val Atlas: String = "atlas/{tripId}"
    const val Transmission: String = "transmission/{threadId}"
    const val TripConstructor: String = "trip_constructor"
}

object AppRouteArguments {
    const val TripId: String = "tripId"
    const val ThreadId: String = "threadId"
}
