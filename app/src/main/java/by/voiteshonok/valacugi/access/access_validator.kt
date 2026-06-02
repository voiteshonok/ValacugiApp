package by.voiteshonok.valacugi.access

object AccessCredentialsValidator {
    private const val ValidIdentification: String = "admin"
    private const val ValidCredential: String = "admin"

    fun isValid(identification: String, credential: String): Boolean {
        val normalizedIdentification: String = identification.trim()
        return normalizedIdentification.equals(ValidIdentification, ignoreCase = true) &&
            credential == ValidCredential
    }
}
