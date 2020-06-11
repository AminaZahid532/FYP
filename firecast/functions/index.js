// Create and Deploy Your First Cloud Functions
// https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//     console.log("Hello World!")
//  response.send("Hello from this firecast!");
// });

'use strict'

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendMessageNotification = functions.database.ref('/MessageNotifications/{user_id}/{notification_id}')
  .onWrite((change, context) => {

    const data_context = context.params;
    const user_id = data_context.user_id;
    const notification_id = data_context.notification_id;

    console.log('We have a notification : ', user_id);

    const fromUser = admin.database().ref(`/MessageNotifications/${user_id}/${notification_id}`).once('value');
    return fromUser.then(fromUserResult => {

      const userId = fromUserResult.val().to;
      const fromUser = fromUserResult.val().from;
      const userPicUrl = fromUserResult.val().picUrl;
      const notificationText = fromUserResult.val().message;

      console.log('You have new notification from  : ', userId + " " + fromUser);

      // const userQuery = admin.database().ref(`Users/${from_user_id}/name`).once('value');
      const deviceToken = admin.database().ref(`/users/${user_id}/device_token`).once('value');

      return Promise.all([deviceToken]).then(result => {
        const token_id = result[0].val();
        const payload = {
          data: {
            userId: userId,
            fromUser: fromUser,
            userPicUrl: userPicUrl,
            notificationText: notificationText,
            icon: "default",
            sound: "default",
            click_action: "com.tickoo.activities.SplashScreenActivity"
          }
        };

        /*
         * Using admin.messaging() we are sending the payload notification to the token_id of
         * the device we retreived.
         */
         setTimeout(function (){
         	var adaRef = admin.database().ref(`MessageNotifications/${user_id}`);
         	adaRef.remove();
		}, 1000);
        
        return admin.messaging().sendToDevice(token_id, payload);
      });
    });
  });