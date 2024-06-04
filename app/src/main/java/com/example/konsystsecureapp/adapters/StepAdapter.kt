package com.example.konsystsecureapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.konsystsecureapp.data.Step
import com.example.konsystsecureapp.databinding.ItemStepBinding
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class StepAdapter(
    internal val steps: List<Step>,
    private val context: Context,
    private val onStepClickListener: (Int) -> Unit,
    private val onActionPhotoClickListener: (Int) -> Unit,
    private val onActionVideoClickListener: (Int) -> Unit
) : RecyclerView.Adapter<StepAdapter.StepViewHolder>() {
    var clickedPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val binding = ItemStepBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StepViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        val step = steps[position]
        holder.bind(step, holder)

        holder.binding.actionsvideo.setOnClickListener {
            clickedPosition = position
            onActionVideoClickListener.invoke(position)
        }
        holder.binding.actionsphoto.setOnClickListener {
            clickedPosition = position
            onActionPhotoClickListener.invoke(position)
        }
        step.videoPath?.let { path ->
            val videoView = holder.binding.videoView
            val closeView = holder.binding.close
            videoView.visibility = View.VISIBLE
            closeView.visibility = View.VISIBLE
            if (path.isNotEmpty()) {
                try {
                    val file = File(path)
                    val videoUri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                    val retriever = MediaMetadataRetriever()
                    retriever.setDataSource(context, videoUri)
                    val bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST)
                    retriever.release()
                    val roundedBitmap = createRoundedBitmap(bitmap!!, 76, 76, 20f)
                    videoView.setImageBitmap(roundedBitmap)
                    holder.binding.actionsvideo.isEnabled = false
                    holder.binding.videoAddRounded.isEnabled = false
                    closeView.setOnClickListener {
                        removeVideoFromStep(holder, position)
                        Log.d("closeView", "Нажал!")
                    }
                } catch (e: FileNotFoundException) {
                    Log.e("StepAdapter", "Не удалось открыть файл: $path", e)
                    holder.binding.videoView.visibility = View.GONE
                    holder.binding.actionsvideo.isEnabled = true
                    holder.binding.videoAddRounded.isEnabled = true
                } catch (e: IOException) {
                    Log.e("StepAdapter", "Ошибка при обработке файла: $path", e)
                    holder.binding.videoView.visibility = View.GONE
                    holder.binding.actionsvideo.isEnabled = true
                    holder.binding.videoAddRounded.isEnabled = true
                }
            }
        } ?: run {
            holder.binding.videoView.visibility = View.GONE
            holder.binding.actionsvideo.isEnabled = true
        }
        step.photoPaths?.let { paths ->
            val photoImageViews = arrayOf(
                holder.binding.photoImageView1,
                holder.binding.photoImageView2,
                holder.binding.photoImageView3
            )
            val closeViews = arrayOf(
                holder.binding.close1,
                holder.binding.close2,
                holder.binding.close3
            )
            for (i in 0 until paths.size) {
                photoImageViews[i].visibility = View.VISIBLE
                closeViews[i].visibility = View.VISIBLE
                val bitmap = BitmapFactory.decodeFile(paths[i])
                val roundedBitmap = createRoundedBitmap(bitmap, 76, 76, 20f)
                photoImageViews[i].setImageBitmap(roundedBitmap)

                closeViews[i].setOnClickListener {
                    removePhotoFromStep(holder, position, i)
                }
            }

            for (i in paths.size until photoImageViews.size) {
                photoImageViews[i].visibility = View.GONE
                closeViews[i].visibility = View.GONE
            }
            holder.binding.actionsphoto.isEnabled = paths.size < 3
            holder.binding.imageAddRounded.isEnabled = paths.size < 3
        } ?: run {
            val photoImageViews = arrayOf(
                holder.binding.photoImageView1,
                holder.binding.photoImageView2,
                holder.binding.photoImageView3
            )

            for (imageView in photoImageViews) {
                imageView.setImageDrawable(null)
            }

            holder.binding.actionsphoto.isEnabled = true
            holder.binding.imageAddRounded.isEnabled = true
        }
    }


    private fun createRoundedBitmap(bitmap: Bitmap, width: Int, height: Int, cornerRadius: Float): Bitmap {
        val roundedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(roundedBitmap)
        val paint = Paint()
        paint.isAntiAlias = true
        val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
        paint.shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
        return roundedBitmap
    }
    private fun updatePhotoVisibility(holder: StepAdapter.StepViewHolder, photoPaths: List<String>?) {
        holder.binding.photoImageView1.visibility = if (photoPaths?.size ?: 0 > 0) View.VISIBLE else View.GONE
        holder.binding.close1.visibility = holder.binding.photoImageView1.visibility
        holder.binding.photoImageView2.visibility = if (photoPaths?.size ?: 0 > 1) View.VISIBLE else View.GONE
        holder.binding.close2.visibility = holder.binding.photoImageView2.visibility
        holder.binding.photoImageView3.visibility = if (photoPaths?.size ?: 0 > 2) View.VISIBLE else View.GONE
        holder.binding.close3.visibility = holder.binding.photoImageView3.visibility
    }
    private fun updateVideoVisibility(holder: StepViewHolder, videoPath: String?) {
        holder.binding.videoView.visibility = if (videoPath!!.isNotEmpty()) View.VISIBLE else View.GONE
    }
    private fun removePhotoFromStep(holder: StepAdapter.StepViewHolder, position: Int, photoIndex: Int) {
        val step = steps[position]
        step.photoPaths?.let { paths ->
            if (photoIndex < paths.size) {
                Log.d("RemoveVideoFromStep", "Video path: $paths")
                File(paths[photoIndex]).delete()
                paths.removeAt(photoIndex)
                holder.setPhotoPaths(paths)
                updatePhotoVisibility(holder, paths)
                notifyItemChanged(position)
            }
        }
    }
    private fun removeVideoFromStep(holder: StepAdapter.StepViewHolder, position: Int) {
        val step = steps[position]
        step.videoPath?.let { path ->
            Log.d("RemoveVideoFromStep", "Video path: $path")
            val file = File(path)
            if (file.exists()) {
                if (file.delete()) {
                    step.videoPath = null
                    holder.setVideoPath(null)
                    updateVideoVisibility(holder, path)
                    notifyItemChanged(position)
                    Log.d("EXISTS", "Файл успешно удален")
                } else {
                    Log.d("EXISTS", "Не удалось удалить файл")
                }
            } else {
                Log.d("EXISTS", "Файл не существует")
            }
        }
    }
    override fun getItemCount() = steps.size

    inner class StepViewHolder(val binding: ItemStepBinding) :
        RecyclerView.ViewHolder(binding.root) {
        protected var step: Step? = null
        @SuppressLint("SetTextI18n")
        fun bind(step: Step, holder: StepViewHolder) {
            if (step.action == "video") {
                binding.actions.visibility = View.VISIBLE
                binding.tvNotificationVideo.visibility = View.VISIBLE
                binding.actionsvideo.visibility = View.VISIBLE
            }
            if (step.action == "photo") {
                val photoImageViews = arrayOfNulls<ImageView>(3)
                photoImageViews[0] = binding.photoImageView1
                photoImageViews[1] = binding.photoImageView2
                photoImageViews[2] = binding.photoImageView3
                binding.actions.visibility = View.VISIBLE
                binding.tvNotificationPhoto.visibility = View.VISIBLE
                binding.actionsphoto.visibility = View.VISIBLE
            }
            if (step.action == "photo,video") {
                binding.actions.visibility = View.VISIBLE
                binding.tvNotificationPhoto.visibility = View.VISIBLE
                binding.actionsphoto.visibility = View.VISIBLE
                binding.tvNotificationVideo.visibility = View.VISIBLE
                binding.actionsvideo.visibility = View.VISIBLE
            }
            binding.tvNumber.text = "Шаг №" + step.number.toString()
            binding.tvDescription.text = step.title
        }
        fun setPhotoPaths(photoPaths: List<String>) {
            step?.photoPaths = photoPaths.toMutableList()
        }
        fun setVideoPath(videoPath: String?) {
            step?.videoPath = videoPath
        }
        }
}










