package pro.gravit.launcher.gui.core.service;

import pro.gravit.launcher.core.api.features.ProfileFeatureAPI;
import pro.gravit.launcher.gui.core.JavaFXApplication;

import java.util.List;

public class ProfileService {
    private List<ProfileFeatureAPI.ClientProfile> profiles;
    private ProfileFeatureAPI.ClientProfile currentProfile;
    private final JavaFXApplication application;

    public ProfileService(JavaFXApplication application) {
        this.application = application;
    }

    public List<ProfileFeatureAPI.ClientProfile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<ProfileFeatureAPI.ClientProfile> profiles) {
        this.profiles = profiles;
    }

    public ProfileFeatureAPI.ClientProfile getCurrentProfile() {
        return currentProfile;
    }

    public void setCurrentProfile(ProfileFeatureAPI.ClientProfile currentProfile) {
        this.currentProfile = currentProfile;
    }
}
