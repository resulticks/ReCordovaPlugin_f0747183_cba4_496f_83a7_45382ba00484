/********* ResulticksPlugin.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>
#import <REIOSSDK/REIOSSDK.h>

@interface ReCordovaPlugin : CDVPlugin {
    // Member variables go here.
}
- (void)userRegister:(CDVInvokedUrlCommand*)command;
- (void)customEvent:(CDVInvokedUrlCommand*)command;
- (void)screenNavigation:(CDVInvokedUrlCommand*)command;
- (void)locationUpdate:(CDVInvokedUrlCommand*)command;
- (void)getNotification:(CDVInvokedUrlCommand*)command;
- (void)deleteNotification:(CDVInvokedUrlCommand*)command;
//- (void)onNotificationPayloadReceiver:(CDVInvokedUrlCommand*)command;

-(void)deleteNotificationByObject:(CDVInvokedUrlCommand*)command ;
-(void)deleteNotificationByCampaignId:(CDVInvokedUrlCommand*)command;
-(void)deleteNotificationByNotificationId:(CDVInvokedUrlCommand*)command;

-(void)getUnReadNotificationCount:(CDVInvokedUrlCommand*)command;
-(void)getReadNotificationCount:(CDVInvokedUrlCommand*)command;
-(void)readNotification:(CDVInvokedUrlCommand*)command;
-(void)unReadNotification:(CDVInvokedUrlCommand*)command;
-(void)notificationCTAClicked:(CDVInvokedUrlCommand*)command;
-(void)handleQrLink:(CDVInvokedUrlCommand *)command;
- (void)getDeepLinkData:(CDVInvokedUrlCommand *)command;


@end

@implementation ReCordovaPlugin

- (id)init{
    
    self = [super init];
    
       if (self) {
           // initialize instance variables here
       }
    NSLog(@"ReCordova Plugin ready");
    return self;
}

+(void)load{
    NSLog(@"ReCordova Plugin Load");
}

- (void)userRegister:(CDVInvokedUrlCommand*)command {

    CDVPluginResult *pluginResult = nil;
    NSDictionary *params = [command.arguments firstObject];

    NSLog(@"userRegister token %@",params);


    if (params != nil && [params count] > 0) {

        NSLog(@"userRegister token %@",params);

        [REiosHandler sdkRegistrationWithDictWithParams:params];

        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"params"];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }
}

- (void)customEvent:(CDVInvokedUrlCommand*)command {

    CDVPluginResult *pluginResult = nil;
    NSDictionary *params = [command.arguments firstObject];

    if (params != nil && [params count] > 0) {

        NSString *eventName = [params valueForKey:@"eventName"];
        NSDictionary *dataDict = [params valueForKey:@"data"];
        NSLog(@"customEvent %@",dataDict);


        NSError * err;
        NSData * jsonData = [NSJSONSerialization  dataWithJSONObject:dataDict options:0 error:&err];
        NSString * dataString = [[NSString alloc] initWithData:jsonData   encoding:NSUTF8StringEncoding];
        NSLog(@"%@",dataString);

        [REiosHandler addEventWithEventName:eventName data:dataString];

        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"params"];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }
}

- (void)screenNavigation:(CDVInvokedUrlCommand*)command {

    CDVPluginResult *pluginResult = nil;
    NSDictionary *params = [command.arguments firstObject];

    if (params != nil && [params count] > 0) {

        NSString *screenName = [params valueForKey:@"screenName"];
        NSLog(@"screenName %@",screenName);
        
        [REiosHandler setScreenNameWithScreenName:screenName];

        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"params"];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }
}

- (void)locationUpdate:(CDVInvokedUrlCommand*)command {

    CDVPluginResult *pluginResult = nil;
    NSDictionary *params = [command.arguments firstObject];

    if (params != nil && [params count] > 0) {

        NSNumber *latitude = [params valueForKey:@"latitude"];
        NSNumber *longitude = [params valueForKey:@"longitude"];
        NSLog(@"latitude %@",latitude);
        NSLog(@"longitude %@",longitude);

        [REiosHandler updateLocationWithLat:[latitude stringValue] long:[longitude stringValue]];

        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"params"];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }
}

- (void)getNotification:(CDVInvokedUrlCommand*)command {

    NSLog(@"command.argument %@", command.arguments);

    NSError* error = nil;

    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:[REiosHandler getNotificationList] options:NSJSONWritingPrettyPrinted error:&error];
    NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];

    NSLog(@"Notification list json string %@", jsonString);

    CDVPluginResult* pluginResult = nil;

    @try {

        if (jsonString != nil && [jsonString length] > 0) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:jsonString];
        } else {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        }
    } @catch (NSException* exception) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION messageAsString:[exception reason]];
    }
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

}

- (void)deleteNotification:(CDVInvokedUrlCommand*)command {

    CDVPluginResult *pluginResult = nil;
    NSDictionary<NSString *, id> *params = [command.arguments firstObject];

    if (params != nil && [params count] > 0) {

        NSLog(@"deleteNotification %@",params);

        [REiosHandler deleteNotificationListWithDict:params];

        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"params"];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }
}

-(void)deleteNotificationByCampaignId:(CDVInvokedUrlCommand*)command {
  NSError *err = nil;
  
  @try {
     NSDictionary *params = [command.arguments firstObject];
      if (params != nil && [params count] > 0) {
           [REiosHandler deleteNotificationByCampaignIdWithCampaignId:params[@"campaignId"]];
      }
      
     
   }
   @catch (NSException *exception) {
      NSLog(@"%@", exception.reason);
   }
}

-(void)deleteNotificationByNotificationId:(CDVInvokedUrlCommand*)command {
  
  NSError *err = nil;

  @try {
     NSDictionary *params = [command.arguments firstObject];
      if (params != nil && [params count] > 0) {
            [REiosHandler deleteNotificationByNotificationIdWithNotificationId:params[@"notificationId"]];
           }
  }
  @catch (NSException *exception) {
     NSLog(@"%@", exception.reason);
  }
}
//MARK:- Get UnReadNotification Count
-(void)getUnReadNotificationCount:(CDVInvokedUrlCommand *)command {
    
    [self.commandDelegate runInBackground:^{
           [REiosHandler getUnReadNotificationCountOnSuccess:^(NSInteger count)  {
               CDVPluginResult *pluginResult = nil;
               pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:(int)count];
               [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
           }];
       }];
    
 
}
//MARK:- Get readNotification count

-(void)getReadNotificationCount:(CDVInvokedUrlCommand *)command {
    
    [self.commandDelegate runInBackground:^{
           [REiosHandler getReadNotificationCountOnSuccess:^(NSInteger count)  {
               CDVPluginResult *pluginResult = nil;
               pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:(int)count];
               [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
           }];
           
       }];
  
}



//MARK:- Get readNotification

-(void)readNotification:(CDVInvokedUrlCommand *)command {
     
    
    [self.commandDelegate runInBackground:^{
        NSDictionary *params = [command.arguments firstObject];
        if (params != nil && [params count] > 0) {
            [REiosHandler readNotificationWithNotificationId:params[@"notificationId"] onSuccess:^(NSInteger count) {
                CDVPluginResult *pluginResult = nil;
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:(int)count ];
                [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
            }];
        }
    }];
     
    
}



//MARK:- Get unReadNotification

-(void)unReadNotification:(CDVInvokedUrlCommand *)command {
    
    
    [self.commandDelegate runInBackground:^{
         NSDictionary *params = [command.arguments firstObject];
        if (params != nil && [params count] > 0) {
            [REiosHandler unReadNotificationWithNotificationId:params[@"notificationId"] onSuccess:^(NSInteger count) {
                CDVPluginResult *pluginResult = nil;
                   pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:(int)count ];
                   [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
            }];
        }
    }];
}

//MARK:- Get notificationCTAClicked

-(void)notificationCTAClicked:(CDVInvokedUrlCommand *)command {
    
    
    [self.commandDelegate runInBackground:^{
        NSDictionary *params = [command.arguments firstObject];
           
        if (params != nil && [params count] > 0) {
                         [REiosHandler notificationCTAClickedWithNotificationId:params[@"notificationId"] actionId:params[@"actionId"] onSuccess:^(NSInteger count) {
                                CDVPluginResult *pluginResult = nil;
                                   pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:(int)count ];
                                   [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                            }];
                      }
    }];
    
    

}

-(void)handleQrLink:(CDVInvokedUrlCommand *)command {
     
    [self.commandDelegate runInBackground:^{
        NSDictionary *params = [command.arguments firstObject];
           
        if (params != nil && [params count] > 0) {
            
            [REiosHandler handleQrLinkWithUrl:params[@"qrlink"] successHandler:^(NSDictionary<NSString *,id> * result) {
                CDVPluginResult *pluginResult = nil;
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:result ];
                [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                
            } failureHandler:^(NSString * error) {
                CDVPluginResult *pluginResult = nil;
                               pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error];
                [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
            }];
        }
    }];
    
    
}

- (void)getDeepLinkData:(CDVInvokedUrlCommand *)command {
    [REiosHandler getDeeplinkingDataWithSuccess:^(NSDictionary<NSString *,id> * result) {
        CDVPluginResult *pluginResult = nil;
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:result ];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        
    } failure:^(NSString * error) {
        CDVPluginResult *pluginResult = nil;
                                      pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error];
                       [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}


/*


//- (void)onNotificationPayloadReceiver:(CDVInvokedUrlCommand*)command {
//
//    CDVPluginResult *pluginResult = nil;
//    NSDictionary *params = [command.arguments firstObject];
//
//    if (params != nil && [params count] > 0) {
//
//        NSLog(@"longitude %@",params);
//
//        [[REiosHandler getNotification] setNotificationActionWithResponse:@{}];
//
//        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"params"];
//    } else {
//        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
//    }
//}
*/



@end
