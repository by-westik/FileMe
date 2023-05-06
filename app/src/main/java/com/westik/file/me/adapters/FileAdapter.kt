package com.westik.file.me.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.westik.file.me.R
import com.westik.file.me.databinding.FileItemBinding
import com.westik.file.me.models.FileModel

class FileViewHolder(binding: FileItemBinding) : RecyclerView.ViewHolder(binding.root) {
    private val tvFileName = binding.tvFileName
    private val tvFileDate = binding.tvFileDate
    private val tvFileSize = binding.tvFileSize
    private val imvFileImage = binding.imvFileImage

    fun bind(file: FileModel) {
        if (file.isDirectory) {
            tvFileSize.visibility = View.GONE
        } else {
            tvFileSize.visibility = View.VISIBLE
        }
        tvFileDate.text = file.lastModified.toString()
        tvFileName.text = file.name
        tvFileSize.text = file.size
    }

}
class FileAdapter(private val files: List<FileModel>) : RecyclerView.Adapter<FileViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        return FileViewHolder(FileItemBinding.inflate(
            LayoutInflater.from(parent.context), parent,
            false))
    }

    override fun getItemCount(): Int = files.size

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = files[position]
        holder.bind(file)


    }
}