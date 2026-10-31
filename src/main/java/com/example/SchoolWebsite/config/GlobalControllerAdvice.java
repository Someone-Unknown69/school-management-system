package com.example.SchoolWebsite.config;

import com.example.SchoolWebsite.model.Staff;
import com.example.SchoolWebsite.service.StaffService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * GlobalControllerAdvice — runs before EVERY controller method and adds
 * shared model attributes so the layout fragment always has what it needs.
 *
 * isDriver:
 *   true  → the logged-in staff member has designatedRole = "Driver"
 *   false → any other role, or not logged in
 *
 * This means the sidebar transport link shows automatically for drivers on
 * every staff page, without each controller having to set isDriver manually.
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    private final StaffService staffService;

    public GlobalControllerAdvice(StaffService staffService) {
        this.staffService = staffService;
    }

    /**
     * Adds isDriver=true/false to every controller's model.
     * A DB call is made only when the session role is "staff".
     */
    @ModelAttribute("isDriver")
    public boolean addIsDriver(HttpSession session) {
        String role   = (String) session.getAttribute("role");
        String userId = (String) session.getAttribute("userId");

        if (!"staff".equals(role) || userId == null) {
            return false;
        }

        Staff staff = staffService.getStaffProfile(userId);
        return staff != null && staff.isDriver();
    }
}

