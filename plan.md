---
name: Valacugi MVP week plan
overview: Build a 1-week MVP of Valacugi using the existing Jetpack Compose + Material 3 base, with a Room (SQLite) database seeded with mock data and a repository facade so the data source can later be swapped to an API.
todos:
  - id: arch_scaffold
    content: Add navigation + ViewModel scaffolding; define routes and 3-tab shell.
    status: pending
  - id: room_seed
    content: Implement Room schema + preseed mock data; add repository facade interfaces and implementations.
    status: pending
  - id: screens_core
    content: Implement Trips, Atlas detail, Directory, Transmission, Identity screens with ViewModels and state flows.
    status: pending
  - id: tests
    content: Add unit + DAO + Compose UI tests alongside each screen’s logic.
    status: pending
  - id: polish
    content: Final styling pass, placeholders, empty/error states, smoke test full flow.
    status: pending
isProject: false
---

# Valacugi 1-week MVP plan

## Scope (MVP screens + flows)
- **Access**: email + password input, “authenticating…” fake delay, stores a local `UserSession`.
  - Existing: `[app/src/main/java/com/example/myapplication/ui/access/AccessScreen.kt](/home/voiteshonok/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/ui/access/AccessScreen.kt)` already matches the spec’s access screen patterns.
- **Trips (Dashboard)**: list of trips (cards with DATES/PAX/BUDGET/ASSIGNED), optional orange “SYSTEM ALERT” banner.
  - Source spec: `stitch_the_minimalist_atlas/itineraries_valacugi/code.html`.
- **Atlas (Trip detail)**: trip header + 2x2 data grid + itinerary timeline (days + optional sub-steps).
  - Source spec: `stitch_the_minimalist_atlas/atlas_detail_valacugi/code.html`.
- **Directory (Global inbox)**: list of threads across trips with unread dot + timestamp; opens a trip’s transmission.
  - Source spec: `stitch_the_minimalist_atlas/directory_valacugi_1/code.html`.
- **Transmission (Trip chat)**: linear message stream (bordered boxes) + input bar.
  - Source spec: `stitch_the_minimalist_atlas/transmissions_valacugi/code.html`.
- **Identity (Settings)**: show user id + toggles (square checkboxes), and Logout.
  - Source spec: `stitch_the_minimalist_atlas/identity_valacugi/code.html`.

## Architecture (optimized for 1 week + future API swap)
### Packages (single-module Clean-ish architecture)
- **`domain/`**: pure models + repository interfaces + use cases.
  - Example interfaces: `TripsRepository`, `MessagesRepository`, `IdentityRepository`, `SessionRepository`.
- **`data/`**: Room entities/DAOs + repository implementations + mapping.
  - Room is the SQLite “DB with mock data”; later, we add a `remote/` implementation and keep the same domain interfaces.
- **`ui/`**: feature screens + state management (MVI-ish) + navigation.
- **`core/`** (small): design system components + navigation routes + app container.

### State management
- Per screen: `UiState` (data class) + `UiEvent` (sealed interface) + `ViewModel` exposing `StateFlow<UiState>`.
- Side effects (load/submit) happen in the ViewModel through use cases.

### Navigation
- Add **Navigation Compose** and define routes:
  - `access`
  - `trips`
  - `atlas/{tripId}`
  - `directory`
  - `transmission/{threadId}`
  - `identity`
- Bottom navigation matches brief: **TRIPS / MESSAGES / IDENTITY**; `atlas` and `transmission` are detail routes.

### Dependency injection (keep minimal)
- For speed: start with a lightweight `AppContainer` created in `MainActivity` and passed to root composables.
- If desired later, migrate to Hilt once MVP is stable.

## Database (Room) + mock data
### Entities (minimum viable)
- `TripEntity(tripId, title, dateStart, dateEnd, pax, budgetText, assignedCount, assignedTotal, referenceCode, coordsText)`
- `ItineraryDayEntity(dayId, tripId, dayIndex, tag, title, description)`
- `ItineraryStepEntity(stepId, dayId, title, timeText)` (optional)
- `ThreadEntity(threadId, tripId, title, lastMessagePreview, lastMessageAt, hasUnread)` , lastMessagePreview, lastMessageAt, hasUnread - should be in another logic/table
- `MessageEntity(messageId, threadId, senderId, body, sentAt, isMine)`
- `SettingsEntity(userId, isPushNotifsEnabled)` (and more later)
- `SessionEntity(userId, email)` (or store in `DataStore` later)

