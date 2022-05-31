package com.example.bikerx.control.firestore;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Parent control class for querying and updating Firebase Firestore.
 */
public class DBManager {
    private FirebaseFirestore db;

    public DBManager() {
        db = FirebaseFirestore.getInstance();
    }

    protected void queryOrderedCollection(String collection, OnCompleteListener<QuerySnapshot> listener, String orderKey, boolean descendingOrder, int limitNumber) {
        if (limitNumber <= 0) {
            if (descendingOrder) db.collection(collection).orderBy(orderKey, Query.Direction.DESCENDING).get().addOnCompleteListener(listener);
            else db.collection(collection).orderBy(orderKey, Query.Direction.ASCENDING).get().addOnCompleteListener(listener);
        } else {
            if (descendingOrder) db.collection(collection).orderBy(orderKey, Query.Direction.DESCENDING).limit(limitNumber).get().addOnCompleteListener(listener);
            else db.collection(collection).orderBy(orderKey, Query.Direction.ASCENDING).limit(limitNumber).get().addOnCompleteListener(listener);
        }
    }

    protected void queryDocument(String collection, String documentId, OnCompleteListener<DocumentSnapshot> listener) {
        db.collection(collection).document(documentId).get().addOnCompleteListener(listener);
    }

    protected void updateDocument(String collection, String documentId, String field, Object value) {
        db.collection(collection).document(documentId).update(field, value);
    }

    protected void addEntry(String collection, String documentId, Object data) {
        db.collection(collection).document(documentId).set(data);
    }

}
