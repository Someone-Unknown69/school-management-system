package com.example.SchoolWebsite.config;

import com.example.SchoolWebsite.model.Staff;
import com.example.SchoolWebsite.service.StaffService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * RoleInterceptor — runs before EVERY controller method.
 *
 * Rules (matched in order):
 *  1. Public paths (/login, /logout, /css/*, /error, …) → always allowed.
 *  2. Any other path without a valid session → redirect to /login.
 *  3. /admin/**   → admin role only, else HTTP 403.
 *  4. /student/** → student role only, else HTTP 403.
 *  5. /teacher/** → teacher role only, else HTTP 403.
 *  6. /staff/**   → staff role only, else HTTP 403.
 *     /staff/transport (sub-path) → additionally requires designatedRole = "Driver".
 *
 * The existing per-controller guards remain as an extra safety net but
 * this interceptor is the single, authoritative gate.
 */
@Component
public class RoleInterceptor implements HandlerInterceptor {

    private final StaffService staffService;

    public RoleInterceptor(StaffService staffService) {
        this.staffService = staffService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String path = request.getRequestURI();

        // 1. Public paths — skip all checks
        if (isPublicPath(path)) {
            return true;
        }

        // 2. Must be logged in
        HttpSession session = request.getSession(false);
        String userId = (session != null) ? (String) session.getAttribute("userId") : null;
        String role   = (session != null) ? (String) session.getAttribute("role")   : null;

        if (userId == null || role == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        // 3. Role-based path guards
        if (path.startsWith("/admin")) {
            // Allow authenticated students to view fee receipts at /admin/fees/receipt/**
            if (path.startsWith("/admin/fees/receipt/") && "student".equals(role)) {
                return true;
            }
            // Allow authenticated teachers and staff to view salary slips at /admin/salary/slip/**
            if (path.startsWith("/admin/salary/slip/") && ("teacher".equals(role) || "staff".equals(role))) {
                return true;
            }
            if (!"admin".equals(role)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }

        } else if (path.startsWith("/student")) {
            if (!"student".equals(role)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }

        } else if (path.startsWith("/teacher")) {
            if (!"teacher".equals(role)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }

        } else if (path.startsWith("/staff")) {
            if (!"staff".equals(role)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
            // /staff/transport is driver-only within the staff role
            if (path.startsWith("/staff/transport")) {
                Staff staff = staffService.getStaffProfile(userId);
                if (staff == null || !staff.isDriver()) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Paths that require no authentication at all.
     * Static assets, the login/logout flow, and Spring Boot's /error dispatcher.
     */
    private boolean isPublicPath(String path) {
        return path.equals("/")
                || path.equals("/login")
                || path.equals("/logout")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.startsWith("/webjars/")
                || path.startsWith("/error");
    }
}

