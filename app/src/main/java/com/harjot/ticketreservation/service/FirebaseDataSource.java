package com.harjot.ticketreservation.service;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.harjot.ticketreservation.model.EventItem;
import com.harjot.ticketreservation.model.ReservationItem;
import com.harjot.ticketreservation.model.UserProfile;
import com.harjot.ticketreservation.util.AppConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseDataSource {
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;
    private final FirebaseFunctions functions;

    public FirebaseDataSource() {
        this.auth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
        this.functions = FirebaseFunctions.getInstance();
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public String getCurrentUid() {
        FirebaseUser user = auth.getCurrentUser();
        return user == null ? null : user.getUid();
    }

    public void signInWithEmail(String email, String password, DataCallback<FirebaseUser> callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> callback.onSuccess(result.getUser()))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void signInWithCredential(AuthCredential credential, DataCallback<FirebaseUser> callback) {
        auth.signInWithCredential(credential)
                .addOnSuccessListener(result -> callback.onSuccess(result.getUser()))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void registerWithEmail(String name, String email, String password, String phone, boolean organizer, DataCallback<FirebaseUser> callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener((AuthResult result) -> {
                    FirebaseUser user = result.getUser();
                    if (user == null) {
                        callback.onError("Registration failed");
                        return;
                    }
                    String role = organizer ? AppConstants.ROLE_ORGANIZER : AppConstants.ROLE_CUSTOMER;
                    String status = organizer ? AppConstants.STATUS_PENDING : AppConstants.STATUS_APPROVED;
                    UserProfile profile = new UserProfile(user.getUid(), name, email, phone, role, status, System.currentTimeMillis());
                    saveUserProfile(profile, new SimpleCallback() {
                        @Override
                        public void onSuccess() {
                            callback.onSuccess(user);
                        }

                        @Override
                        public void onError(String error) {
                            callback.onError(error);
                        }
                    });
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void saveUserProfile(UserProfile profile, SimpleCallback callback) {
        db.collection(AppConstants.USERS)
                .document(profile.getUid())
                .set(profile)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void upsertPhoneUser(FirebaseUser firebaseUser, String phoneNumber, String displayName, DataCallback<UserProfile> callback) {
        if (firebaseUser == null) {
            callback.onError("No authenticated user");
            return;
        }
        String uid = firebaseUser.getUid();
        db.collection(AppConstants.USERS).document(uid).get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        UserProfile profile = snapshot.toObject(UserProfile.class);
                        callback.onSuccess(profile);
                    } else {
                        String name = displayName == null || displayName.trim().isEmpty() ? "Phone User" : displayName;
                        UserProfile profile = new UserProfile(uid, name, firebaseUser.getEmail(), phoneNumber, AppConstants.ROLE_CUSTOMER, AppConstants.STATUS_APPROVED, System.currentTimeMillis());
                        saveUserProfile(profile, new SimpleCallback() {
                            @Override
                            public void onSuccess() {
                                callback.onSuccess(profile);
                            }

                            @Override
                            public void onError(String error) {
                                callback.onError(error);
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void fetchCurrentProfile(DataCallback<UserProfile> callback) {
        String uid = getCurrentUid();
        if (uid == null) {
            callback.onError("Not authenticated");
            return;
        }
        db.collection(AppConstants.USERS).document(uid).get()
                .addOnSuccessListener(snapshot -> callback.onSuccess(snapshot.toObject(UserProfile.class)))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void bootstrapAdmin(String adminEmail, String adminPhone, SimpleCallback callback) {
        db.collection(AppConstants.USERS)
                .whereEqualTo("email", adminEmail)
                .limit(1)
                .get()
                .addOnSuccessListener(query -> {
                    if (query.isEmpty()) {
                        callback.onSuccess();
                        return;
                    }
                    DocumentSnapshot snapshot = query.getDocuments().get(0);
                    UserProfile profile = snapshot.toObject(UserProfile.class);
                    if (profile == null) {
                        callback.onSuccess();
                        return;
                    }
                    profile.setRole(AppConstants.ROLE_ADMIN);
                    profile.setStatus(AppConstants.STATUS_APPROVED);
                    if (profile.getPhone() == null || profile.getPhone().isEmpty()) {
                        profile.setPhone(adminPhone);
                    }
                    db.collection(AppConstants.USERS).document(profile.getUid()).set(profile)
                            .addOnSuccessListener(unused -> callback.onSuccess())
                            .addOnFailureListener(e -> callback.onError(e.getMessage()));
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void fetchEvents(boolean activeOnly, ListCallback<EventItem> callback) {
        Task<com.google.firebase.firestore.QuerySnapshot> task;
        if (activeOnly) {
            task = db.collection(AppConstants.EVENTS).whereEqualTo("status", AppConstants.STATUS_ACTIVE).get();
        } else {
            task = db.collection(AppConstants.EVENTS).get();
        }
        task.addOnSuccessListener(query -> {
            List<EventItem> items = new ArrayList<>();
            for (QueryDocumentSnapshot doc : query) {
                EventItem item = doc.toObject(EventItem.class);
                item.setId(doc.getId());
                items.add(item);
            }
            callback.onSuccess(items);
        }).addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void fetchManageableEvents(UserProfile profile, ListCallback<EventItem> callback) {
        if (profile == null) {
            callback.onError("User profile missing");
            return;
        }
        Task<com.google.firebase.firestore.QuerySnapshot> task;
        if (AppConstants.ROLE_ADMIN.equals(profile.getRole())) {
            task = db.collection(AppConstants.EVENTS).get();
        } else {
            task = db.collection(AppConstants.EVENTS).whereEqualTo("organizerId", profile.getUid()).get();
        }
        task.addOnSuccessListener(query -> {
            List<EventItem> items = new ArrayList<>();
            for (QueryDocumentSnapshot doc : query) {
                EventItem item = doc.toObject(EventItem.class);
                item.setId(doc.getId());
                items.add(item);
            }
            callback.onSuccess(items);
        }).addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void saveEvent(EventItem event, SimpleCallback callback) {
        if (event.getId() == null || event.getId().isEmpty()) {
            event.setCreatedAt(System.currentTimeMillis());
            event.setStatus(AppConstants.STATUS_ACTIVE);
            db.collection(AppConstants.EVENTS)
                    .add(event)
                    .addOnSuccessListener(unused -> callback.onSuccess())
                    .addOnFailureListener(e -> callback.onError(e.getMessage()));
        } else {
            db.collection(AppConstants.EVENTS)
                    .document(event.getId())
                    .set(event)
                    .addOnSuccessListener(unused -> callback.onSuccess())
                    .addOnFailureListener(e -> callback.onError(e.getMessage()));
        }
    }

    public void cancelEvent(String eventId, SimpleCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", AppConstants.STATUS_CANCELLED);
        db.collection(AppConstants.EVENTS)
                .document(eventId)
                .update(updates)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void reserveEvent(@NonNull EventItem event, int tickets, SimpleCallback callback) {
        String uid = getCurrentUid();
        if (uid == null) {
            callback.onError("Not authenticated");
            return;
        }
        DocumentReference eventRef = db.collection(AppConstants.EVENTS).document(event.getId());
        DocumentReference reservationRef = db.collection(AppConstants.RESERVATIONS).document();
        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(eventRef);
            EventItem fresh = snapshot.toObject(EventItem.class);
            if (fresh == null) {
                throw new IllegalStateException("Event missing");
            }
            int available = fresh.getAvailableTickets();
            if (available < tickets) {
                throw new IllegalStateException("Not enough tickets");
            }
            Map<String, Object> eventUpdates = new HashMap<>();
            eventUpdates.put("availableTickets", available - tickets);
            transaction.update(eventRef, eventUpdates);

            ReservationItem reservation = new ReservationItem(
                    reservationRef.getId(),
                    uid,
                    event.getId(),
                    event.getTitle(),
                    event.getLocation(),
                    event.getDateTimeMillis(),
                    tickets,
                    AppConstants.STATUS_ACTIVE,
                    System.currentTimeMillis()
            );
            transaction.set(reservationRef, reservation);
            return reservationRef.getId();
        }).addOnSuccessListener(id -> {
            sendConfirmation(id, "reserved");
            callback.onSuccess();
        }).addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void fetchReservations(String uid, ListCallback<ReservationItem> callback) {
        db.collection(AppConstants.RESERVATIONS)
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener(query -> {
                    List<ReservationItem> items = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : query) {
                        ReservationItem item = doc.toObject(ReservationItem.class);
                        item.setId(doc.getId());
                        items.add(item);
                    }
                    callback.onSuccess(items);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void cancelReservation(ReservationItem reservation, SimpleCallback callback) {
        DocumentReference reservationRef = db.collection(AppConstants.RESERVATIONS).document(reservation.getId());
        DocumentReference eventRef = db.collection(AppConstants.EVENTS).document(reservation.getEventId());
        db.runTransaction(transaction -> {
            DocumentSnapshot reservationSnapshot = transaction.get(reservationRef);
            ReservationItem liveReservation = reservationSnapshot.toObject(ReservationItem.class);
            if (liveReservation == null) {
                throw new IllegalStateException("Reservation missing");
            }
            if (AppConstants.STATUS_CANCELLED.equals(liveReservation.getStatus())) {
                return reservation.getId();
            }
            DocumentSnapshot eventSnapshot = transaction.get(eventRef);
            EventItem event = eventSnapshot.toObject(EventItem.class);
            if (event != null) {
                int newAvailable = event.getAvailableTickets() + liveReservation.getTickets();
                transaction.update(eventRef, "availableTickets", newAvailable);
            }
            transaction.update(reservationRef, "status", AppConstants.STATUS_CANCELLED, "cancelledAt", FieldValue.serverTimestamp());
            return reservation.getId();
        }).addOnSuccessListener(id -> {
            sendConfirmation(id, "cancelled");
            callback.onSuccess();
        }).addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void fetchPendingOrganizers(ListCallback<UserProfile> callback) {
        db.collection(AppConstants.USERS)
                .whereEqualTo("role", AppConstants.ROLE_ORGANIZER)
                .whereEqualTo("status", AppConstants.STATUS_PENDING)
                .get()
                .addOnSuccessListener(query -> {
                    List<UserProfile> items = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : query) {
                        UserProfile profile = doc.toObject(UserProfile.class);
                        items.add(profile);
                    }
                    callback.onSuccess(items);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void updateOrganizerStatus(String uid, String status, SimpleCallback callback) {
        db.collection(AppConstants.USERS)
                .document(uid)
                .update("status", status)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    private void sendConfirmation(String reservationId, String action) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("reservationId", reservationId);
        payload.put("action", action);
        functions.getHttpsCallable("sendConfirmation").call(payload);
    }
}
