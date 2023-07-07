package com.bijaystudio.frontpagemaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var rvTemplatesCollege : RecyclerView
    private lateinit var rvTemplatesSchool : RecyclerView
    private var arrImageFilesCollege = arrayOf(R.drawable.college_temp_one_image , R.drawable.college_temp_two_image , R.drawable.college_temp_three_image , R.drawable.college_temp_four_image)
    private var arrImageFilesSchool = arrayOf(R.drawable.school_temp_one_image , R.drawable.school_temp_two_image , R.drawable.school_temp_three_image , R.drawable.school_temp_four_image)
    private lateinit var collegeAdapter: CollegeTempAdapter
    private lateinit var schoolAdapter: SchoolTempAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        collegeAdapter = CollegeTempAdapter(this , arrImageFilesCollege)
        schoolAdapter = SchoolTempAdapter(this , arrImageFilesSchool)

        rvTemplatesCollege = findViewById(R.id.rv_templates_college)
        rvTemplatesSchool = findViewById(R.id.rv_templates_school)

        rvTemplatesCollege.layoutManager = GridLayoutManager(this , 2)
        rvTemplatesCollege.adapter = collegeAdapter

        rvTemplatesSchool.layoutManager = GridLayoutManager(this , 2)
        rvTemplatesSchool.adapter = schoolAdapter

    }
}