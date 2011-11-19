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
