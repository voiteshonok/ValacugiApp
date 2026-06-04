package by.voiteshonok.valacugi.data

import by.voiteshonok.valacugi.data.room.ItineraryDayEntity
import by.voiteshonok.valacugi.data.room.ItineraryStepEntity
import by.voiteshonok.valacugi.data.room.MessageEntity
import by.voiteshonok.valacugi.data.room.ThreadEntity
import by.voiteshonok.valacugi.data.room.ThreadWithUnreadEntity
import by.voiteshonok.valacugi.data.room.TripEntity
import by.voiteshonok.valacugi.data.room.TripWithAssignedCountEntity
import by.voiteshonok.valacugi.data.room.UserEntity
import by.voiteshonok.valacugi.domain.ItineraryDay
import by.voiteshonok.valacugi.domain.ItineraryStep
import by.voiteshonok.valacugi.domain.Message
import by.voiteshonok.valacugi.domain.MessageThread
import by.voiteshonok.valacugi.domain.Trip
import by.voiteshonok.valacugi.domain.User

fun UserEntity.toDomain(): User {
    return User(
        id = userId,
        login = login,
        displayName = displayName,
        isPushNotificationsEnabled = pushNotificationsEnabled
    )
}

fun MessageEntity.toDomain(): Message {
    return Message(
        id = messageId,
        threadId = threadId,
        senderId = senderId,
        body = body,
        sentAt = sentAt
    )
}

fun ThreadEntity.toDomain(hasUnread: Boolean = false): MessageThread {
    return MessageThread(
        id = threadId,
        tripId = tripId,
        title = title,
        lastMessagePreview = lastMessagePreview,
        lastMessageAt = lastMessageAt,
        hasUnread = hasUnread
    )
}

fun ThreadWithUnreadEntity.toDomain(): MessageThread {
    return thread.toDomain(hasUnread = hasUnread)
}

fun TripWithAssignedCountEntity.toDomain(): Trip {
    return Trip(
        id = trip.tripId,
        title = trip.title,
        dateStart = trip.dateStart,
        dateEnd = trip.dateEnd,
        pax = trip.pax,
        budgetText = trip.budgetText,
        createdById = trip.createdById,
        assignedCount = assignedCount
    )
}

fun ItineraryDayEntity.toDomain(): ItineraryDay {
    return ItineraryDay(
        id = dayId,
        dayIndex = dayIndex,
        title = title
    )
}

fun ItineraryStepEntity.toDomain(): ItineraryStep {
    return ItineraryStep(
        id = stepId,
        stepIndex = stepIndex,
        title = title,
        timeText = timeText,
        budgetText = budgetText,
        maxPeople = maxPeople
    )
}

