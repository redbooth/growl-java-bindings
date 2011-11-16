#import "GrowlJavaDelegate.h"

@implementation GrowlJavaDelegate

@synthesize applicationNameForGrowl;
@synthesize applicationIconDataForGrowl;
@synthesize registrationDictionaryForGrowl = registrationDictionary; 
@synthesize jvm;
@synthesize javaDelegate;
@synthesize onNotificationClicked;
@synthesize onNotificationDismissed;    

- (void) dealloc {
	[registrationDictionary release];
	[applicationNameForGrowl release];
	[applicationIconDataForGrowl release];
	[super dealloc];
}

- (id) initWithAllNotifications:(NSArray *)allNotifications defaultNotifications:(NSArray *)defaultNotifications {
	if ((self = [self init])) {
		self.registrationDictionaryForGrowl = [[[NSDictionary alloc] initWithObjectsAndKeys:
                                                allNotifications,     GROWL_NOTIFICATIONS_ALL,
                                                defaultNotifications, GROWL_NOTIFICATIONS_DEFAULT,
                                                nil] autorelease];
	}
    
	return self;
}

- (void) growlNotificationWasClicked:(id)clickContext
{
    if(!jvm || !javaDelegate || !onNotificationClicked) {
        return;   
    }
    
    JNIEnv* env;
    (*jvm)->AttachCurrentThread(jvm, (void**) &env, NULL);
    if (!env) { 
        return; 
    }
    
    int ctx = [(NSNumber*)clickContext intValue];
    (*env)->CallVoidMethod(env, javaDelegate, onNotificationClicked, ctx);
}

- (void) growlNotificationTimedOut:(id)clickContext
{
    if(!jvm || !javaDelegate || !onNotificationDismissed) {
        return;   
    }

    JNIEnv* env;
    (*jvm)->AttachCurrentThread(jvm, (void**) &env, NULL);
    if (!env) { 
        return; 
    }

    int ctx = [(NSNumber*)clickContext intValue];
    (*env)->CallVoidMethod(env, javaDelegate, onNotificationDismissed, ctx);
}

@end
