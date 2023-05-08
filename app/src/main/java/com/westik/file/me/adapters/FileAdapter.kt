package com.westik.file.me.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.westik.file.me.HomeFragment
import com.westik.file.me.R
import com.westik.file.me.databinding.FileItemBinding
import com.westik.file.me.helpers.FileHelper
import com.westik.file.me.helpers.StorageHelper
import com.westik.file.me.models.FileModel

class FileViewHolder(binding: FileItemBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root){

    private val tvFileName = binding.tvFileName
    private val tvFileDate = binding.tvFileDate
    private val tvFileSize = binding.tvFileSize
    private val imvFileImage = binding.imvFileImage

    fun bind(file: FileModel) {
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
class FileAdapter(private var files: List<FileModel>, private val fragment: HomeFragment) : RecyclerView.Adapter<FileViewHolder>() {

    var onItemClick:((path:String, position:Int) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        return FileViewHolder(FileItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false), fragment.requireContext())
    }

    override fun getItemCount(): Int = files.size

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = files[position]
        holder.bind(file)

        holder.itemView.setOnClickListener {
            if (!file.canRead) {
                Toast.makeText(fragment.requireContext(), "Доступ запрещен", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (file.isDirectory) {
                if (file.isDirectoryEmpty!!) {
                    //TODO сделать пустой вид/фрагмент
                    Toast.makeText(fragment.requireContext(), "Пустая папка", Toast.LENGTH_SHORT).show()
                } else {
                    onItemClick?.invoke(file.absolutePath, position)
                    directoryOnClick(StorageHelper.getFiles(file.absolutePath))
                }
            } else {
                fragment.startActivity(FileHelper.openFile(file, fragment.requireContext()))
            }
        }
        if (!file.isDirectory) {
            holder.itemView.setOnLongClickListener {
                fragment.startActivity(FileHelper.sendFile(file, fragment.requireContext()))
                return@setOnLongClickListener true
            }
        }

    }



    fun directoryOnClick(arrayList: ArrayList<FileModel>){
        this.files = arrayList
        // TODO мне пока это не нравится, надо как-то потом переделать
        fragment.setFilterData(this.files)
        notifyDataSetChanged()
    }
}