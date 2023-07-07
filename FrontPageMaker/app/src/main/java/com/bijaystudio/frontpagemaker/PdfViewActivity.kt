package com.bijaystudio.frontpagemaker

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.barteksc.pdfviewer.PDFView
import java.io.File
import java.io.InputStream


class PdfViewActivity : AppCompatActivity() {

    private lateinit var pdfView : PDFView
    private val arrIdOfPdfCollege = arrayOf(R.raw.college_temp_one_pdf , R.raw.college_temp_two_pdf , R.raw.college_temp_three_pdf , R.raw.college_temp_four_pdf)
    private val arrIdOfPdfSchool = arrayOf(R.raw.school_temp_one_pdf , R.raw.school_temp_two_pdf , R.raw.school_temp_three_pdf , R.raw.school_temp_four_pdf)
    private lateinit var btnShare : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_view)
        val templateIndex = intent.getIntExtra("templateIndex" ,0 )
        val identity = intent.getStringExtra("identity")
        pdfView = findViewById(R.id.pdfView)
        btnShare = findViewById(R.id.btnSharePdf)

        // when preview
        if(templateIndex == 9999){
            val path = intent.getStringExtra("actualFileUri")

            if(path != null){
                val builder = AlertDialog.Builder(this@PdfViewActivity)
                builder.setTitle("Pdf Saved Location:")
                builder.setMessage("/storage/emulated/0/Documents/Front_Page_Maker/...")
                builder.setPositiveButton("OK") { _, _ -> }
                builder.show()
                val file = File(path)
                pdfView.fromFile(file).load()
            }else{
                Toast.makeText(this , "Some error occured in PdfViewActivity" , Toast.LENGTH_SHORT).show()
            }

            btnShare.visibility = View.VISIBLE
            btnShare.setOnClickListener {
                val uri = Uri.parse(path)
                val share = Intent()
                share.action = Intent.ACTION_SEND
                share.type = "application/pdf"
                share.putExtra(Intent.EXTRA_STREAM, uri)
                startActivity(Intent.createChooser(share, "Share"))

            }

        }else{ // when actual
            btnShare.visibility = View.GONE
            val inputStream : InputStream = if(identity == "school"){
                this.resources.openRawResource(arrIdOfPdfSchool[templateIndex])
            }else{
                this.resources.openRawResource(arrIdOfPdfCollege[templateIndex])
            }
            pdfView.fromStream(inputStream).load()
        }

    }
}
