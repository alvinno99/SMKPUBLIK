package com.alk84.smkpublik

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.app.ProgressDialog.show
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import com.alk84.smkpublik.R
import utils.Preferences
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_sign_up_photoscreen.*
import java.io.File
import java.io.IOException
import java.util.*


class SignUpPhotoscreenActivity : AppCompatActivity(),  PermissionListener{

    val REQUEST_IMAGE_CAPTURE = 1
    var statusAdd:Boolean = false
    lateinit var filePath: Uri

    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference
    lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_photoscreen)


        preferences = Preferences(this)
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        tv_hello.text = "Selamat Datang Di SMK PUBLIK\n"+intent.getStringExtra("nama")

        iv_add.setOnClickListener {
            if (statusAdd) {
                statusAdd = false
                btn_save.visibility = View.INVISIBLE
                iv_add.setImageResource(R.drawable.ic_btn_upload)
                iv_profile.setImageResource(R.drawable.user_pic)

            } else {
                Dexter.withActivity(this)
                    .withPermission(Manifest.permission.CAMERA)
                    .withListener(this)
                    .check()
            }
        }
        btn_home.setOnClickListener {

            finishAffinity()

            val intent = Intent(this@SignUpPhotoscreenActivity,
                HomeActivity::class.java)
            startActivity(intent)
        }

        btn_save.setOnClickListener {
            if (filePath != null) {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Uploading...")
                progressDialog.show()

                Log.v("tamvan", "file uri upload 2"+filePath)

                val ref = storageReference.child("images/" + UUID.randomUUID().toString())
                ref.putFile(filePath)
                    .addOnSuccessListener {
                        progressDialog.dismiss()
                        Toast.makeText(this@SignUpPhotoscreenActivity, "Uploaded", Toast.LENGTH_SHORT).show()

                        ref.downloadUrl.addOnSuccessListener {
                            preferences.setValues("url", it.toString())
                        }

                            finishAffinity()
                            val intent = Intent(this@SignUpPhotoscreenActivity,
                                HomeActivity::class.java)
                            startActivity(intent)

                    }
                    .addOnFailureListener { e ->
                        progressDialog.dismiss()
                        Toast.makeText(this@SignUpPhotoscreenActivity, "Failed " + e.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                    .addOnProgressListener { taskSnapshot ->
                        val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                            .totalByteCount
                        progressDialog.setMessage("Uploaded " + progress.toInt() + "%")
                    }
            }

        }

    }

    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
        //To change body of created functions use File | Settings | File Templates.
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }

    }

    override fun onPermissionRationaleShouldBeShown(
        permission: com.karumi.dexter.listener.PermissionRequest?,
        token: PermissionToken?
    ) {
        //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
        //To change body of created functions use File | Settings | File Templates.
        Toast.makeText(this, "Anda tidak bisa menambahkan photo profile", Toast.LENGTH_LONG ).show()
    }

    override fun onBackPressed() {
        Toast.makeText(this, "Tergesah? Klik tombol Upload Nanti aja", Toast.LENGTH_LONG ).show()
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            var bitmap = data?.extras?.get("data") as Bitmap
            statusAdd = true

            filePath = data.getData()!!
            Glide.with(this)
                .load(bitmap)
                .apply(RequestOptions.circleCropTransform())
                .into(iv_profile)

            btn_save.visibility = View.VISIBLE
            iv_add.setImageResource(R.drawable.ic_btn_delete)
        }
    }
}

