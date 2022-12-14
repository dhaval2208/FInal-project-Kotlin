// Name :- Dhaval Bhimani Zaverbhai
// Student id :- A00255187
// Course :- JAV-1001 - 91337 - App Development for Android - 202209 - 001


@file:Suppress("DEPRECATION")

package com.dhaval.contacts

import android.Manifest
import android.content.ContentProviderOperation
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.nfc.Tag
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.InputBinding
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dhaval.contacts.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
     private lateinit var binding: ActivityMainBinding

     private val TAG = "CONTACT_ADD_TAG"

    private lateinit var contactPermissions: Array<String>

    private val WRITE_CONTACT_PERMISSION_CODE =100

    private val IMAGE_PICK_GALLERY_CODE =200

    private var image_uri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contactPermissions = arrayOf(Manifest.permission.WRITE_CONTACTS)

        binding.profile.setOnClickListener{
            openGalleryIntent()
        }

        binding.SaveNumber.setOnClickListener{
          if(isWriteContactPermissionEnable()){
              saveContact()
          }
            else{
                requestWriteContactPermission()
          }
        }
    }
private fun saveContact(){
    Log.d(TAG, "saveContact")
}
    private fun isWriteContactPermissionEnable(): Boolean{
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)== PackageManager.PERMISSION_DENIED
    }

    private fun requestWriteContactPermission(){
        ActivityCompat.requestPermissions(this , contactPermissions, WRITE_CONTACT_PERMISSION_CODE)
    }
    private fun openGalleryIntent() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty()) {
            if (requestCode == WRITE_CONTACT_PERMISSION_CODE) {
                val haveWriteContactPermission = grantResults[0] == PackageManager.PERMISSION_DENIED
                if(haveWriteContactPermission){
                    saveContact()
                }
                else{
                    Toast.makeText(this, "Permission deneid" , Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                image_uri = data!!.data
                binding.profile.setImageURI(image_uri)
            }
        }
        else{
            Toast.makeText(this, "cancelled" , Toast.LENGTH_SHORT).show()
        }
    }

}