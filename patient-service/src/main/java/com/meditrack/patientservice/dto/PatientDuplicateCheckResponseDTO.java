package com.meditrack.patientservice.dto;

import java.util.List;

public class PatientDuplicateCheckResponseDTO {
    private boolean duplicateFound;
    private long matchCount;
    private List<PatientResponseDTO> matchedPatients;

    public PatientDuplicateCheckResponseDTO() {
    }

    public PatientDuplicateCheckResponseDTO(boolean duplicateFound, long matchCount,
                                            List<PatientResponseDTO> matchedPatients) {
        this.duplicateFound = duplicateFound;
        this.matchCount = matchCount;
        this.matchedPatients = matchedPatients;
    }

    public boolean isDuplicateFound() {
        return duplicateFound;
    }

    public void setDuplicateFound(boolean duplicateFound) {
        this.duplicateFound = duplicateFound;
    }

    public long getMatchCount() {
        return matchCount;
    }

    public void setMatchCount(long matchCount) {
        this.matchCount = matchCount;
    }

    public List<PatientResponseDTO> getMatchedPatients() {
        return matchedPatients;
    }

    public void setMatchedPatients(List<PatientResponseDTO> matchedPatients) {
        this.matchedPatients = matchedPatients;
    }
}
