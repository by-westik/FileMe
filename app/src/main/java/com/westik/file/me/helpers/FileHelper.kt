package com.westik.file.me.helpers

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.westik.file.me.R
import com.westik.file.me.models.FileModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

class FileHelper {

    companion object {
        fun getFileDrawable(context: Context, type: String): Drawable {
            return when(type){
                "jpg", "png", "jpeg" -> {
                    ActivityCompat.getDrawable(context, R.drawable.ic_file_image)!!
                }
                "txt" -> {
                    ActivityCompat.getDrawable(context, R.drawable.ic_file_document)!!
                }
                "doc", "docx" -> {
                    ActivityCompat.getDrawable(context, R.drawable.ic_file_word)!!
                }
                "pdf" -> {
                    ActivityCompat.getDrawable(context, R.drawable.ic_file_pdf)!!

                }
                "xls", "xlsx" -> {
                    ActivityCompat.getDrawable(context, R.drawable.ic_file_excel)!!
                }
                "ppt", "pptx" -> {
                    ActivityCompat.getDrawable(context, R.drawable.ic_file_powerpoint)!!
                }
                "zip", "tar", "7z" -> {
                    ActivityCompat.getDrawable(context, R.drawable.ic_zip_box)!!
                }
                "mp3" -> {
                    ActivityCompat.getDrawable(context, R.drawable.ic_file_music)!!
                }
                "mp4", "mkv" -> {
                    ActivityCompat.getDrawable(context, R.drawable.ic_file_video)!!
                }
                else -> {
                    ActivityCompat.getDrawable(context, R.drawable.ic_file_question)!!
                }
            }

        }

        fun openFile(file: FileModel, context: Context) : Intent {
            val apkUri = FileProvider.getUriForFile(context, context.packageName + ".MyFileProvider", File(file.absolutePath))
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, null)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            return intent
        }
        fun sendFile(file: FileModel, context: Context) : Intent {
            val apkUri = FileProvider.getUriForFile(
                context,
                context.packageName + ".MyFileProvider",
                File(file.absolutePath)
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                setDataAndType(apkUri, null)
                putExtra(Intent.EXTRA_STREAM, apkUri)
            }
            return intent
        }

        fun getFileDate(date: Long): String {
            return SimpleDateFormat.getDateInstance().format(Date(date))
        }

        fun getFileSize(length: Long): String {
            val megaBytes = length.toDouble() / (1024 * 1024)
            val str = String.format("%.2f", megaBytes)
            return "$str Mb"
        }
    }
}