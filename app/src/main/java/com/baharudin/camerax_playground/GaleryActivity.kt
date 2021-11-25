package com.baharudin.camerax_playground

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.baharudin.camerax_playground.databinding.ActivityGaleryBinding
import com.baharudin.camerax_playground.databinding.ActivityMainBinding
import java.io.File

class GaleryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGaleryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGaleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val directory = File(externalMediaDirs[0].absolutePath)
        val files = directory.listFiles() as Array<File>

        //menampilkan file foto secara terbalik, yang terakhir akan ditampilkan lebih dulu
        val adapterGalery = GaleryAdapter(files.reversedArray())
        binding.viewPager.adapter = adapterGalery
    }
}