package com.quartzodev.data;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quartzodev.utils.DateUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by victoraldir on 29/03/2017.
 */

public class FirebaseDatabaseHelper {

    public static final String MAX_FOLDERS_KEY = "max_folders";
    public static final String MAX_BOOKS_KEY = "max_books";
    //    public static final int TOTAL_BOOKS_FOLDER_TIER = 25;
//    public static final int TOTAL_FOLDER_TIER = 4;
    public static final String REF_POPULAR_FOLDER = "_popularBooks"; //See a better way to maintain popular folder
    public static final String REF_MY_BOOKS_FOLDER = "myBooksFolder";
    public static final String REF_SEARCH_HISTORY = "search_history"; //See a better way to maintain popular folder
    private static final String TAG = FirebaseDatabaseHelper.class.getSimpleName();
    private static final String ROOT = "users";
    private static final String REF_FOLDERS = "folders";
    private static final String REF_BOOKS = "books";
    private static FirebaseDatabaseHelper mInstance;
    private DatabaseReference mDatabaseReference;


    private FirebaseDatabaseHelper() {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseDatabase.setPersistenceEnabled(true);
        mDatabaseReference = mFirebaseDatabase.getReference().child(ROOT);
        mDatabaseReference.keepSynced(true);

    }

    public static FirebaseDatabaseHelper getInstance() {
        if (mInstance == null) mInstance = new FirebaseDatabaseHelper();

        return mInstance;
    }

    public void insertUser(User user, DatabaseReference.CompletionListener completionListener) {
        mDatabaseReference.updateChildren(Collections.singletonMap(user.getUid(), (Object) user), completionListener);
    }

    public void updateUserLastActivity(String userId) {
        Map<String, Object> mapLastActivity = new HashMap<>();
        mapLastActivity.put("lastActivity", DateUtils.getCurrentTimeString());
        updateUser(userId, mapLastActivity);
    }

    public void updateUser(String userId, Map<String, Object> fields) {
        mDatabaseReference.child(userId).updateChildren(fields);
    }

    public void fetchPopularBooks(final OnDataSnapshotListener onDataSnapshotListener) {

        mDatabaseReference.child(REF_POPULAR_FOLDER)
                .addListenerForSingleValueEvent(buildValueEventListener(onDataSnapshotListener));
    }

    public void insertPopularBooks(Map<String, Book> books) {

        Folder folder = new Folder("Popular Books Folder");

        folder.setBooks(books);

        mDatabaseReference.child(REF_POPULAR_FOLDER).setValue(folder);

    }

    public void insertBookSearchHistory(String userId, Book book) {

        mDatabaseReference.child(userId).child(REF_SEARCH_HISTORY).updateChildren(Collections.singletonMap(book.getId(), (Object) book));

    }

    public void fetchMyBooksFolder(String userId, final OnDataSnapshotListener onDataSnapshotListener) {

        mDatabaseReference.child(userId).child(REF_FOLDERS).child(REF_MY_BOOKS_FOLDER)
                .addListenerForSingleValueEvent(buildValueEventListener(onDataSnapshotListener));

    }

    public ChildEventListener attachBookFolderChildEventListener(String userId, String folderId, ChildEventListener listener) {
        return mDatabaseReference.child(userId).child(REF_FOLDERS).child(folderId).child(REF_BOOKS).addChildEventListener(listener);
    }

    public void detachBookFolderChildEventListener(String userId, String folderId, ChildEventListener listener) {
        mDatabaseReference.child(userId).child(REF_FOLDERS).child(folderId).child(REF_BOOKS).removeEventListener(listener);
    }

    public void fetchBooksFromFolder(String userId, String folderId, final OnDataSnapshotListener onDataSnapshotListener) {

        mDatabaseReference.child(userId).child(REF_FOLDERS).child(folderId)
                .addListenerForSingleValueEvent(buildValueEventListener(onDataSnapshotListener));

    }

    public ValueEventListener fetchLentBooks(String userId, final ValueEventListener valueEventListener) {

        return mDatabaseReference.child(userId).child(REF_FOLDERS).child(REF_MY_BOOKS_FOLDER)
                .addValueEventListener(valueEventListener);

    }

    public void findBook(String userId, String folderId, String bookId, final OnDataSnapshotListener onDataSnapshotListener) {

        if (folderId == null) {

            mDatabaseReference.child(userId).child(REF_SEARCH_HISTORY).child(bookId).addListenerForSingleValueEvent(buildValueEventListener(onDataSnapshotListener));

        } else if (folderId.equals(REF_POPULAR_FOLDER)) {

            mDatabaseReference.child(REF_POPULAR_FOLDER).child("books").child(bookId)
                    .addListenerForSingleValueEvent(buildValueEventListener(onDataSnapshotListener));

        } else {

            mDatabaseReference.child(userId).child(REF_FOLDERS).child(folderId).child("books").child(bookId)
                    .addListenerForSingleValueEvent(buildValueEventListener(onDataSnapshotListener));

        }

    }

