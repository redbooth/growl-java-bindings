package com.aerofs.growl;

import java.awt.image.RenderedImage;
import java.security.acl.LastOwnerException;

public class Notification
{
    public enum Priority {
        VERY_LOW(-2),
        MODERATE(-1),
        NORMAL(0),
        HIGH(1),
        EMERGENCY(2);

        private int value;
        private Priority(int value) { this.value = value; }
        public int getValue() { return value;}
    }

    private NotificationType _type;
    private String _title;
    private String _message;
    private String _groupId;
    private RenderedImage _icon;
    private Priority _priority;
    private boolean _isSticky;
    private Runnable _clickedCallback;
    private Runnable _dismissedCallback;

    public Notification(NotificationType type)
    {
        _type = type;
        _priority = Priority.NORMAL;
    }

    // Getters
    NotificationType getType()      { return _type; }
    String getTitle()               { return _title; }
    String getMessage()             { return _message; }
    String getGroup()               { return _groupId; }
    RenderedImage getIcon()         { return _icon; }
    Priority getPriority()          { return _priority; }
    boolean isSticky()              { return _isSticky; }
    Runnable getClickedCallback()   { return _clickedCallback; }
    Runnable getDismissedCallback() { return _dismissedCallback; }

    // Setters
    public Notification setTitle(String title)           { _title = title;         return this; }
    public Notification setMessage(String message)       { _message = message;     return this; }
    public Notification setGroup(String groupId)         { _groupId = groupId;     return this; }
    public Notification setIcon(RenderedImage icon)      { _icon = icon;           return this; }
    public Notification setPriority(Priority priority)   { _priority = priority;   return this; }
    public Notification setSticky(boolean sticky)        { _isSticky = sticky;     return this; }
    public Notification setClickedCallback(Runnable r)   { _clickedCallback = r;   return this; }
    public Notification setDismissedCallback(Runnable r) { _dismissedCallback = r; return this; }
}
