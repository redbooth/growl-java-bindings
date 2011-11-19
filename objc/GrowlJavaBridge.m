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

#import "GrowlJavaBridge.h"
#import "GrowlJavaDelegate.h"

/// helper functions
NSData* NSDataFromJByteArray(JNIEnv* env, jbyteArray jbarray);
NSString* NSStringFromJString(JNIEnv* env, jstring jstring);
NSArray* NSArrayFromJStringArray(JNIEnv* env, jobjectArray jarray);
/////////

JNIEXPORT jboolean JNICALL Java_com_aerofs_growl_GrowlApplicationBridge_00024GrowlNativeMethods_isMistEnabled(JNIEnv* env, jobject obj)
{
    return [GrowlApplicationBridge isMistEnabled];
}

JNIEXPORT jboolean JNICALL Java_com_aerofs_growl_GrowlApplicationBridge_00024GrowlNativeMethods_isGrowlRunning(JNIEnv* env, jobject obj)
{    
    return [GrowlApplicationBridge isGrowlRunning];
}

JNIEXPORT void JNICALL Java_com_aerofs_growl_GrowlApplicationBridge_00024GrowlNativeMethods_register(JNIEnv* env, 
                                                                             jobject obj,
                                                                             jobject bridge,
                                                                             jstring jappName, 
                                                                             jbyteArray jiconData, 
                                                                             jobjectArray jallNotifications, 
                                                                             jobjectArray jenabledNotifications)
{
    assert([GrowlApplicationBridge growlDelegate] == nil); // Assert that we don't register twice
    
    GrowlJavaDelegate* delegate = [[GrowlJavaDelegate alloc] initWithAllNotifications:NSArrayFromJStringArray(env, jallNotifications) 
                                                     defaultNotifications:NSArrayFromJStringArray(env, jenabledNotifications)];
    
    JavaVM* jvm;
    (*env)->GetJavaVM(env, &jvm);
    jclass cls = (*env)->GetObjectClass(env, bridge);

    delegate.jvm = jvm;
    delegate.javaDelegate = (*env)->NewGlobalRef(env, bridge);
    delegate.onNotificationClicked = (*env)->GetMethodID(env, cls, "onNotificationClicked", "(I)V");
    delegate.onNotificationDismissed = (*env)->GetMethodID(env, cls, "onNotificationDismissed", "(I)V");
    delegate.applicationNameForGrowl = NSStringFromJString(env, jappName);
    delegate.applicationIconDataForGrowl = NSDataFromJByteArray(env, jiconData);
    
    [GrowlApplicationBridge setGrowlDelegate: delegate];
    
    // TODO: Must have an autorelease pool ?
}

JNIEXPORT void JNICALL Java_com_aerofs_growl_GrowlApplicationBridge_00024GrowlNativeMethods_notify(JNIEnv* env, 
                                                                   jobject obj, 
                                                                   jstring jtitle, 
                                                                   jstring jdescription, 
                                                                   jstring jnotificationName, 
                                                                   jbyteArray jiconData, 
                                                                   jint jpriority, 
                                                                   jboolean jisSticky, 
                                                                   jint jclickContext, 
                                                                   jstring jidentifier)
{
      
    NSLog(@"IDENTIFIER: %@", NSStringFromJString(env, jidentifier));
    
    [GrowlApplicationBridge notifyWithTitle:NSStringFromJString(env, jtitle) 
                                description:NSStringFromJString(env, jdescription) 
                           notificationName:NSStringFromJString(env, jnotificationName) 
                                   iconData:NSDataFromJByteArray(env, jiconData) 
                                   priority:jpriority 
                                   isSticky:jisSticky 
                               clickContext: [NSNumber numberWithInt:jclickContext] 
                                 identifier:NSStringFromJString(env, jidentifier)];
}

JNIEXPORT void JNICALL Java_com_aerofs_growl_GrowlApplicationBridge_00024GrowlNativeMethods_release(JNIEnv* env, jobject obj)
{
    GrowlJavaDelegate* delegate = (GrowlJavaDelegate*) [GrowlApplicationBridge growlDelegate];   
    (*env)->DeleteGlobalRef(env, delegate.javaDelegate);
    [delegate release];
}

// helper methods

NSData* NSDataFromJByteArray(JNIEnv* env, jbyteArray jbarray)
{
    if (!jbarray) {
        return nil;   
    }
    
    jbyte* bytes = (*env)->GetByteArrayElements(env, jbarray, NULL);
	NSData* result = [NSData dataWithBytes:bytes length:(*env)->GetArrayLength(env, jbarray)];
	(*env)->ReleaseByteArrayElements(env, jbarray, bytes, JNI_ABORT);
    return result;
}

NSString* NSStringFromJString(JNIEnv* env, jstring jstring)
{
    if (!jstring) {
        return nil; 
    }                   
    
    NSString* result = nil;
    const jchar* unichars = NULL;
    
    unichars = (*env)->GetStringChars(env, jstring, NULL);
    if ((*env)->ExceptionOccurred(env)) {
        return nil;
    }
    result = [NSString stringWithCharacters:unichars length:(*env)->GetStringLength(env, jstring)];
    (*env)->ReleaseStringChars(env, jstring, unichars);
    return result;
}

NSArray* NSArrayFromJStringArray(JNIEnv* env, jobjectArray jarray)
{
    int length = (*env)->GetArrayLength(env, jarray);
    NSMutableArray* result = [NSMutableArray arrayWithCapacity:length];
    for(int i = 0; i < length; i++) {
        jstring jstr = (*env)->GetObjectArrayElement(env, jarray, i);
        [result addObject: NSStringFromJString(env, jstr)];
        (*env)->DeleteLocalRef(env, jstr);
    }
    return result;
}