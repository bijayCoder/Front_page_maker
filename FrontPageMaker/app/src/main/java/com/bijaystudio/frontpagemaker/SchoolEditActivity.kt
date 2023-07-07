package com.bijaystudio.frontpagemaker

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.font.PDType0Font
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject
import java.io.ByteArrayOutputStream
import java.io.File

class SchoolEditActivity : AppCompatActivity() {

    private val arrIdOfPdfSchool = arrayOf(R.raw.school_temp_one_pdf , R.raw.school_temp_two_pdf , R.raw.school_temp_three_pdf , R.raw.school_temp_four_pdf)
    @SuppressLint("SdCardPath")
    var directory = File("/sdcard/Documents/Front_Page_Maker/")
    private var folderPath = Environment.getDataDirectory().absolutePath + "/storage/emulated/0/Documents/Front_Page_Maker"
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    var activityResultLauncher: ActivityResultLauncher<String>? = null
    private lateinit var edtName : EditText
    private lateinit var edtClass : EditText
    private lateinit var edtSection : EditText
    private lateinit var edtRollNo : EditText
    private lateinit var edtSubject : EditText
    private lateinit var edtSession : EditText
    private lateinit var edtSchoolName : EditText
    private lateinit var cbQrCode : CheckBox
    private lateinit var btnSubmit : AppCompatButton
    private lateinit var btnLogo : AppCompatButton
    private lateinit var ivLogo : ImageView
    private var name : String = ""
    private var myClass : String = ""
    private var section : String = ""
    private var rollNo : String = ""
    private var subject : String = ""
    private var session : String = ""
    private var schoolName : String = ""
    private var schoolLogoBitmap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_school_edit)
        PDFBoxResourceLoader.init(applicationContext);

        edtName = findViewById(R.id.edtNameForSchool)
        edtRollNo = findViewById(R.id.edtRollNoForSchool)
        edtSession = findViewById(R.id.edtSessionForSchool)
        edtClass = findViewById(R.id.edtClassForSchool)
        edtSection = findViewById(R.id.edtSectionForSchool)
        edtSchoolName = findViewById(R.id.edtSchoolName)
        edtSubject = findViewById(R.id.edtSubjectForSchool)
        cbQrCode = findViewById(R.id.cbQrCodeForSchool)
        btnSubmit = findViewById(R.id.btnSubmitForSchool)
        btnLogo = findViewById(R.id.btnLogoForSchool)
        ivLogo = findViewById(R.id.ivLogoForSchool)


        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                if (result.data!!.data != null) {
                    val image: Uri? = result.data!!.data
                    val filepathColumn = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME)
                    val cursor: Cursor? = contentResolver.query(image!!, filepathColumn, null, null)
                    cursor!!.moveToNext()
                    val filePath: Int = cursor.getColumnIndex(filepathColumn[0])
                    val path: String = cursor.getString(filePath)
                    cursor.close()
                    val file = File(path)
                    val bitmap = BitmapFactory.decodeFile(file.toString())
                    ivLogo.setImageBitmap(bitmap)
                    schoolLogoBitmap = bitmap
                    ivLogo.visibility = View.VISIBLE
                }
            }
        }
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->

        }

        btnSubmit.setOnClickListener {
            createPdfFromTemplate(intent.getIntExtra("templateIndex" , 0))
        }
        btnLogo.setOnClickListener {
            if (checkPermission()) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                resultLauncher.launch(intent)
            } else {
                requestPermission()
            }
        }

    }

    private fun createPdfFromTemplate(templateId : Int ) {
        name = edtName.text.toString()
        myClass = edtClass.text.toString()
        section = edtSection.text.toString()
        rollNo = edtRollNo.text.toString()
        session = edtSession.text.toString()
        subject = edtSubject.text.toString()
        schoolName = edtSchoolName.text.toString()
        val inputStream = this.resources.openRawResource(arrIdOfPdfSchool[templateId])
        val document = PDDocument.load(inputStream)

        when(templateId){
            0 ->
                tempFirst(document , name , myClass, section , rollNo , session , subject , schoolName)
            1 ->
                tempSecond(document , name , myClass, section , rollNo , session , subject , schoolName)
            2 ->
                tempThird(document , name , myClass, section , rollNo , session , subject , schoolName)
            3 ->
                tempFourth(document , name , myClass, section , rollNo , session , subject , schoolName)
        }
    }



    private fun tempFirst(document: PDDocument, name: String, myClass : String, section:String ,rollNo: String, session: String, subject: String , schoolName : String)  {
        //edit the pdf for first template
        val pdPage = document.getPage(0)
        val cs =  PDPageContentStream(document , pdPage , PDPageContentStream.AppendMode.APPEND , true)
        cs.beginText()
        val font = PDType0Font.load(document, assets.open("com/tom_roush/pdfbox/resources/ttf/LiberationSans-Regular.ttf"))
        cs.setLeading(16.0f) //gap between  two line if used
        cs.setFont(font , 25f)
        cs.setNonStrokingColor(0f,0f,0f)

        //name
        cs.newLineAtOffset(120f ,484.0f )
        cs.showText(name)

        //class
        cs.newLineAtOffset(0f , -42.5f)
        cs.showText(myClass)

        //section
        cs.newLineAtOffset(25f , -42.5f)
        cs.showText(section)

        //roll no
        cs.newLineAtOffset(5f , -42.5f)
        cs.showText(rollNo)

        //subject
        cs.newLineAtOffset(-6f , -42.5f)
        cs.showText(subject)

        //school name
        cs.setFont(font , 30f)
        val textWidth = (font.getStringWidth(schoolName.uppercase()) / 1000.0f) * 30f;
        cs.newLineAtOffset(145-(textWidth)/2f , 470f)
        cs.showText(schoolName.uppercase())
        cs.newLineAtOffset(textWidth/2f , 0f)

        //session
        cs.setFont(font , 27f)
        val textWidth2 = (font.getStringWidth(session) / 1000.0f) * 27f;
        cs.newLineAtOffset(0-(textWidth2)/2f , -55f)
        cs.showText(session)

        //contentStream close
        cs.endText()
        cs.close()
        //QRCode
        if(cbQrCode.isChecked){
            val bitmap = getBitmapOfQrText("Myself $name, I am from class $myClass, section $section and roll number $rollNo. I have completed an assignment in the subject of $subject.\n ThankYou!")
            val stream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val bitMapData = stream.toByteArray()
            val qrImage = PDImageXObject.createFromByteArray(document , bitMapData , "qrCode")
            val contentStreamForImage =  PDPageContentStream(document , pdPage , PDPageContentStream.AppendMode.APPEND , true)
            contentStreamForImage.drawImage(qrImage , 30.0f , 40.0f , 75f , 75f)
            contentStreamForImage.close()
        }
        //college logo
        if(schoolLogoBitmap != null){
            val stream = ByteArrayOutputStream()
            schoolLogoBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val bitMapData = stream.toByteArray()
            val qrImage = PDImageXObject.createFromByteArray(document , bitMapData , "collegeLogo")
            val contentStreamForImage =  PDPageContentStream(document , pdPage , PDPageContentStream.AppendMode.APPEND , true)
            contentStreamForImage.drawImage(qrImage , 40.0f , 620.0f , 85f , 85f)
            contentStreamForImage.close()
        }

        writePdfFile(document , "Front_page_maker-" + System.currentTimeMillis())
    }

    private fun tempSecond(document: PDDocument, name: String, myClass: String, section: String, rollNo: String, session: String, subject: String, schoolName: String) {

        //edit the pdf for second template
        val pdPage = document.getPage(0)
        val cs =  PDPageContentStream(document , pdPage , PDPageContentStream.AppendMode.APPEND , true)
        cs.beginText()
        val font = PDType0Font.load(document, assets.open("com/tom_roush/pdfbox/resources/ttf/LiberationSans-Regular.ttf"))
        cs.setLeading(16.0f) //gap between  two line if used
        cs.setFont(font , 25f)
        cs.setNonStrokingColor(0f,0f,0f)

        //name
        cs.newLineAtOffset(150f ,501f )
        cs.showText(name)

        //class
        cs.newLineAtOffset(0f , -68f)
        cs.showText(myClass)

        //section
        cs.newLineAtOffset(30f , -67f)
        cs.showText(section)

        //roll no
        cs.newLineAtOffset(0f , -67f)
        cs.showText(rollNo)

        //subject
        cs.newLineAtOffset(-6f , -65f)
        cs.showText(subject)
        //session
        cs.newLineAtOffset(0f , -71f)
        cs.showText(session)

        //school name
        cs.setFont(font , 30f)
        val textWidth = (font.getStringWidth(schoolName.uppercase()) / 1000.0f) * 30f;
        cs.newLineAtOffset(122-(textWidth)/2f , 585f)
        cs.showText(schoolName.uppercase())
        cs.newLineAtOffset(textWidth/2f , 0f)

        //add a underline to college name
        cs.endText()
        cs.setNonStrokingColor(0.25f , 0.25f ,0.1f)
        cs.addRect(297-(textWidth)/2f , 741f , textWidth , 2.5f)
        cs.fill()
        cs.close()

        //QRCode
        if(cbQrCode.isChecked){
            val bitmap = getBitmapOfQrText("Myself $name, I am from class $myClass, section $section" +
                    " and roll number $rollNo. I have completed an assignment in the subject of $subject.\n ThankYou!")
            val stream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val bitMapData = stream.toByteArray()
            val qrImage = PDImageXObject.createFromByteArray(document , bitMapData , "qrCode")
            val contentStreamForImage =  PDPageContentStream(document , pdPage , PDPageContentStream.AppendMode.APPEND , true)
            contentStreamForImage.drawImage(qrImage , 490.0f , 30.0f , 75f , 75f)
            contentStreamForImage.close()
        }
        //college logo
        if(schoolLogoBitmap != null){
            val stream = ByteArrayOutputStream()
            schoolLogoBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val bitMapData = stream.toByteArray()
            val qrImage = PDImageXObject.createFromByteArray(document , bitMapData , "collegeLogo")
            val contentStreamForImage =  PDPageContentStream(document , pdPage , PDPageContentStream.AppendMode.APPEND , true)
            contentStreamForImage.drawImage(qrImage , 246f , 600.0f , 100f , 100f)
            contentStreamForImage.close()
        }


        writePdfFile(document , "Front_page_maker-" + System.currentTimeMillis())

    }

    private fun tempThird(document: PDDocument, name: String, myClass: String, section: String, rollNo: String, session: String, subject: String, schoolName: String) {
        //edit the pdf for second template
        val pdPage = document.getPage(0)
        val cs =  PDPageContentStream(document , pdPage , PDPageContentStream.AppendMode.APPEND , true)
        cs.beginText()
        val font = PDType0Font.load(document, assets.open("com/tom_roush/pdfbox/resources/ttf/LiberationSans-Regular.ttf"))
        cs.setLeading(16.0f) //gap between  two line if used
        cs.setFont(font , 25f)
        cs.setNonStrokingColor(0f,0f,0f)

        //name
        cs.newLineAtOffset(140f ,497f )
        cs.showText(name)

        //class
        cs.newLineAtOffset(-0.5f ,-67f )
        cs.showText(myClass)

        //section
        cs.newLineAtOffset(22f ,-65f )
        cs.showText(section)

        //roll no
        cs.newLineAtOffset(2f ,-65f )
        cs.showText(rollNo)

        //subject
        cs.newLineAtOffset(-2f ,-65f )
        cs.showText(subject)
        cs.endText()

        //school name
        cs.beginText()
        cs.setFont(font , 27f)
        val textWidth = (font.getStringWidth(schoolName.uppercase()) / 1000.0f) * 27f;
        cs.newLineAtOffset(297.72f-(textWidth)/2f , 670f)
        cs.showText(schoolName.uppercase())
        cs.newLineAtOffset(textWidth/2f , 0f)
        //add a underline to college name
        cs.endText()
        cs.setNonStrokingColor(0.25f , 0.25f ,0.1f)
        cs.addRect(297.72f-(textWidth)/2f , 664f , textWidth , 2.5f)
        cs.fill()

        //session
        cs.beginText()
        cs.setFont(font , 26.0f)
        cs.setNonStrokingColor(0.0f , 0.0f ,0.0f)
        val textWidth2 = (font.getStringWidth(session.uppercase()) / 1000.0f) * 26.0f;
        cs.newLineAtOffset(297.72f-(textWidth2)/2f , 630f)
        cs.showText(session.uppercase())
        cs.newLineAtOffset(textWidth2/2f , 0f)
        //add a underline to college name
        cs.endText()
        cs.setNonStrokingColor(0.25f , 0.25f ,0.1f)
        cs.addRect(297.72f-(textWidth2)/2f , 626f , textWidth2 , 2.0f)
        cs.fill()
        cs.close()

        //QRCode
        if(cbQrCode.isChecked){
            val bitmap = getBitmapOfQrText("Myself $name, I am from class $myClass, section $section and roll number $rollNo." +
                    " I have completed an assignment in the subject of $subject.\n ThankYou!")
            val stream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val bitMapData = stream.toByteArray()
            val qrImage = PDImageXObject.createFromByteArray(document , bitMapData , "qrCode")
            val contentStreamForImage =  PDPageContentStream(document , pdPage , PDPageContentStream.AppendMode.APPEND , true)
            contentStreamForImage.drawImage(qrImage , 510.0f , 10.0f , 75f , 75f)
            contentStreamForImage.close()
        }
        //college logo
        if(schoolLogoBitmap != null){
            val stream = ByteArrayOutputStream()
            schoolLogoBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val bitMapData = stream.toByteArray()
            val qrImage = PDImageXObject.createFromByteArray(document , bitMapData , "collegeLogo")
            val contentStreamForImage =  PDPageContentStream(document , pdPage , PDPageContentStream.AppendMode.APPEND , true)
            contentStreamForImage.drawImage(qrImage , 477.5f , 734.0f , 100f , 100f)
            contentStreamForImage.close()
        }


        writePdfFile(document , "Front_page_maker-" + System.currentTimeMillis())

    }

    private fun tempFourth(document: PDDocument, name: String, myClass: String, section: String, rollNo: String, session: String, subject: String, schoolName: String) {

        //edit the pdf for second template
        val pdPage = document.getPage(0)
        val cs =  PDPageContentStream(document , pdPage , PDPageContentStream.AppendMode.APPEND , true)
        cs.beginText()
        val font = PDType0Font.load(document, assets.open("com/tom_roush/pdfbox/resources/ttf/LiberationSans-Regular.ttf"))
        cs.setLeading(16.0f) //gap between  two line if used
        cs.setFont(font , 25f)
        cs.setNonStrokingColor(0f,0f,0f)

        //name
        cs.newLineAtOffset(135f ,530f )
        cs.showText(name)

        //class
        cs.newLineAtOffset(-4f ,-59f )
        cs.showText(myClass)

        //section
        cs.newLineAtOffset(28f ,-57.5f )
        cs.showText(section)

        //roll no
        cs.newLineAtOffset(4f ,-57.5f )
        cs.showText(rollNo)

        //subject
        cs.newLineAtOffset(-1f ,-57.5f )
        cs.showText(subject)
        cs.endText()

        //school name
        cs.beginText()
        cs.setFont(font , 29f)
        val textWidth = (font.getStringWidth(schoolName.uppercase()) / 1000.0f) * 29
        cs.newLineAtOffset(575f-textWidth , 750f)
        cs.showText(schoolName.uppercase())
        //add a underline to college name
        cs.endText()
        cs.setNonStrokingColor(0.25f , 0.25f ,0.1f)
        cs.addRect(575f-textWidth , 740f , textWidth , 2.5f)
        cs.fill()

        //session
        cs.beginText()
        cs.setFont(font , 27f)
        cs.setNonStrokingColor(0f,0f,0f)
        val textWidth2 = (font.getStringWidth(session.uppercase()) / 1000.0f) * 27
        cs.newLineAtOffset(575f-textWidth2 , 700f)
        cs.showText(session.uppercase())
        //add a underline to college name
        cs.endText()
        cs.setNonStrokingColor(0.25f , 0.25f ,0.1f)
        cs.addRect(575f-textWidth2 , 695f , textWidth2 , 1.8f)
        cs.fill()
        cs.close()

        //QRCode
        if(cbQrCode.isChecked){
            val bitmap = getBitmapOfQrText("Myself $name, I am from class $myClass, section $section and roll number $rollNo." +
                    " I have completed an assignment in the subject of $subject.\n ThankYou!")
            val stream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val bitMapData = stream.toByteArray()
            val qrImage = PDImageXObject.createFromByteArray(document , bitMapData , "qrCode")
            val contentStreamForImage =  PDPageContentStream(document , pdPage , PDPageContentStream.AppendMode.APPEND , true)
            contentStreamForImage.drawImage(qrImage , 510.0f , 10.0f , 75f , 75f)
            contentStreamForImage.close()
        }
        //college logo
        if(schoolLogoBitmap != null){
            val stream = ByteArrayOutputStream()
            schoolLogoBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val bitMapData = stream.toByteArray()
            val qrImage = PDImageXObject.createFromByteArray(document , bitMapData , "collegeLogo")
            val contentStreamForImage =  PDPageContentStream(document , pdPage , PDPageContentStream.AppendMode.APPEND , true)
            contentStreamForImage.drawImage(qrImage , 30f , 600f , 100f , 100f)
            contentStreamForImage.close()
        }



        writePdfFile(document , "Front_page_maker-" + System.currentTimeMillis())

    }

    private fun writePdfFile(pdDocument: PDDocument, pdfName: String) {
        val intent = Intent(this@SchoolEditActivity , PdfViewActivity::class.java)
        intent.putExtra("templateIndex" , 9999)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentResolver = contentResolver
            val pdfCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Downloads.EXTERNAL_CONTENT_URI
            }
            val contentValues = ContentValues().apply {
                put(MediaStore.Files.FileColumns.DISPLAY_NAME, "${pdfName}.pdf")
                put(MediaStore.Files.FileColumns.MIME_TYPE, "application/pdf")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Files.FileColumns.IS_PENDING, 1)
                    put(MediaStore.Files.FileColumns.RELATIVE_PATH, "Documents/Front_Page_Maker/")
                }
            }
            val pdfUri = contentResolver.insert(pdfCollection, contentValues)
            pdfUri?.let {
                val outputStream = contentResolver.openOutputStream(it)
                pdDocument.save(outputStream)
                pdDocument.close()
                outputStream?.close()
                contentValues.clear()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.put(MediaStore.Files.FileColumns.IS_PENDING, 0)
                }
                contentResolver.update(it, contentValues, null, null)
                outputStream?.close()
                val newPath = directory.absolutePath.toString() + "/" + pdfName + ".pdf"

                intent.putExtra("actualFileUri" , newPath)
                startActivity(intent)
            }
        } else {

            if (ContextCompat.checkSelfPermission(this@SchoolEditActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this@SchoolEditActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@SchoolEditActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            } else {
                createFolder()
                val newPath = directory.absolutePath.toString() + "/" + pdfName + ".pdf"
                val fileForSave = File(newPath)

                pdDocument.save(fileForSave)
                pdDocument.close()
                intent.putExtra("actualFileUri" , newPath)
                startActivity(intent)
            }


        }

    }

    private fun createFolder() {
        val folder = File(folderPath)
        if (!folder.exists()) {
            folder.mkdir()
        }
        directory.mkdirs()
    }

    private fun getBitmapOfQrText(data : String) : Bitmap?{
        val qrgEncoder = QRGEncoder(data, null, QRGContents.Type.TEXT, 200)  // dimen --500
        qrgEncoder.colorBlack = Color.WHITE
        qrgEncoder.colorWhite = Color.BLACK
        var bitmap : Bitmap? = null
        try {
            bitmap = qrgEncoder.bitmap
        } catch (e : Exception) {
            Toast.makeText(this@SchoolEditActivity , e.toString() , Toast.LENGTH_SHORT).show()
        }
        return bitmap
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activityResultLauncher!!.launch(Manifest.permission.READ_MEDIA_IMAGES)

        }
        else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            activityResultLauncher!!.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        else{
            ActivityCompat.requestPermissions(this@SchoolEditActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.WRITE_EXTERNAL_STORAGE), 10)
        }
    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(applicationContext , Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        }
        else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            ContextCompat.checkSelfPermission(applicationContext , Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
        else {
            ContextCompat.checkSelfPermission(applicationContext , Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(applicationContext , Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }
}