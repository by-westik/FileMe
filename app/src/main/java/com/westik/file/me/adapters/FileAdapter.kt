package com.westik.file.me.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.westik.file.me.R
import com.westik.file.me.databinding.FileItemBinding
import com.westik.file.me.helpers.FileIconHelper
import com.westik.file.me.helpers.Files
import com.westik.file.me.models.FileModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

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
            imvFileImage.setImageDrawable(FileIconHelper().getFileDrawable(context, file.type))
        }
        tvFileDate.text = SimpleDateFormat.getDateInstance().format(Date(file.lastModified))
        tvFileName.text = file.name
        tvFileSize.text = file.size
    }


}
class FileAdapter(private var files: List<FileModel>, private val fragment: Fragment) : RecyclerView.Adapter<FileViewHolder>() {

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
                    directoryOnClick(Files.getFiles(file.absolutePath))
                }
            } else {
                openFile(file)
            }
        }
        if (!file.isDirectory) {
            holder.itemView.setOnLongClickListener {
                sendFile(file)
                return@setOnLongClickListener true
            }
        }

    }

    private fun openFile(file: FileModel) {
        val apkUri = FileProvider.getUriForFile(fragment.requireContext(), fragment.requireContext().packageName + ".MyFileProvider", File(file.absolutePath))
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, null)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        fragment.startActivity(intent)
    }
    private fun sendFile(file: FileModel) {
        val apkUri = FileProvider.getUriForFile(
            fragment.requireContext(),
            fragment.requireContext().packageName + ".MyFileProvider",
            File(file.absolutePath)
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            setDataAndType(apkUri, null)
            putExtra(Intent.EXTRA_STREAM, apkUri)
        }
        fragment.startActivity(intent)
    }

    fun directoryOnClick(arrayList: ArrayList<FileModel>){
        this.files = arrayList
        notifyDataSetChanged()
    }
}