### Seeding
- Prepopulate DB on first run using Room `Callback` + `CoroutineScope`.
- Seed at least:
  - 2 trips (London/Tokyo) like the HTML.
  - 1 trip with unknown `[ ? ]` dates to validate placeholder rendering.
  - 2–3 threads and ~5–10 messages.

## UI implementation approach
- Reuse the existing brutalist theme (`MyApplicationTheme`, typography tokens) in:
  - `[app/src/main/java/com/example/myapplication/ui/theme/Theme.kt](/home/voiteshonok/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/ui/theme/Theme.kt)`
  - `[app/src/main/java/com/example/myapplication/ui/theme/Type.kt](/home/voiteshonok/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/ui/theme/Type.kt)`
- Build a tiny component kit for consistency:
  - `BrutalCard` (1px border, 0 radius)
  - `BrutalDivider`
  - `BrutalChip`
  - `ValacugiTopBar` + `ValacugiBottomBar`

## Testing strategy (parallel with “screen-by-screen” logic)
- **Domain/use case tests**: JUnit for mapping/filtering, placeholder rules (`[ ? ]`), ordering.
- **Room tests**: DAO tests with in-memory Room (instrumented).
- **UI tests**: Compose UI tests for:
  - Trips list renders seeded trips.
  - Directory unread dot appears.
  - Transmission send inserts message and UI updates.
  - Logout clears session and returns to Access.

## 7-day execution schedule

### Day 1 — App skeleton + navigation + state conventions
- **Goal**: after Access, user can enter the app shell and switch between **TRIPS / MESSAGES / IDENTITY** tabs (even if the screens are placeholders).
- **Implementation tasks**
  - Add deps
    - Navigation Compose
    - lifecycle viewmodel-compose
    - Room (runtime + compiler) + KSP
    - (optional) kotlinx-datetime for timestamps
  - Create packages
    - `core/navigation/` routes + typed args
    - `core/ui/` top bar + bottom bar + brutal primitives
    - `ui/<feature>/` folders for `trips`, `atlas`, `directory`, `transmission`, `identity`
    - `domain/` and `data/` package roots
  - Define navigation graph
    - `access` start destination
    - `shell` hosting bottom tabs
    - detail routes: `atlas/{tripId}`, `transmission/{threadId}`
  - Define a state pattern template
    - `XxxUiState` (data class)
    - `XxxUiEvent` (sealed interface)
    - `XxxViewModel` exposes `StateFlow<XxxUiState>` and `onEvent(event)`
- **Acceptance checks**
  - Access → Shell transition works.
  - Bottom tabs switch between 3 placeholder screens.
  - Detail navigation can be triggered (to Atlas/Transmission).
- **Tests to add**
  - Compose UI test: Access → Shell, bottom bar shows 3 items.

### Day 2 — Room database + seed + repository facade
- **Goal**: app shows real seeded data (Trips + Directory + Transmission) from SQLite behind repository interfaces.
- **Implementation tasks**
  - Domain layer
    - Models: `Trip`, `ItineraryDay`, `ItineraryStep`, `Thread`, `Message`, `UserSession`, `IdentitySettings`
    - Repository interfaces: `TripsRepository`, `AtlasRepository`, `DirectoryRepository`, `TransmissionRepository`, `SessionRepository`, `IdentityRepository`
    - Use cases (thin): `GetTrips`, `GetTripDetails`, `GetThreads`, `GetThreadMessages`, `SendMessage`, `GetIdentity`, `UpdatePushNotifs`, `Logout`
  - Data layer
    - Room entities + DAOs for Trips, Itinerary, Threads, Messages, Settings, Session
    - Mappers: `Entity ↔ Domain`
    - Database prepopulation callback
      - Seed 2 trips + 1 “unknown field” trip (for `[ ? ]` placeholder behavior)
      - Seed 2–3 threads and 8–15 messages total
  - App wiring
    - `AppContainer` provides repositories + database instance; ViewModels use container-provided use cases
- **Acceptance checks**
  - Fresh install shows seeded Trips list (TOKYO/LONDON).
  - Directory has at least one unread thread with orange dot.
  - Transmission screen can render messages for a thread id.
