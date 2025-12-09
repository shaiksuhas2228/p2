package com.revhub.notificationservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Map;
import java.util.HashMap;

@Document(collection = "user_preferences")
public class UserPreference {
    @Id
    private String id;
    private Long userId;
    private boolean pushEnabled = true;
    private boolean emailEnabled = true;
    private Map<String, Boolean> typePreferences = new HashMap<>();
    private String timezone = "UTC";

    public UserPreference() {
        // Default preferences
        typePreferences.put("FOLLOW", true);
        typePreferences.put("LIKE", true);
        typePreferences.put("COMMENT", true);
        typePreferences.put("POST", true);
        typePreferences.put("SYSTEM", true);
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public boolean isPushEnabled() { return pushEnabled; }
    public void setPushEnabled(boolean pushEnabled) { this.pushEnabled = pushEnabled; }

    public boolean isEmailEnabled() { return emailEnabled; }
    public void setEmailEnabled(boolean emailEnabled) { this.emailEnabled = emailEnabled; }

    public Map<String, Boolean> getTypePreferences() { return typePreferences; }
    public void setTypePreferences(Map<String, Boolean> typePreferences) { this.typePreferences = typePreferences; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
}