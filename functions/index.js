const admin = require("firebase-admin");
const functions = require("firebase-functions/v2/https");
const nodemailer = require("nodemailer");
const twilio = require("twilio");

admin.initializeApp();

const db = admin.firestore();

exports.sendConfirmation = functions.onCall(async (request) => {
  const reservationId = request.data.reservationId;
  const action = request.data.action;
  if (!reservationId || !action) {
    throw new Error("Missing reservationId or action");
  }

  const reservationRef = db.collection("reservations").doc(reservationId);
  const reservationSnap = await reservationRef.get();
  if (!reservationSnap.exists) {
    throw new Error("Reservation not found");
  }

  const reservation = reservationSnap.data();
  const userSnap = await db.collection("users").doc(reservation.userId).get();
  if (!userSnap.exists) {
    throw new Error("User not found");
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
    await twilioClient.messages.create({
      body: message,
      from: fromPhone,
      to: user.phone
    });
    smsStatus = "sent";
  }

  if (gmailUser && gmailPass && user.email) {
    const transporter = nodemailer.createTransport({
      service: "gmail",
      auth: {
        user: gmailUser,
        pass: gmailPass
      }
    });

    await transporter.sendMail({
      from: gmailUser,
      to: user.email,
      subject: "Ticket Reservation Confirmation",
      text: message
    });
    emailStatus = "sent";
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
