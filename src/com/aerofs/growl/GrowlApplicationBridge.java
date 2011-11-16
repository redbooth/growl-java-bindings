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
    private long _timeLastNotif;

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

    /**
     * @return Time elapsed since the last notification, in milliseconds
     */
    public long timeSinceLastNotification()
    {
        return System.currentTimeMillis() - _timeLastNotif;
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
        }

        // Due to bugs in Growl 1.3, it is possible that we miss one or more onDisposed or onClicked calls
        // Because of that, we could leak memory because notifications in _pendingCallbacks would never get deleted
        // So if we have more than 16 notifications pending and it has been 30 seconds since the last notification
        // we assume that all notifications are long gone and we clear _pendingCallbacks
        // Obviously, this might be bad if we used a lot of sticky notifications - which is not the case.
        if (_pendingCallbacks.size() > 16 && timeSinceLastNotification() > 30 * 1000) {
            _pendingCallbacks.clear();
        }

        NotificationType type = n.getType();
        RenderedImage icon = (n.getIcon() != null) ? n.getIcon() : type.getDefaultIcon();
        _notify(n.getTitle(), n.getMessage(), type.getName(), serializeImage(icon), n.getPriority().getValue(), n.isSticky(), clickContext, n.getGroup());
        _timeLastNotif = System.currentTimeMillis();
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
