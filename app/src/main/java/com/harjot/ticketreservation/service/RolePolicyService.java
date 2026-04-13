package com.harjot.ticketreservation.service;

import com.harjot.ticketreservation.model.UserProfile;
import com.harjot.ticketreservation.util.AppConstants;

public class RolePolicyService {
    public boolean canManageEvents(UserProfile profile) {
        if (profile == null || profile.getRole() == null) {
            return false;
        }
        if (AppConstants.ROLE_ADMIN.equals(profile.getRole())) {
            return true;
        }
        return AppConstants.ROLE_ORGANIZER.equals(profile.getRole())
                && AppConstants.STATUS_APPROVED.equals(profile.getStatus());
    }

    public boolean canApproveOrganizers(UserProfile profile) {
        return profile != null && AppConstants.ROLE_ADMIN.equals(profile.getRole());
    }
}
