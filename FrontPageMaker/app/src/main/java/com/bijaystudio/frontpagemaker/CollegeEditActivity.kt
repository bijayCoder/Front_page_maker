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
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
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
import androidx.appcompat.app.AppCompatActivity
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


class CollegeEditActivity : AppCompatActivity() {

    private val arrIdOfPdfCollege = arrayOf(R.raw.college_temp_one_pdf , R.raw.college_temp_two_pdf , R.raw.college_temp_three_pdf)
    @SuppressLint("SdCardPath")
    var directory = File("/sdcard/Documents/Front_Page_Maker/")
    private var folderPath = Environment.getDataDirectory().absolutePath + "/storage/emulated/0/Documents/Front_Page_Maker"
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    var activityResultLauncher: ActivityResultLauncher<String>? = null
    private lateinit var edtName : EditText
    private lateinit var edtRegNo : EditText
    private lateinit var edtRollNo : EditText
    private lateinit var edtBranch : EditText
    private lateinit var edtSem : EditText
    private lateinit var edtSession : EditText
    private lateinit var edtSubject : EditText
    private lateinit var edtCollegeName : EditText
    private lateinit var cbQrCode : CheckBox
    private lateinit var btnSubmit : AppCompatButton
    private lateinit var btnLogo : AppCompatButton
    private lateinit var ivLogo : ImageView
    private var branchName : String = ""
    private var semesterName : String = ""
    private var name : String = ""
    private var regNo : String = ""
    private var rollNo : String = ""
    private var session : String = ""
    private var subject : String = ""
    private var collegeName : String = ""
    private var collegeLogoBitmap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        PDFBoxResourceLoader.init(applicationContext);

