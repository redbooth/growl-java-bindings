package com.aerofs.growl;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class GrowlApplicationBridge
{
    // Native methods. They start with an underscore
    private native boolean _isMistEnabled();
    private native boolean _isGrowlRunning();
    private native void _register(String appName, byte[] iconData, String[] allNotifications, String[] enabledNotifications);
    private native void _release();
    private native void _notify(String title, String message, String notificationName, byte[] iconData, int priority, boolean isSticky, int clickContext, String groupId);

    // Callbacks (called by native code)
    void onNotificationClicked(int clickContext)
    {
        System.out.println("Java: clicked " + clickContext);

        Notification n = _pendingCallbacks.remove(clickContext);
        if (n != null && n.getClickedCallback() != null) {
            n.getClickedCallback().run();
        }
    }

    void onNotificationDismissed(int clickContext)
    {
        System.out.println("Java: dismissed " + clickContext);

        Notification n = _pendingCallbacks.remove(clickContext);
        if (n != null && n.getDismissedCallback() != null) {
            n.getDismissedCallback().run();
        }
    }

    static {
        System.loadLibrary("GrowlJavaBridge");
    }

    String _appName;
    RenderedImage _defaultIcon;
    private HashMap<Integer, Notification> _pendingCallbacks = new HashMap<Integer, Notification>();

    public GrowlApplicationBridge(String appName)
    {
        _appName = appName;
    }

    public void setDefaultIcon(RenderedImage icon)
    {
        _defaultIcon = icon;
    }

    public boolean isGrowlRunning()
    {
        return _isGrowlRunning();
    }

    public void registerNotifications(NotificationType... types)
    {
        ArrayList<String> allNotifications = new ArrayList<String>();
        ArrayList<String> enabledNotifications = new ArrayList<String>();

        for(NotificationType nt : types) {
            allNotifications.add(nt.getName());
            if (!nt.isDisabledByDefault()) {
                enabledNotifications.add(nt.getName());
            }
        }

        String[] allNotifArr = allNotifications.toArray(new String[allNotifications.size()]);
        String[] enabledNotifArr = enabledNotifications.toArray(new String[enabledNotifications.size()]);

        _register(_appName, serializeImage(_defaultIcon), allNotifArr, enabledNotifArr);
    }

    public void notify(Notification n)
    {
        int clickContext = 0;
        if (n.getClickedCallback() != null || n.getDismissedCallback() != null) {
            clickContext = n.hashCode();
            _pendingCallbacks.put(clickContext, n);

            // Make sure that we are not leaking Notifications
            // TODO: Do something to clean up the hashmap
            assert (_pendingCallbacks.size() < 16);
        }

        NotificationType type = n.getType();
        RenderedImage icon = (n.getIcon() != null) ? n.getIcon() : type.getDefaultIcon();
        _notify(n.getTitle(), n.getMessage(), type.getName(), serializeImage(icon), n.getPriority().getValue(), n.isSticky(), clickContext, n.getGroup());
    }

    public void release()
    {
        _release();
    }

    // Helper methods

    private byte[] serializeImage(RenderedImage image)
    {
        if (image == null) {
            return null;
        }

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
