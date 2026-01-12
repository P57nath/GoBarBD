const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

exports.notifyBookingStatus = functions.firestore
  .document("bookings/{bookingId}")
  .onUpdate(async (change, context) => {
    const before = change.before.data();
    const after = change.after.data();

    if (!before || !after) {
      return null;
    }
    if (before.status === after.status) {
      return null;
    }

    const userId = after.customerId;
    if (!userId) {
      return null;
    }

    const userSnap = await admin.firestore().collection("users").doc(userId).get();
    const token = userSnap.get("fcmToken");
    if (!token) {
      return null;
    }

    const payload = {
      notification: {
        title: "Booking update",
        body: `Your booking is now ${after.status}.`,
      },
      data: {
        bookingId: context.params.bookingId,
        status: after.status,
      },
    };

    return admin.messaging().sendToDevice(token, payload);
  });
