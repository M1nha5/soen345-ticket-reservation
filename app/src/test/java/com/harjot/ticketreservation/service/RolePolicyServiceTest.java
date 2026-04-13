package com.harjot.ticketreservation.service;

import com.harjot.ticketreservation.model.UserProfile;
import com.harjot.ticketreservation.util.AppConstants;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RolePolicyServiceTest {
    private final RolePolicyService service = new RolePolicyService();

    @Test
    public void adminCanManageAndApprove() {
        UserProfile profile = new UserProfile("u1", "Admin", "a@a.com", "+1", AppConstants.ROLE_ADMIN, AppConstants.STATUS_APPROVED, 0);
        assertTrue(service.canManageEvents(profile));
        assertTrue(service.canApproveOrganizers(profile));
    }

    @Test
    public void pendingOrganizerCannotManage() {
        UserProfile profile = new UserProfile("u1", "Org", "o@a.com", "+1", AppConstants.ROLE_ORGANIZER, AppConstants.STATUS_PENDING, 0);
        assertFalse(service.canManageEvents(profile));
    }

    @Test
    public void approvedOrganizerCanManage() {
        UserProfile profile = new UserProfile("u1", "Org", "o@a.com", "+1", AppConstants.ROLE_ORGANIZER, AppConstants.STATUS_APPROVED, 0);
        assertTrue(service.canManageEvents(profile));
    }
}
