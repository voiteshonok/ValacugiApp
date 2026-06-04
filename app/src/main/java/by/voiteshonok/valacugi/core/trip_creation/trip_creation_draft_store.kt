package by.voiteshonok.valacugi.core.trip_creation

object TripCreationDraftStore {
    var currentDraft: TripCreationDraft? = null
    var pendingConstructorSteps: List<TripStepDraft> = emptyList()

    fun saveDraft(draft: TripCreationDraft) {
        currentDraft = draft
    }

    fun saveDraftForEdit(draft: TripCreationDraft, steps: List<TripStepDraft>) {
        currentDraft = draft
        pendingConstructorSteps = steps
    }

    fun clearDraft() {
        currentDraft = null
        pendingConstructorSteps = emptyList()
    }
}
