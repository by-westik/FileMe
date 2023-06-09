package com.westik.file.me.adapters

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.westik.file.me.fragments.HomeFragment
import com.westik.file.me.R
import com.westik.file.me.databinding.FileItemBinding
import com.westik.file.me.helpers.FileHelper
import com.westik.file.me.models.FileEntity
import com.westik.file.me.models.FileItem
import java.io.File
import java.math.BigInteger
import java.nio.file.Files
import java.nio.file.Paths
import java.security.MessageDigest

class FileViewHolder(private val binding: FileItemBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root){

    fun bind(file: FileItem) {
        binding.apply {
            if (file.isDirectory) {
                tvFileSize.visibility = View.GONE
                imvIsUpdate.visibility = View.GONE
                if (!file.canRead) {
                    imvFileImage.setImageDrawable(
                        AppCompatResources.getDrawable(
                            context,
                            R.drawable.ic_folder_lock
                        )
                    )
                } else {
                    imvFileImage.setImageDrawable(
                        AppCompatResources.getDrawable(
                            context,
                            R.drawable.ic_folder
                        )
                    )
                }
            } else {

                if (file.isModified) {
                    imvIsUpdate.visibility = View.VISIBLE
                } else {
                    imvIsUpdate.visibility = View.GONE
                }
                tvFileSize.visibility = View.VISIBLE
                imvFileImage.setImageDrawable(FileHelper.getFileDrawable(context, file.type))
            }
            tvFileDate.text = FileHelper.getFileDate(file.lastModified)
            tvFileName.text = file.name
            tvFileSize.text = FileHelper.getFileSize(file.size)
        }
    }
}


class FileAdapter(
    private var files: List<FileItem>,
    private val fragment: HomeFragment,
    private val onItemClick: (file: FileItem) -> Unit
) : RecyclerView.Adapter<FileViewHolder>() {


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

    fun updateAdapter(list: List<FileItem>) {
        this.files = list
        notifyDataSetChanged()
    }

}