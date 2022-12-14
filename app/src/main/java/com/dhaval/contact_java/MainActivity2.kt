package com.dhaval.contact_java

import android.Manifest
import android.content.ContentProviderOperation
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dhaval.contact_java.databinding.ActivityMain2Binding
import com.dhaval.contact_java.databinding.ActivityMainBinding
import java.io.ByteArrayOutputStream

class MainActivity2 : AppCompatActivity() {

        private lateinit var binding: ActivityMain2Binding
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
            binding = ActivityMain2Binding.inflate(layoutInflater)
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
            val firstname = binding.FirstName.text.toString().trim()
            val lastname = binding.LastName.text.toString().trim()
            val phonemobile = binding.PhoneMobile.text.toString().trim()
            val phonehome = binding.PhoneHome.text.toString().trim()
            val email = binding.Email.text.toString().trim()
            val address = binding.Address.text.toString().trim()
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
            Contacts.add(
                ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE , null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME , null)
                .build())
// add to first name and last name
            Contacts.add(
                ContentProviderOperation.newInsert(
                ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactId)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, firstname)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, lastname)
                .build())

            // add to Phone mobile number
            Contacts.add(
                ContentProviderOperation.newInsert(
                ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactId)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, firstname)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build())
//  add to phone Home Number
            Contacts.add(
                ContentProviderOperation.newInsert(
                ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactId)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phonehome)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                .build())

// add email
            Contacts.add(
                ContentProviderOperation.newInsert(
                ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactId)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.DATA, email)
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .build())
// add Address
            Contacts.add(
                ContentProviderOperation.newInsert(
                ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactId)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.DATA, address)
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME)
                .build())

            // get image as bytes as contact image

            val imageBytes = imageUriToBytes()
            if (imageBytes != null) {
                // contact with image
                Log.d(TAG, "saveContact: contact with image")

                // add image
                Contacts.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactId)
                    .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO,imageBytes).build())
            }
            else{
                // contact without image
                Log.d(TAG,"saveContact: contact without image")
            }
// save contact
            try {
                contentResolver.applyBatch(ContactsContract.AUTHORITY, Contacts)
                Log.d(TAG, "saveContact:Saved")
                Toast.makeText(this, "Saved" , Toast.LENGTH_SHORT).show()
            }
            catch (e: Exception) {
                Log.d(TAG, "saveContact: failed to save due to ${e.message}")
                Toast.makeText(this, "saveContact: failed to save due to ${e.message}" , Toast.LENGTH_SHORT).show()
            }
        }
        private fun imageUriToBytes(): ByteArray? {
            val bitmap: Bitmap
            val baos: ByteArrayOutputStream?

            return try {
                if (Build.VERSION.SDK_INT < 28){
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, image_uri)
                }
                else{
                    val source = ImageDecoder.createSource(contentResolver, image_uri!!)
                    bitmap = ImageDecoder.decodeBitmap(source)
                }
                baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50,baos)
                baos.toByteArray()
            }catch (e: Exception) {
                Log.d(TAG, "imageUriToBytes: ${e.message}")
                null
            }
        }

        private fun isWriteContactPermissionEnable(): Boolean{
            return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)== PackageManager.PERMISSION_GRANTED
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
                        Toast.makeText(this, "Permission denied" , Toast.LENGTH_SHORT).show()
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

