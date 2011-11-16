#import <Foundation/Foundation.h>
#import <Growl/Growl.h>
#include <JavaVM/jni.h>


@interface GrowlJavaDelegate : NSObject <GrowlApplicationBridgeDelegate> {
	NSDictionary*  registrationDictionary;
	NSString*      applicationNameForGrowl;
	NSData*        applicationIconDataForGrowl;
    JavaVM*        jvm;
    jobject        javaDelegate;
    jmethodID      onNotificationClicked;
    jmethodID      onNotificationDismissed;    
}

- (id) initWithAllNotifications:(NSArray*)allNotifications defaultNotifications:(NSArray*)defaultNotifications;

@property (retain) NSString*      applicationNameForGrowl;
@property (retain) NSData*        applicationIconDataForGrowl;
@property (retain) NSDictionary*  registrationDictionaryForGrowl;
@property          JavaVM*        jvm;
@property          jobject        javaDelegate;
@property          jmethodID      onNotificationClicked;
@property          jmethodID      onNotificationDismissed;

- (void) growlNotificationWasClicked:(id)clickContext;
- (void) growlNotificationTimedOut:(id)clickContext;

@end