- **Tests to add**
  - DAO instrumented tests: trips query order; thread unread + last timestamp
  - Unit test: placeholder formatting (`[ ? ]`)

### Day 3 — Trips screen (Dashboard) end-to-end
- **Goal**: Trips screen matches spec and opens Atlas detail.
- **Implementation tasks**
  - UI
    - Top app bar + “TRIPS” subheader + add button
    - Optional orange alert banner driven by `UiState.alertBannerText`
    - Trip rows as bordered list items; mini bordered cells for DATES/PAX/BUDGET/ASSIGNED
  - ViewModel
    - Load trips via `GetTrips`
    - `onTripClick(tripId)` navigates to `atlas/{tripId}`
  - Formatting
    - Dates: show range or `[ ? ]`
    - Assigned: `assignedCount/assignedTotal` with bold count
- **Acceptance checks**
  - Trips list scrolls via `LazyColumn`.
  - Clicking a trip navigates to Atlas.
- **Tests to add**
  - Compose UI test: trips render + click opens Atlas.

### Day 4 — Atlas (Trip detail) + itinerary timeline
- **Goal**: Atlas detail matches spec: header + 2x2 grid + itinerary timeline.
- **Implementation tasks**
  - UI
    - Header: trip title + reference code
    - 2x2 grid on mobile (and optionally 4 columns on wide screens)
    - Timeline: vertical line + square nodes + bordered day cards
    - Day tag chip in brutal style
  - ViewModel
    - `GetTripDetails(tripId)` loads trip + itinerary days + steps
- **Acceptance checks**
  - Correct trip loads by id.
  - Timeline shows at least 2 days from seed.
- **Tests to add**
  - Compose UI test: reference code + Day 01/Day 02 render.
  - Unit test: itinerary ordering by `dayIndex`.

### Day 5 — Messages: Directory + Transmission (send message)
- **Goal**: Directory lists threads; Transmission shows messages and supports sending.
- **Implementation tasks**
  - Directory UI
    - Rows: title + preview + timestamp + unread dot
    - Row click navigates to `transmission/{threadId}`
  - Transmission UI
    - Bordered message blocks (mine right-shifted)
    - Bottom input bar; send inserts into DB
  - ViewModels
    - Directory loads via `GetThreads`
    - Transmission loads via `GetThreadMessages(threadId)` and sends via `SendMessage`
  - Data behavior
    - Send updates thread last preview/time and unread state
- **Acceptance checks**
  - Unread dot appears for seeded unread thread.
  - Sending appends instantly and persists after restart.
- **Tests to add**
  - DAO test: insert message updates thread metadata.
  - Compose UI test: type + send shows message.

### Day 6 — Identity + session + logout
- **Goal**: Identity renders user id + settings; logout returns to Access.
- **Implementation tasks**
  - Identity UI
    - User block (ID)
    - System configuration list with square checkbox toggle(s) (`PUSH_NOTIFS`)
    - Logout row
  - Session
    - On Access success, upsert `SessionEntity`
    - Logout clears session and navigates to `access` with cleared back stack
- **Acceptance checks**
  - Identity shows user id from Session/Settings.
  - Toggle persists (Room).
  - Logout returns to Access and back does not return to shell.
- **Tests to add**
  - Unit test: logout clears session.
  - Compose UI test: logout navigates to Access.

### Day 7 — Stabilize, edge cases, and MVP hardening
- **Goal**: shippable MVP feel: consistent visuals, empty/error states, stability.
- **Implementation tasks**
  - UI consistency: 0 radius, 1px borders, no shadows, orange only for alert/unread/primary CTA
  - Edge cases: empty states, long titles, input validation (Access + Transmission)
  - Performance: `LazyColumn` everywhere, stable keys, avoid recomposition traps
- **Acceptance checks**
  - Full flow: Access → Trips → Atlas → Messages → Transmission(send) → Identity(toggle) → Logout.
  - No obvious layout glitches across devices.
- **Tests to add**
  - One end-to-end Compose UI smoke test for the happy path.

## Deliverable definition (what “done” means)
- App opens to Access, then navigates into a 3-tab shell.
- Trips list + trip detail work from seeded DB.
- Messages tab shows Directory; opening a thread shows Transmission; sending a message persists and appears.
- Identity shows user id + settings toggle; Logout returns to Access.