        edtName = findViewById(R.id.edtName)
        edtRegNo = findViewById(R.id.edtRegNo)
        edtRollNo = findViewById(R.id.edtRollNo)
        edtSession = findViewById(R.id.edtSession)
        edtSubject = findViewById(R.id.edtSubject)
        edtCollegeName = findViewById(R.id.edtCollegeName)
        edtBranch = findViewById(R.id.edtBranch)
        edtSem = findViewById(R.id.edtSem)
        cbQrCode = findViewById(R.id.cbQrCode)
        btnSubmit = findViewById(R.id.btnSubmit)
        btnLogo = findViewById(R.id.btnLogo)
        ivLogo = findViewById(R.id.ivLogo)

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
                    collegeLogoBitmap = bitmap
                    ivLogo.visibility = View.VISIBLE
                }
            }
        }
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->

        }
        btnSubmit.setOnClickListener {
            createPdfFromTemplate(intent.getIntExtra("templateIndex" , 0))
//            Timer().schedule(5000) {
//
//            }
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
        regNo = edtRegNo.text.toString()
        rollNo = edtRollNo.text.toString()
        session = edtSession.text.toString()
        subject = edtSubject.text.toString()
        collegeName = edtCollegeName.text.toString()
        branchName = edtBranch.text.toString()
        semesterName = edtSem.text.toString()
        val inputStream = this.resources.openRawResource(arrIdOfPdfCollege[templateId])
        val document = PDDocument.load(inputStream)

        when(templateId){
            0 ->
                tempFirst(document , name , regNo , rollNo , branchName , semesterName , session , subject , collegeName)
            1 ->
                tempSecond(document , name , regNo , rollNo , branchName , semesterName , session,subject , collegeName)
            2 ->
                tempThird(document , name , regNo , rollNo , branchName , semesterName , session,subject , collegeName)
        }
    }

    private fun tempFirst(document: PDDocument, name: String, regNo : String, rollNo: String, branchName: String, semesterName: String, session: String, subject: String , collegeName : String)  {
        //edit the pdf for first template
        val pdPage = document.getPage(0)
        val cs =  PDPageContentStream(document , pdPage , PDPageContentStream.AppendMode.APPEND , true)
        cs.beginText()
       val font = PDType0Font.load(document, assets.open("com/tom_roush/pdfbox/resources/ttf/LiberationSans-Regular.ttf"))
        cs.setLeading(16.0f) //gap between  two line if used
        cs.setFont(font , 25f)
        cs.setNonStrokingColor(0f,0f,0f)
        //name
        cs.newLineAtOffset(120f ,520f )
        cs.showText(name)

        //department
        cs.newLineAtOffset(67f , -42.5f)
        cs.showText(branchName)

        //roll no
        cs.newLineAtOffset(-43f , -42.5f)
        cs.showText(rollNo)

        //subject
        cs.newLineAtOffset(-5f , -42.5f)
        cs.showText(subject)

        //reg no
        cs.newLineAtOffset(-2f , -42.5f)
        cs.showText(regNo)

        //semester
        cs.newLineAtOffset(16f , -42.5f)
        cs.showText(semesterName)

        // college name
        cs.setFont(font , 30f)
       val textWidth = (font.getStringWidth(collegeName.uppercase()) / 1000.0f) * 30f;
        cs.newLineAtOffset(145-(textWidth)/2f , 470f)
        cs.showText(collegeName.uppercase())
        cs.newLineAtOffset(textWidth/2f , 0f)
        //add a underline to college name

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
            val bitmap = getBitmapOfQrText("Myself $name, I am from $branchName department $semesterName semester with roll number $rollNo and registration number $regNo. I have completed an assignment in the subject of $subject.\n Thank You!")
            val stream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val bitMapData = stream.toByteArray()
            val qrImage = PDImageXObject.createFromByteArray(document , bitMapData , "qrCode")
            val contentStreamForImage =  PDPageContentStream(document , pdPage , PDPageContentStream.AppendMode.APPEND , true)
            contentStreamForImage.drawImage(qrImage , 30.0f , 40.0f , 75f , 75f)
            contentStreamForImage.close()
        }
        //college logo
        if(collegeLogoBitmap != null){
            val stream = ByteArrayOutputStream()
            collegeLogoBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val bitMapData = stream.toByteArray()
            val qrImage = PDImageXObject.createFromByteArray(document , bitMapData , "collegeLogo")
            val contentStreamForImage =  PDPageContentStream(document , pdPage , PDPageContentStream.AppendMode.APPEND , true)
            contentStreamForImage.drawImage(qrImage , 40.0f , 620.0f , 85f , 85f)
            contentStreamForImage.close()
        }

        writePdfFile(document , "Front_page_maker-" + System.currentTimeMillis())
    }

    private fun tempSecond(document: PDDocument, name: String, regNo: String, rollNo: String, branchName: String, semesterName: String, session: String, subject: String  , collegeName: String) {
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

        //roll no
        cs.newLineAtOffset(32f , -68f)
        cs.showText(rollNo)

        //reg no
        cs.newLineAtOffset(-5f , -67f)
        cs.showText(regNo)

        //semester
        cs.newLineAtOffset(20f , -67f)
        cs.showText(semesterName)
        //department
        cs.newLineAtOffset(40f , -67f)
        cs.showText(branchName)
        //subject
        cs.newLineAtOffset(-60f , -67f)
        cs.showText(subject)
        //session
        cs.newLineAtOffset(0f , -67f)
        cs.showText(session)

        // college name
        cs.setFont(font , 30f)
        val textWidth = (font.getStringWidth(collegeName.uppercase()) / 1000.0f) * 30f;
        cs.newLineAtOffset(120-(textWidth)/2f , 650f)
        cs.showText(collegeName.uppercase())
        cs.newLineAtOffset(textWidth/2f , 0f)

        //add a underline to college name
        cs.endText()
        cs.setNonStrokingColor(0.25f , 0.25f ,0.1f)
        cs.addRect(297-(textWidth)/2f , 741f , textWidth , 2.0f)
        cs.fill()
        cs.close()

        //QRCode
        if(cbQrCode.isChecked){
            val bitmap = getBitmapOfQrText("Myself $name, I am from $branchName department $semesterName semester with roll number $rollNo and registration number $regNo. I have completed an assignment in the subject of $subject.\n Thank You!")
            val stream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val bitMapData = stream.toByteArray()
            val qrImage = PDImageXObject.createFromByteArray(document , bitMapData , "qrCode")
            val contentStreamForImage =  PDPageContentStream(document , pdPage , PDPageContentStream.AppendMode.APPEND , true)
            contentStreamForImage.drawImage(qrImage , 490.0f , 30.0f , 75f , 75f)
            contentStreamForImage.close()
        }
        //college logo
        if(collegeLogoBitmap != null){
            val stream = ByteArrayOutputStream()
            collegeLogoBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val bitMapData = stream.toByteArray()
            val qrImage = PDImageXObject.createFromByteArray(document , bitMapData , "collegeLogo")
            val contentStreamForImage =  PDPageContentStream(document , pdPage , PDPageContentStream.AppendMode.APPEND , true)
            contentStreamForImage.drawImage(qrImage , 245f , 600.0f , 100f , 100f)
            contentStreamForImage.close()
        }


        writePdfFile(document , "Front_page_maker-" + System.currentTimeMillis())
    }

    private fun tempThird(document: PDDocument, name: String, regNo: String, rollNo: String, branchName: String, semesterName: String, session: String, subject: String  , collegeName: String) {
        //edit the pdf for second template
        val pdPage = document.getPage(0)
        val cs =  PDPageContentStream(document , pdPage , PDPageContentStream.AppendMode.APPEND , true)
        cs.beginText()
        val font = PDType0Font.load(document, assets.open("com/tom_roush/pdfbox/resources/ttf/LiberationSans-Regular.ttf"))
        cs.setLeading(16.0f) //gap between  two line if used
        cs.setFont(font , 25f)
        cs.setNonStrokingColor(0f,0f,0f)
        //name
        cs.newLineAtOffset(140f ,531f )
        cs.showText(name)

        //roll no
        cs.newLineAtOffset(27f ,-65.4f )
        cs.showText(rollNo)

        //reg no
        cs.newLineAtOffset(-5f ,-65.4f )
        cs.showText(regNo)

        //semester
        cs.newLineAtOffset(20f ,-65.4f )
        cs.showText(semesterName)

        //department
        cs.newLineAtOffset(35f ,-64.3f )
        cs.showText(branchName)

        //subject
        cs.newLineAtOffset(-60f ,-65.4f )
        cs.showText(subject)
        cs.endText()

        //college name
        cs.beginText()
        cs.setFont(font , 27f)
        val textWidth = (font.getStringWidth(collegeName.uppercase()) / 1000.0f) * 27f;
        cs.newLineAtOffset(297.72f-(textWidth)/2f , 670f)
        cs.showText(collegeName.uppercase())
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
            val bitmap = getBitmapOfQrText("Myself $name, I am from $branchName department $semesterName semester with roll number $rollNo and registration number $regNo. I have completed an assignment in the subject of $subject.\n Thank You!")
            val stream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val bitMapData = stream.toByteArray()
            val qrImage = PDImageXObject.createFromByteArray(document , bitMapData , "qrCode")
            val contentStreamForImage =  PDPageContentStream(document , pdPage , PDPageContentStream.AppendMode.APPEND , true)
            contentStreamForImage.drawImage(qrImage , 510.0f , 10.0f , 75f , 75f)
            contentStreamForImage.close()
        }
        //college logo
        if(collegeLogoBitmap != null){
            val stream = ByteArrayOutputStream()
            collegeLogoBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val bitMapData = stream.toByteArray()
            val qrImage = PDImageXObject.createFromByteArray(document , bitMapData , "collegeLogo")
            val contentStreamForImage =  PDPageContentStream(document , pdPage , PDPageContentStream.AppendMode.APPEND , true)
            contentStreamForImage.drawImage(qrImage , 477.5f , 734.0f , 100f , 100f)
            contentStreamForImage.close()
        }


        writePdfFile(document , "Front_page_maker-" + System.currentTimeMillis())
    }

    private fun writePdfFile(pdDocument: PDDocument, pdfName: String) {
        val intent = Intent(this@CollegeEditActivity , PdfViewActivity::class.java)
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
                Toast.makeText(this@CollegeEditActivity, "/storage/emulated/0/Documents/Front_Page_Maker/$pdfName", Toast.LENGTH_LONG).show()
                val newPath = directory.absolutePath.toString() + "/" + pdfName + ".pdf"

                intent.putExtra("actualFileUri" , newPath)
                startActivity(intent)
            }
        } else {

            if (ContextCompat.checkSelfPermission(this@CollegeEditActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this@CollegeEditActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@CollegeEditActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            } else {
                createFolder()
                val newPath = directory.absolutePath.toString() + "/" + pdfName + ".pdf"
                val fileForSave = File(newPath)

                pdDocument.save(fileForSave)
                pdDocument.close()
                Toast.makeText(this@CollegeEditActivity, "/storage/emulated/0/Documents/Front_Page_Maker/$pdfName", Toast.LENGTH_LONG).show()
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
            Toast.makeText(this@CollegeEditActivity , e.toString() , Toast.LENGTH_SHORT).show()
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
            ActivityCompat.requestPermissions(this@CollegeEditActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.WRITE_EXTERNAL_STORAGE), 10)
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