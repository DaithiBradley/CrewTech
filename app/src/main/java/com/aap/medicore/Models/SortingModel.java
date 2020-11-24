package com.aap.medicore.Models;

import java.util.Date;
import java.util.List;

public class SortingModel {

    private Date date;
    private List<AssignedIncidencesModel> assignedIncidencesModels;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<AssignedIncidencesModel> getAssignedIncidencesModels() {
        return assignedIncidencesModels;
    }

    public void setAssignedIncidencesModels(List<AssignedIncidencesModel> assignedIncidencesModels) {
        this.assignedIncidencesModels = assignedIncidencesModels;
    }
}
