package com.westik.file.me.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.westik.file.me.HomeFragment
import com.westik.file.me.R
import com.westik.file.me.databinding.FileItemBinding
import com.westik.file.me.helpers.FileHelper
import com.westik.file.me.models.FileEntity

class FileViewHolder(binding: FileItemBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root){

    private val tvFileName = binding.tvFileName
    private val tvFileDate = binding.tvFileDate
    private val tvFileSize = binding.tvFileSize
    private val imvFileImage = binding.imvFileImage

    fun bind(file: FileEntity) {
        if (file.isDirectory) {
            tvFileSize.visibility = View.GONE
            if (!file.canRead) {
                imvFileImage.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_folder_lock))
            } else {
                imvFileImage.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_folder))
            }
        } else {
            tvFileSize.visibility = View.VISIBLE
            imvFileImage.setImageDrawable(FileHelper.getFileDrawable(context, file.type))
        }
        tvFileDate.text = FileHelper.getFileDate(file.lastModified)
        tvFileName.text = file.name
        tvFileSize.text = FileHelper.getFileSize(file.size)
    }
}
class FileAdapter(
    private var files: List<FileEntity>,
    private val fragment: HomeFragment,
    private val onItemClick: (file: FileEntity) -> Unit) : RecyclerView.Adapter<FileViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        return FileViewHolder(FileItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false), fragment.requireContext())
    }

    override fun getItemCount(): Int = files.size

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = files[position]
        holder.bind(file)

        holder.itemView.setOnClickListener {
            onItemClick(file)
        }
        if (!file.isDirectory) {
            holder.itemView.setOnLongClickListener {
                fragment.startActivity(FileHelper.sendFile(file, fragment.requireContext()))
                return@setOnLongClickListener true
            }
        }

    }

    fun updateAdapter(list: List<FileEntity>) {
        this.files = list
        notifyDataSetChanged()
    }



    fun directoryOnClick(arrayList: ArrayList<FileEntity>){
        this.files = arrayList
        // TODO мне пока это не нравится, надо как-то потом переделать
      //  fragment.setFilterData(this.files)
      //  notifyDataSetChanged()
    }
}