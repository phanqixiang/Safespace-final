package com.example.safespace.user

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import com.example.safespace.R
import com.example.safespace.databinding.ActivityReportBinding
import com.example.safespace.entity.Reports
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException


class ReportActivity : AppCompatActivity() {

    private lateinit var binding : ActivityReportBinding
    private val PICK_IMAGE_REQUEST = 71
    private var imagePath: Uri? = null
    private lateinit var dRef : DatabaseReference
    private lateinit var sRef : StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        sRef = FirebaseStorage.getInstance().getReference("reports")
        dRef = FirebaseDatabase.getInstance().getReference("reports")

        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_report
        )

        binding.choosePhoto.setOnClickListener{ chooseImage() }
        binding.submitButton.setOnClickListener{ uploadData() }
        binding.reportToolbar.setNavigationOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_host, ReportFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
        }
    }

    private fun chooseImage(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            imagePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imagePath)
                binding.imgView.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadData() = if(imagePath != null){
        val ref = sRef.child(System.currentTimeMillis().toString() + "." +
        getFileExtension(imagePath!!))

        ref.putFile(imagePath!!)
            .addOnSuccessListener {
                Handler().postDelayed({ doNtg() }, 300)
                //Toast.makeText(this, "Upload Successful", Toast.LENGTH_SHORT).show()
                val downUri = it.storage.downloadUrl.toString().trim()
                val reports = Reports(binding.spinner.selectedItem.toString().trim(), binding.addressText.text.toString().trim(),
                    downUri, binding.abuseText.text.toString().trim(), binding.victimText.text.toString().trim())
                val rID = dRef.push().key.toString()

                dRef.child(rID).setValue(reports).addOnCompleteListener {
                    Toast.makeText(this, "Upload Successful", Toast.LENGTH_SHORT).show()
                    binding.apply {
                        addressText.text.clear()
                        imgView.setImageResource(0)
                        abuseText.text.clear()
                        victimText.text.clear()
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
            .addOnProgressListener {
                val progress = (100.0 * it.bytesTransferred / it.totalByteCount)

            }
    }else{
        Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
    }

    private fun getFileExtension(uri: Uri): String? {
        val cR : ContentResolver = contentResolver
        val mime : MimeTypeMap = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))
    }

    private fun doNtg(){

    }

}