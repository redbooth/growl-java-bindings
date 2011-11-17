package com.aerofs.growl;

import java.awt.image.RenderedImage;

public class NotificationType
{
    private String _name;
    private boolean _disabled;
    private RenderedImage _defaultIcon;

    /**
     * Create a new type of notification
     * @param name must be a human-readable name. It will be displayed in the Growl preferences
     * Example: new NotificationType("New track playing")
     */
    public NotificationType(String name)
    {
        if (name == null || name.isEmpty()) {
            throw new RuntimeException("Growl NotificationType: name cannot be empty");
        }

        _name = name;
        _disabled = false;
    }

    // Getters
    String getName()               { return _name; }
    boolean isDisabledByDefault()  { return _disabled; }
    RenderedImage getDefaultIcon() { return  _defaultIcon; }

    // Setters
    public void setDisabledByDefault(boolean disabled) { _disabled = disabled; }
    public void setDefaultIcon(RenderedImage icon)     { _defaultIcon = icon; }
}
