Growl 1.3 Java Bindings for Mac OS X
-------------------------------

Why use this library?
---------------------

Starting from version 1.3, Growl introduced a new protocol called GNTP. There are GNTP implementations in a variety of languages, [including Java](http://growl.info/documentation/developer/bindings.php).

However, Growl's framework for Cocoa developers (also known as Growl.framework), offers two features that are not found in the other libraries:

- Backwards compatibility with Growl 1.2 (If Growl 1.2 is detected, the older protocol is automatically used in place of GNTP).

- The ability to display notifications [*even if Growl is not running*](http://growl.info/notetodevelopers). This is extremely useful since it removes the need to offer to install Growl on the user's machine. If Growl is installed and running, the user will see the notifications through it, otherwise, notifications will still be displayed.

This library allows Java developers on Mac OS X to use Growl.framework directly, thus benefiting from those features too.

Building it
-----------

1. Build the Xcode project. This will put `libGrowlJavaBridge.jnilib` and `Growl.framework` in the `bin` folder

2. Build the Java code. We currently have an [IDEA](http://www.jetbrains.com/idea/download/) project file. If you're using another IDE, just compile the classes in `src/com/aerofs/growl` and package them into a jar.

3. To use the library, copy `libGrowlJavaBridge.jnilib` AND the `Growl.framework` found under `bin` to a location accessible in your CLASSPATH. The best location is probably the same location as your other jars.

Important: the standard Growl.framework is configured to be loaded from path ../Frameworks/Growl.framework relative to the code using it (in this case, libGrowlJavaBridge.jnilib). If you're using a vanilla Growl.framework instead of the one supplied with this library, you'll have to either put it at the relative path above, or use install\_name\_tool to change this path:

    $ cd Growl.framework/Versions/A/
    $ install_name_tool -id @loader_path/Growl.framework/Versions/A/Growl Growl

You can use `otool -L Growl` to check that the changes have been applied successfully.

Usage
-----

Here's some sample code to get you started:

    // Step 1. During your app initialization, load the library and register your notifications with Growl:

    // In practice, those two variable would be instance variables kept until your application exits.
    final GrowlApplicationBridge growl = new GrowlApplicationBridge("MyApp");
    final NotificationType defaultNotif = new NotificationType("MyApp Notifications");

    try {
        _growl.registerNotifications(defaultNotif);
    } catch (UnsatisfiedLinkError e) {
        l.warn("Failed to load the Growl library: " + e);
    }

    // Step 2. Start sending notifications.
    // If registration failed during initialization, this will produce no result and no errors

    Notification n = new Notification(defaultNotif)
            .setTitle("Hello World")
            .setMessage("This is a test")
            .setClickedCallback(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Notification clicked");
                }
            });

    growl.notify(n);

    // Step 3. When your application quits, release the native resources:

    growl.release();

Contributing & Contact Information
----------------------------------

Pull request are welcome! If you fixed a bug or want to suggest a new feature, feel free to send a pull request. 

For any other information, you can get in touch at support@aerofs.com

License
-------

This library is released under the [BSD license](http://www.opensource.org/licenses/BSD-2-Clause)

Copyright 2011 Air Computing Inc.
