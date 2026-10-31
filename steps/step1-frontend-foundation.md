# Step 1: Frontend Foundation

> **Test Suite Status:** `Tests run: 17, Failures: 0, Errors: 0` ✅ **BUILD SUCCESS**

---

## 1. Overview & Objective
Establish a clean, robust, and accessible Black-and-White frontend architecture for the School Management System using server-side Thymeleaf and Bootstrap 5 (via CDN).

Key goals:
- Create a unified black-and-white design system adhering strictly to the grayscale palette (`#000`, `#fff`, `#f5f5f5`, `#e0e0e0`, `#666`).
- Create a reusable Thymeleaf master layout (`templates/fragments/layout.html`) with role-based sidebar navigation and mobile offcanvas support.
- Build reusable UI fragments (`templates/fragments/ui.html`) for alerts, modals, stat cards, filters, and badges.
- Replace repetitive session and authorization logic with a centralized Spring MVC `HandlerInterceptor`.

---

## 2. Files Created & Modified

### A. Security & Configuration — `src/main/java/com/example/SchoolWebsite/config/`
| File | Description |
| :--- | :--- |
| [`RoleInterceptor.java`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/java/com/example/SchoolWebsite/config/RoleInterceptor.java) | Central `HandlerInterceptor` that guards `/admin/**`, `/teacher/**`, `/student/**`, `/staff/**`, and `/staff/transport`. Redirects unauthenticated requests to `/login` and sends HTTP 403 on role mismatch. |
| [`WebConfig.java`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/java/com/example/SchoolWebsite/config/WebConfig.java) | Implements `WebMvcConfigurer` to register `RoleInterceptor` across all application URL patterns. |
| [`GlobalControllerAdvice.java`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/java/com/example/SchoolWebsite/config/GlobalControllerAdvice.java) | `@ControllerAdvice` injecting role flags (such as `isDriver`) globally into models so sidebar links display seamlessly without modifying individual controller methods. |

### B. Styling & Design System — `src/main/resources/static/css/`
| File | Description |
| :--- | :--- |
| [`style.css`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/static/css/style.css) | Complete black-and-white CSS design system: system font stack, 8px spacing units, 1px solid borders, slightly rounded cards (`2px`), outlined/filled status badges, light-gray table headers, and Bootstrap color resets. |

### C. Reusable Fragments & Layout — `src/main/resources/templates/fragments/`
| File | Description |
| :--- | :--- |
| [`layout.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/fragments/layout.html) | Shared master layout with top navigation bar (school branding, user details, logout), role-aware sidebar with active-route highlighting, mobile offcanvas hamburger toggle, breadcrumbs, and main content placeholder. |
| [`ui.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/fragments/ui.html) | Reusable Thymeleaf components: `flash-messages`, `pagination`, `stat-card`, `filter-bar`, `confirm-modal`, `empty-state`, and `status-badge`. |

### D. Error Pages — `src/main/resources/templates/error/`
| File | Description |
| :--- | :--- |
| [`403.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/error/403.html) | Clean, monochrome "Access Denied" page with user role diagnostics and navigation back to safe portals. |
| [`404.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/error/404.html) | Clean, monochrome "Page Not Found" page with navigation links. |

### E. Proof-of-Concept Pages
- [`index.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/index.html): Home portal rendered with master layout, KPI stat cards, and role navigation.
- [`login.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/login.html): Centered monochrome login form using the master layout.

---

## 3. Architecture & Request Flow

```
Incoming Browser Request
           │
           ▼
RoleInterceptor.preHandle()       ← Central security gate before controller
  ├─ Public URL or asset?         → Pass through
  ├─ No active session?           → Redirect to /login
  ├─ Role does not match rule?    → Send HTTP 403 Forbidden (renders error/403.html)
  └─ Authorized                   → Proceed
           │
           ▼
GlobalControllerAdvice            ← Adds global context (e.g. isDriver) to Model
           │
           ▼
Spring MVC Controller Method      ← Handles business logic & prepares Model attributes
           │
           ▼
Thymeleaf Engine                  ← Renders master layout.html + requested template content
```

---

## 4. Design System Tokens

| Token | Hex Value | Purpose |
| :--- | :--- | :--- |
| `--c-black` | `#000000` | Primary text, borders, high-emphasis action buttons |
| `--c-white` | `#ffffff` | Backgrounds, inverted text |
| `--c-light` | `#f5f5f5` | Table headers, card headers, filter panels |
| `--c-mid` | `#e0e0e0` | Subtle dividing lines, inactive borders |
| `--c-muted` | `#666666` | Secondary labels, breadcrumbs, helper hints |
| `--sidebar-w` | `220px` | Fixed desktop sidebar navigation width |
| `--topbar-h` | `52px` | Fixed desktop header height |
| `--radius` | `2px` | Subtle corner rounding on buttons and cards |

---

## 5. Verification & Testing

1. **Automated Tests**:
   ```bash
   ./mvnw test
   ```
   *Result:* 17 tests passed (0 failures, 0 errors).

2. **Manual Checks**:
   - `http://localhost:8080/login`: Styled centered form with topbar.
   - `http://localhost:8080/`: Responsive stat cards and dynamic role links.
   - Role protection: Log in as a student and attempt accessing `/admin/students` &rarr; returns styled 403 page.

