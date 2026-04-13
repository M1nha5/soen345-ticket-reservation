const admin = require("firebase-admin");
const functions = require("firebase-functions/v2/https");
const nodemailer = require("nodemailer");
const twilio = require("twilio");

admin.initializeApp();

const db = admin.firestore();

exports.sendConfirmation = functions.onCall(async (request) => {
  if (!request.auth || !request.auth.uid) {
    throw new functions.HttpsError("unauthenticated", "Authentication required");
  }
  const reservationId = request.data.reservationId;
  const action = request.data.action;
  if (!reservationId || !action || (action !== "reserved" && action !== "cancelled")) {
    throw new functions.HttpsError("invalid-argument", "Invalid reservation request");
  }

  const reservationRef = db.collection("reservations").doc(reservationId);
  const reservationSnap = await reservationRef.get();
  if (!reservationSnap.exists) {
    throw new functions.HttpsError("not-found", "Reservation not found");
  }

  const reservation = reservationSnap.data();
  const callerUid = request.auth.uid;
  const callerUserSnap = await db.collection("users").doc(callerUid).get();
  const callerRole = callerUserSnap.exists ? callerUserSnap.data().role : "";
  if (callerUid !== reservation.userId && callerRole !== "admin") {
    throw new functions.HttpsError("permission-denied", "Not allowed");
  }

  const existingConfirmation = await db.collection("confirmations")
    .where("reservationId", "==", reservationId)
    .where("action", "==", action)
    .limit(1)
    .get();
  if (!existingConfirmation.empty) {
    const existingData = existingConfirmation.docs[0].data();
    return {
      smsStatus: existingData.smsStatus || "skipped",
      emailStatus: existingData.emailStatus || "skipped"
    };
  }

  const userSnap = await db.collection("users").doc(reservation.userId).get();
  if (!userSnap.exists) {
    throw new functions.HttpsError("not-found", "User not found");
  }
  const user = userSnap.data();

  const message = `Reservation ${action}: ${reservation.eventTitle} (${reservation.tickets} tickets)`;

  const sid = process.env.TWILIO_SID;
  const token = process.env.TWILIO_AUTH_TOKEN;
  const fromPhone = process.env.TWILIO_FROM_NUMBER;
  const gmailUser = process.env.GMAIL_USER;
  const gmailPass = process.env.GMAIL_APP_PASSWORD;

  let smsStatus = "skipped";
  let emailStatus = "skipped";

  if (sid && token && fromPhone && user.phone) {
    const twilioClient = twilio(sid, token);
    try {
      await twilioClient.messages.create({
        body: message,
        from: fromPhone,
        to: user.phone
      });
      smsStatus = "sent";
    } catch (e) {
      smsStatus = "failed";
    }
  }

  if (gmailUser && gmailPass && user.email) {
    const transporter = nodemailer.createTransport({
      service: "gmail",
      auth: {
        user: gmailUser,
        pass: gmailPass
      }
    });

    try {
      await transporter.sendMail({
        from: gmailUser,
        to: user.email,
        subject: "Ticket Reservation Confirmation",
        text: message
      });
      emailStatus = "sent";
    } catch (e) {
      emailStatus = "failed";
    }
  }

  await db.collection("confirmations").add({
    reservationId,
    userId: reservation.userId,
    eventId: reservation.eventId,
    action,
    smsStatus,
    emailStatus,
    createdAt: admin.firestore.FieldValue.serverTimestamp()
  });

  return { smsStatus, emailStatus };
});
