package com.reiserx.myapplication24.Models;

import com.google.firebase.database.FirebaseDatabase;

public class uploadTask {
    String Path, storagePath, UserID, fileUrl;
    uploadModel uploadModel;

    public uploadTask(String path, String storagePath, String userID, String fileUrl) {
        Path = path;
        this.storagePath = storagePath;
        UserID = userID;
        this.fileUrl = fileUrl;
        uploadModel = new uploadModel(path, storagePath, fileUrl);
    }

    public void uploadData () {
        FirebaseDatabase.getInstance().getReference().child("Main").child(UserID).child("UploadedFiles").push().setValue(uploadModel);
    }
}
