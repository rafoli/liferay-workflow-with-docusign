package com.liferay.sample.docusign.dto.docuSignConfig;

public class Actions {
    private Action permissions;
    private Action get;
    private Action replace;
    private Action update;

    public Action getPermissions() {
        return permissions;
    }

    public void setPermissions(Action permissions) {
        this.permissions = permissions;
    }

    public Action getGet() {
        return get;
    }

    public void setGet(Action get) {
        this.get = get;
    }

    public Action getReplace() {
        return replace;
    }

    public void setReplace(Action replace) {
        this.replace = replace;
    }

    public Action getUpdate() {
        return update;
    }

    public void setUpdate(Action update) {
        this.update = update;
    }
}
