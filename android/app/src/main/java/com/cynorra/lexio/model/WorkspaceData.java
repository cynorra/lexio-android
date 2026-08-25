package com.cynorra.lexio.model;

import java.util.ArrayList;
import java.util.List;

public class WorkspaceData {
    private static final List<Workspace> WORKSPACES = new ArrayList<>();

    public static List<Workspace> getWorkspaces() {
        return WORKSPACES;
    }

    public static void setWorkspaces(List<Workspace> newWorkspaces) {
        WORKSPACES.clear();
        if (newWorkspaces != null) {
            WORKSPACES.addAll(newWorkspaces);
        }
    }
}

