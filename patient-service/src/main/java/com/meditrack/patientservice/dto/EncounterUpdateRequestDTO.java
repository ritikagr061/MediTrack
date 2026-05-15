package com.meditrack.patientservice.dto;

import com.meditrack.patientservice.model.EncounterLocationType;
import jakarta.validation.constraints.Size;

public class EncounterUpdateRequestDTO {
    @Size(max = 1000)
    private String chiefComplaint;

    @Size(max = 1000)
    private String reasonText;

    private EncounterLocationType locationType;

    @Size(max = 255)
    private String locationText;

    public String getChiefComplaint() {
        return chiefComplaint;
    }

    public void setChiefComplaint(String chiefComplaint) {
        this.chiefComplaint = chiefComplaint;
    }

    public String getReasonText() {
        return reasonText;
    }

    public void setReasonText(String reasonText) {
        this.reasonText = reasonText;
    }

    public EncounterLocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(EncounterLocationType locationType) {
        this.locationType = locationType;
    }

    public String getLocationText() {
        return locationText;
    }

    public void setLocationText(String locationText) {
        this.locationText = locationText;
    }
}
