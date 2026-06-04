package by.voiteshonok.valacugi.core.di

import android.content.Context
import by.voiteshonok.valacugi.access.AccessCredentialsValidator
import by.voiteshonok.valacugi.data.repositories.RoomMessagesRepository
import by.voiteshonok.valacugi.data.repositories.RoomThreadsRepository
import by.voiteshonok.valacugi.data.repositories.RoomTripsRepository
import by.voiteshonok.valacugi.data.repositories.RoomUsersRepository
import by.voiteshonok.valacugi.data.room.AppDatabase
import by.voiteshonok.valacugi.core.notifications.ValacugiNotificationSender
import by.voiteshonok.valacugi.core.session.DataStoreSessionRepository
import by.voiteshonok.valacugi.core.session.SessionRepository
import by.voiteshonok.valacugi.domain.MessagesRepository
import by.voiteshonok.valacugi.domain.ThreadsRepository
import by.voiteshonok.valacugi.domain.TripsRepository
import by.voiteshonok.valacugi.domain.UsersRepository

class AppContainer(context: Context) {
    val sessionRepository: SessionRepository = DataStoreSessionRepository(context = context)
    val database: AppDatabase = AppDatabase.create(context = context)
    val usersRepository: UsersRepository = RoomUsersRepository(usersDao = database.usersDao())
    val tripsRepository: TripsRepository = RoomTripsRepository(
        tripsDao = database.tripsDao(),
        threadsDao = database.threadsDao()
    )
    val threadsRepository: ThreadsRepository = RoomThreadsRepository(threadsDao = database.threadsDao())
    val messagesRepository: MessagesRepository = RoomMessagesRepository(
        messagesDao = database.messagesDao(),
        threadsDao = database.threadsDao(),
        lastReadMessagesDao = database.lastReadMessagesDao()
    )
    val accessCredentialsValidator: AccessCredentialsValidator =
        AccessCredentialsValidator(usersRepository = usersRepository)
    val notificationSender: ValacugiNotificationSender =
        ValacugiNotificationSender(context = context.applicationContext)
}

