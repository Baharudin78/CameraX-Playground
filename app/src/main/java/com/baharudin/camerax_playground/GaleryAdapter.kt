package com.baharudin.camerax_playground

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.baharudin.camerax_playground.databinding.ListImageBinding
import com.bumptech.glide.Glide
import java.io.File

class GaleryAdapter(private var fileArray : Array<File>)
    :RecyclerView.Adapter<GaleryAdapter.GaleryHolder>() {

        class GaleryHolder(val binding : ListImageBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(file: File) {
                Glide.with(binding.root)
                    .load(file)
                    .into(binding.localImg)
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GaleryHolder {
        val inflater = ListImageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return GaleryHolder(inflater)
    }

    override fun onBindViewHolder(holder: GaleryHolder, position: Int) {
        holder.bind(fileArray[position])
    }

    override fun getItemCount(): Int {
       return fileArray.size
    }
}