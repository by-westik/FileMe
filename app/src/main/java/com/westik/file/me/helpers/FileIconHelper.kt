package com.westik.file.me.helpers

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.app.ActivityCompat
import com.westik.file.me.R

class FileIconHelper {


    fun getFileDrawable(context: Context, type: String): Drawable {
        return when(type){
            "jpg", "png", "jpeg" -> {
                ActivityCompat.getDrawable(context, R.drawable.ic_file_image)!!
            }
            "pdf", "txt", "doc", "docx" -> {
                ActivityCompat.getDrawable(context, R.drawable.ic_file_document)!!
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
}