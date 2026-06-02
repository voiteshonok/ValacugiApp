---
name: The Minimalist Atlas
colors:
  surface: '#faf9f9'
  surface-dim: '#dbdad9'
  surface-bright: '#faf9f9'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f5f3f3'
  surface-container: '#efeded'
  surface-container-high: '#e9e8e8'
  surface-container-highest: '#e3e2e2'
  on-surface: '#1b1c1c'
  on-surface-variant: '#4c4546'
  inverse-surface: '#303031'
  inverse-on-surface: '#f2f0f0'
  outline: '#7e7576'
  outline-variant: '#cfc4c5'
  surface-tint: '#5e5e5e'
  primary: '#000000'
  on-primary: '#ffffff'
  primary-container: '#1b1b1b'
  on-primary-container: '#848484'
  inverse-primary: '#c6c6c6'
  secondary: '#b32100'
  on-secondary: '#ffffff'
  secondary-container: '#e02c00'
  on-secondary-container: '#fffbff'
  tertiary: '#000000'
  on-tertiary: '#ffffff'
  tertiary-container: '#1b1b1b'
  on-tertiary-container: '#848484'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#e2e2e2'
  primary-fixed-dim: '#c6c6c6'
  on-primary-fixed: '#1b1b1b'
  on-primary-fixed-variant: '#474747'
  secondary-fixed: '#ffdad3'
  secondary-fixed-dim: '#ffb4a4'
  on-secondary-fixed: '#3e0500'
  on-secondary-fixed-variant: '#8c1800'
  tertiary-fixed: '#e2e2e2'
  tertiary-fixed-dim: '#c6c6c6'
  on-tertiary-fixed: '#1b1b1b'
  on-tertiary-fixed-variant: '#474747'
  background: '#faf9f9'
  on-background: '#1b1c1c'
  surface-variant: '#e3e2e2'
typography:
  display-lg:
    fontFamily: Inter
    fontSize: 48px
    fontWeight: '600'
    lineHeight: '1.1'
    letterSpacing: -0.03em
  headline-lg:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '600'
    lineHeight: '1.2'
    letterSpacing: -0.02em
  headline-lg-mobile:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: '1.2'
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Inter
    fontSize: 20px
    fontWeight: '600'
    lineHeight: '1.2'
    letterSpacing: -0.01em
  body-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: '1.5'
    letterSpacing: 0em
  label-sm:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '500'
    lineHeight: '1'
    letterSpacing: 0.05em
  data-tabular:
    fontFamily: Space Mono
    fontSize: 14px
    fontWeight: '400'
    lineHeight: '1.4'
    letterSpacing: 0em
spacing:
  unit: 16px
  xs: 4px
  sm: 8px
  md: 16px
  lg: 32px
  xl: 64px
  gutter: 16px
  margin-mobile: 16px
  margin-desktop: 32px
---

## Brand & Style
The design system is rooted in the "Valacugi" philosophy: an unapologetic Swiss-inspired utility for travel. It prioritizes information density and structural clarity over decorative elements. By combining the raw structural honesty of Brutalism with the precise grid-based discipline of Swiss International Style, the interface functions as a high-performance tool for navigation and logistics.

The aesthetic is characterized by:
- **Severe Geometric Layouts:** A relentless commitment to the grid.
- **High-Contrast Monochrome:** Using black and white to define hierarchy, with safety orange used sparingly for critical status or action.
- **Intentional Whitespace:** Space is used as a functional separator rather than a passive void.
- **Technical Honesty:** Visible borders and monospaced data points emphasize the "Atlas" nature of the application.

## Colors
The palette is strictly functional, mirroring technical manuals and transit wayfinding. 

- **Primary (#000000):** Used for all structural borders, primary text, and high-emphasis icons.
- **Background (#FFFFFF):** The base canvas for all views to ensure maximum contrast.
- **Surface (#F9F9F9):** Used for subtle grouping of secondary information or as a background for code/data blocks.
- **Muted (#808080):** Reserved for secondary labels and non-interactive metadata.
- **Accent (#FF3300):** "Safety Orange" is the only color allowed. It is reserved for primary calls to action, active transit states, or urgent alerts. It should never be used for purely decorative purposes.

## Typography
Typography is the primary vehicle for the design system's identity. 

- **Headlines:** Use Inter with tight tracking and a semi-bold weight to create a "blocked" look.
- **Body:** Standard Inter for readability in long-form itinerary details.
- **Labels:** Uppercase Inter with increased tracking for a functional, "stamped" appearance on small metadata.
- **Data & Metrics:** Space Mono is used exclusively for time, coordinates, flight numbers, and price data to evoke a technical, utilitarian feel. All numeric values should use monospaced fonts to ensure alignment in lists.

## Layout & Spacing
The layout follows a rigorous 16px modular scale.

- **Grid:** A 12-column fixed grid for desktop (max-width 1280px) and a fluid 4-column grid for mobile.
- **Borders as Spacers:** Elements are often separated by 1px solid black lines rather than just negative space.
- **Alignment:** All content must snap to the 16px grid. Avoid soft centering; lean toward hard left-aligned layouts that echo Swiss poster design.
- **Reflow:** On mobile, side-by-side data points reflow into a vertical list of 1px bordered cells.

## Elevation & Depth
This design system avoids shadows entirely. Depth is communicated through structural layering and contrast.

- **Flat Stack:** Elements sit on the same plane, separated by 1px solid borders (`#000000`).
- **Tonal Layering:** The Surface color (`#F9F9F9`) is used to denote a container that is "active" or "secondary" compared to the white background.
- **Active States:** Invert the colors for active elements (black background with white text) to show depth through visual weight rather than Z-axis simulation.
- **Modals:** Use a solid 1px border and a stark 100% white background. If an overlay is required, use a high-opacity (80%) white tint rather than a dark blur.

## Shapes
The shape language is strictly orthogonal. 

- **Sharp Corners:** Every element—from buttons to input fields to images—must have a 0px border radius. 
- **The "Safety Orange" Exception:** Large primary action buttons maintain the 0px radius but use the vibrant `#FF3300` to draw attention.
- **Dividers:** Use 1px horizontal and vertical rules to create cells. Every section should feel like it belongs in a structured ledger.

## Components
Consistent application of the brutalist-utility style across the component library:

- **Buttons:** 0px radius. Primary buttons use black background with white text or Safety Orange for high-priority travel actions. Secondary buttons use a 1px black border with white background.
- **Input Fields:** 1px solid black border. Use `label-sm` (uppercase) as a persistent top-aligned label. 
- **Cards:** Simple 1px black border containers. No shadow. Card headers should be separated from the body by a 1px horizontal line.
- **Chips/Status:** Small rectangular boxes with a 1px border. For "Active" or "Live" travel status, use Safety Orange text or a small solid orange square icon.
- **Lists:** Each item is separated by a 1px solid line. Use `data-tabular` (Space Mono) for any time or reference numbers on the right-hand side of the list item.
- **Checkboxes/Radios:** Square 0px corners. When checked, they should be filled solid black.
- **Transit Timelines:** Use a vertical 2px solid black line to connect stops, with square 8px nodes.