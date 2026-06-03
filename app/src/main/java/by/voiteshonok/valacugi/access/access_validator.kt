package by.voiteshonok.valacugi.access

import by.voiteshonok.valacugi.domain.User
import by.voiteshonok.valacugi.domain.UsersRepository

class AccessCredentialsValidator(
    private val usersRepository: UsersRepository
) {
    suspend fun authenticate(identification: String, credential: String): User? {
        val normalizedLogin: String = identification.trim()
        if (normalizedLogin.isEmpty() || credential.isEmpty()) {
            return null
        }
        return usersRepository.authenticate(login = normalizedLogin, password = credential)
    }
}