    public void findBookSearch(String userId, String folderId, String bookQuery, final OnDataSnapshotListener onDataSnapshotListener) {

        if (folderId == null) {

            mDatabaseReference.child(userId)
                    .child(REF_FOLDERS)
                    .child(REF_MY_BOOKS_FOLDER)
                    .child(REF_BOOKS)
                    .orderByChild("volumeInfo/searchField")
//                    .startAt(bookQuery)
//                    .endAt(bookQuery+"\uf8ff")
//                    .limitToFirst(5)
                    .addListenerForSingleValueEvent(buildValueEventListener(onDataSnapshotListener));

        } else {

            mDatabaseReference.child(userId)
                    .child(REF_FOLDERS)
                    .child(folderId)
                    .child(REF_BOOKS)
                    .orderByChild("volumeInfo/searchField")
//                    .startAt(bookQuery)
//                    .endAt(bookQuery+"\uf8ff")
//                    .limitToFirst(5)
                    .addListenerForSingleValueEvent(buildValueEventListener(onDataSnapshotListener));

        }

    }

    public void fetchFolders(String userId, final OnDataSnapshotListener onDataSnapshotListener) {

        DatabaseReference ref = mDatabaseReference.child(userId).child(REF_FOLDERS);
        ref.addListenerForSingleValueEvent(buildValueEventListener(onDataSnapshotListener));

    }

    public void attachFetchFolders(String userId, ChildEventListener listener) {
        DatabaseReference ref = mDatabaseReference.child(userId).child(REF_FOLDERS);
        ref.addChildEventListener(listener);
    }

    public void detachFetchFolders(String userId, ChildEventListener listener) {
        DatabaseReference ref = mDatabaseReference.child(userId).child(REF_FOLDERS);
        ref.removeEventListener(listener);
    }


    public void deleteFolder(String userId, String folderId) {
        mDatabaseReference.child(userId).child(REF_FOLDERS).child(folderId).removeValue();
    }

    public void insertFolder(final String userId, final Folder folder) {

        mDatabaseReference.child(userId).child(REF_FOLDERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                DatabaseReference df = mDatabaseReference.child(userId).child(REF_FOLDERS).push();
                folder.setId(df.getKey());
                folder.setCustom(true);
                df.setValue(folder);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void insertDefaulFolder(String userId, String description, DatabaseReference.CompletionListener completionListener) {
        DatabaseReference df = mDatabaseReference.child(userId).child(REF_FOLDERS).child(REF_MY_BOOKS_FOLDER);

        Folder myBooksFolder = new Folder();
        myBooksFolder.setId(UUID.randomUUID().toString());
        myBooksFolder.setDescription(description);
        myBooksFolder.setCustom(false);

        df.setValue(myBooksFolder, completionListener);

    }

    public void updateBook(String userId, String folderId, Book Book) {
        mDatabaseReference.child(userId).child(REF_FOLDERS).child(folderId).child(REF_BOOKS).updateChildren(Collections.singletonMap(Book.getId(), (Object) Book));
    }

    public void attachUpdateBookChildListener(String userId, String folderId, Book Book, ValueEventListener listener) {
        mDatabaseReference.child(userId).child(REF_FOLDERS).child(folderId).child(REF_BOOKS).child(Book.getId()).addValueEventListener(listener);
    }

    public void detachUpdateBookChildListener(String userId, String folderId, Book Book, ValueEventListener listener) {
        mDatabaseReference.child(userId).child(REF_FOLDERS).child(folderId).child(REF_BOOKS).child(Book.getId()).addValueEventListener(listener);
    }

    public void insertBookFolder(String userId, String folderId, final Book book) {

        final DatabaseReference ref = mDatabaseReference
                .child(userId)
                .child(REF_FOLDERS)
                .child(folderId)
                .child("books");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(book.getId() == null ){ //If it's custom
                    book.setId(ref.push().getKey());
                }

                ref.updateChildren(Collections.singletonMap(book.getId(), (Object) book));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void deleteBookFolder(String userId, String folderId, Book book) {
        mDatabaseReference.child(userId).child(REF_FOLDERS).child(folderId).child(REF_BOOKS).child(book.getId()).removeValue();
    }

    public void fetchUserById(String userId, final OnDataSnapshotListener onDataSnapshotListener) {

        mDatabaseReference.child(userId)
                .addListenerForSingleValueEvent(buildValueEventListener(onDataSnapshotListener));

    }

    private ValueEventListener buildValueEventListener(final OnDataSnapshotListener onDataSnapshotListener) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    onDataSnapshotListener.onDataSnapshotListenerAvailable(dataSnapshot);
                    notifyCaller(onDataSnapshotListener);
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO create method on OnDataSnapshotListener to deal with this
                Log.e(TAG, databaseError.getDetails());
            }
        };
    }

    private void notifyCaller(final OnDataSnapshotListener onDataSnapshotListener) {
        synchronized (onDataSnapshotListener) {
            onDataSnapshotListener.notify(); //Release Task
        }
    }

    /**
     * This interface must be implemented by classes that want
     * to load DataSnapshot from this util
     */
    public interface OnDataSnapshotListener {
        void onDataSnapshotListenerAvailable(DataSnapshot dataSnapshot);
    }

}
