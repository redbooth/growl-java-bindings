/*
 * Copyright (c) 2011, Air Computing Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.aerofs.growl;

import java.awt.image.RenderedImage;

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
