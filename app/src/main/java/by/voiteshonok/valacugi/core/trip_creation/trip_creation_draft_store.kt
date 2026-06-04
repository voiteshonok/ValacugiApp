package by.voiteshonok.valacugi.core.trip_creation

object TripCreationDraftStore {
    var currentDraft: TripCreationDraft? = null

    fun saveDraft(draft: TripCreationDraft) {
        currentDraft = draft
    }

    fun clearDraft() {
        currentDraft = null
    }
}
