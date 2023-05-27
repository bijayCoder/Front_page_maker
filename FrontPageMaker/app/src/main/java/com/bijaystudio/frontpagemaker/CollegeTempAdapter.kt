package com.bijaystudio.frontpagemaker

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class CollegeTempAdapter internal constructor(var context: Context, var arrImage: Array<Int>) : RecyclerView.Adapter<CollegeTempAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(context).inflate(R.layout.templetes_view_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imgFile = arrImage[position]
        val bitmap1 = BitmapFactory.decodeResource(context.resources , arrImage[position])
        val height = bitmap1.height
        val width = bitmap1.width

        val out = ByteArrayOutputStream()
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap1, width / 5, height / 5, false)
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
        val decoded = BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))
        holder.image.setImageBitmap(decoded)

        holder.image.setOnClickListener {
            val intent = Intent(context , CollegeEditActivity::class.java);
            intent.putExtra("templateIndex" ,position)
            context.startActivity(intent)
        }
        holder.preview.setOnClickListener {
            val intent = Intent(context , PdfViewActivity::class.java);
            intent.putExtra("templateIndex" , position)
            intent.putExtra("identity" , "college")
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return arrImage.size // set the size of number in recycler view item
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView =itemView.findViewById(R.id.rv_layout_image)
         var preview : ImageView =itemView.findViewById(R.id.rv_layout_preview)

    }
}
