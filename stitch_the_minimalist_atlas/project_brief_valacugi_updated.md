# Project Brief: Valacugi (The Minimalist Atlas)

## 1. Executive Summary
**Product Vision:** A brutalist, Swiss-inspired orchestration utility for travel. Valacugi replaces the "wanderlust" aesthetic of traditional travel apps with high-signal, zero-noise structural clarity. It is designed for planners who value data integrity and precise group coordination over glossy imagery.

**Target Audience:** 
- Meticulous itinerary planners.
- Frequent business travelers.
- High-coordination groups (expeditions, production crews, organized family travel).

---

## 2. Design Philosophy: "Unapologetic Utility"
The visual language is rooted in **Swiss International Style** and **Brutalist** web design.

### Core Principles
- **Signal over Noise:** Every element must serve a functional purpose. Decorative elements are stripped away.
- **Structural Integrity:** Layouts are defined by strict 1px solid borders and a modular grid.
- **Typographic Hierarchy:** Heavy use of neo-grotesque sans-serif for headings and monospace for all technical/variable data.
- **Zero Softness:** 0px border radius. Sharp corners. No drop shadows. High contrast.

---

## 3. Screen Specifications

### 1. Access (Authentication)
- **Objective:** Secure, friction-less entry.
- **Visuals:** Massive whitespace, single-line inputs with bottom borders, and full-width high-contrast buttons. Includes email and password authentication.

### 2. Trips (Dashboard)
- **Objective:** Macro-view of all travel commitments.
- **Key Features:**
    - **System Alerts:** Safety Orange (`#FF3300`) banner for critical updates.
    - **Trip Cards:** Data-dense cards displaying Location, Dates, Pax, and Budget.
    - **Unknown Handling:** Pending data renders as monospaced `[ ? ]` tags.

### 3. Atlas (Trip Detail)
- **Objective:** The "Single Source of Truth" for a specific expedition.
- **Key Features:**
    - **2x2 Data Grid:** Split-pane display of core metrics (Coords, Roster, etc.).
    - **Chronological Timeline:** Vertical 1px line with square nodes representing daily steps.

### 4. Transmissions (Linear Chat)
- **Objective:** Structured coordination for specific trips.
- **Logic:** Linear message stream within 1px border boxes. Streamlined for quick status updates and team syncs.

### 5. Directory (Global Inbox)
- **Objective:** Central monitoring of all active communications across different trips. Acts as the folder hub for all Transmissions.

### 6. Identity (System Settings)
- **Objective:** Account management and global configuration.
- **Visuals:** Square checkbox toggles and monospaced User IDs.

---

## 4. Technical Design System

### Colors
- **Primary:** `#000000` (Black)
- **Background:** `#FFFFFF` (White)
- **Surface:** `#F9F9F9` (Light Gray)
- **Accent/Alert:** `#FF3300` (Safety Orange)

### Typography
- **Headings:** `Inter Tight` (Bold, tight tracking)
- **Body:** `Inter` (Standard legibility)
- **Data/Metrics:** `Space Mono` (Technical clarity)

---

## 5. Navigation Structure
The application uses a streamlined three-tab bottom navigation, focusing on core utility:
1. **TRIPS:** Itineraries and active expeditions.
2. **MESSAGES:** The Directory and individual Transmissions.
3. **IDENTITY:** User profile and system settings.
*(Note: The 'Atlas' navigation folder has been removed to increase signal-to-noise ratio.)*