package com.example.superwolffirebase.api

import android.net.Uri
import com.example.superwolffirebase.models.Player
import com.example.superwolffirebase.other.Resource
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.util.Date
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SetUpProfileImpl @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    private val firebaseDatabase: FirebaseDatabase
) : SetUpProfile {

    override suspend fun uploadProfile(
        avatar: Uri,
        id: String,
        name: String,
        gender: String,
        email: String,
        role: String,
        status: String
    ): Resource<DatabaseReference> {
        return suspendCoroutine {continuation ->
            val date = Date().time.toString()
            val reference = firebaseStorage.reference.child("avatars").child(date)
            val uploadTask: UploadTask = reference.putFile(avatar)


            uploadTask.addOnFailureListener{
                continuation.resumeWithException(it)
            } .addOnSuccessListener {
                it.task.continueWithTask(com.google.android.gms.tasks.Continuation<UploadTask.TaskSnapshot, Task<Uri>>{task->

                    if (task.isSuccessful){
                        task.exception?.let {exception ->
                            continuation.resumeWithException(exception)
                        }
                    }

                    return@Continuation reference.downloadUrl
                }).addOnCompleteListener {uri->
                    if (uri.isSuccessful){
                        val downloadUri = uri.result.toString()
                        val player = Player(id, name, downloadUri, gender, email, role, status)

                        firebaseDatabase.reference.child("players")
                            .child(id)
                            .setValue(player)
                            .addOnCompleteListener {
                                continuation.resume(Resource.Success(firebaseDatabase.reference))
                            }
                            .addOnFailureListener { exception->
                                continuation.resume(Resource.Error(exception))
                            }

                    }
                }
            }
        }
    }
}