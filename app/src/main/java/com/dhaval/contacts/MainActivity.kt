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
import javax.sql.CommonDataSource

class MainActivity : AppCompatActivity() {
     private lateinit var binding: ActivityMainBinding
//TAG
     private val TAG = "CONTACT_ADD_TAG"
// Array permisssion
    private lateinit var contactPermissions: Array<String>
// request for Write contacts permission
    private val WRITE_CONTACT_PERMISSION_CODE =100
// request for image pick gallery
    private val IMAGE_PICK_GALLERY_CODE =200

    private var image_uri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
// init permission array to write contacts
        contactPermissions = arrayOf(Manifest.permission.WRITE_CONTACTS)
// pick image from gallery
        binding.profile.setOnClickListener{
            openGalleryIntent()
        }
// click to save contacts
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
// define variables
    val firstname = binding.FirstName.text.trim()
    val lastname = binding.LastName.text.trim()
    val phonemobile = binding.PhoneMobile.text.trim()
    val phonehome = binding.PhoneHome.text.trim()
    val email = binding.Email.text.trim()
    val address = binding.Address.text.trim()
// input data
    Log.d(TAG, "SaveContact: FirstName $firstname")
    Log.d(TAG, "SaveContact: Last Name $lastname")
    Log.d(TAG, "SaveContact: Phone Mobile $phonemobile")
    Log.d(TAG, "SaveContact: Phone Home $phonehome")
    Log.d(TAG, "SaveContact: Email $email")
    Log.d(TAG, "SaveContact: Address $address")
// init array of object ContentProviderOperation
    val Contacts = ArrayList<ContentProviderOperation>()
// add to contact id
    val rawContactId = Contacts.size
    Contacts.add(ContentProviderOperation.newInsert(
        ContactsContract.RawContacts.CONTENT_URI)
        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE , null)
        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME , null)
        .build())
// add to first name and last name
    Contacts.add(ContentProviderOperation.newInsert(
        ContactsContract.Data.CONTENT_URI)
        .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactId)
        .withValue(ContactsContract.RawContacts.Data.MIMETYPE,ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
        .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, firstname)
        .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, lastname)
        .build())


}

    private fun isWriteContactPermissionEnable(): Boolean{
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)== PackageManager.PERMISSION_DENIED
    }

    private fun requestWriteContactPermission(){
        ActivityCompat.requestPermissions(this , contactPermissions, WRITE_CONTACT_PERMISSION_CODE)
    }

    // pick image from gallery
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
        // handle permission request result
        if (grantResults.isNotEmpty()) {
            if (requestCode == WRITE_CONTACT_PERMISSION_CODE) {
                val haveWriteContactPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if(haveWriteContactPermission){
                    // permission granted save contact
                    saveContact()
                }
                else{
                    // permission denied , can't save contact
                    Toast.makeText(this, "Permission deneid" , Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
// handle image pick result
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                image_uri = data!!.data
                binding.profile.setImageURI(image_uri)
            }
        }
        // canclled
        else{
            Toast.makeText(this, "cancelled" , Toast.LENGTH_SHORT).show()
        }
    }

}