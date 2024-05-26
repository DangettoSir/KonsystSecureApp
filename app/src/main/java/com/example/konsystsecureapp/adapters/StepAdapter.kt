package com.example.konsystsecureapp.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.recyclerview.widget.RecyclerView
import com.example.konsystsecureapp.data.Step
import com.example.konsystsecureapp.databinding.ItemStepBinding

class StepAdapter(
    private val steps: List<Step>,
    private val context: Context,
    private val photos: MutableList<Bitmap?>,
    private val onStepClickListener: (Int) -> Unit,
) : RecyclerView.Adapter<StepAdapter.StepViewHolder>() {
    private val photoImageViews = arrayOfNulls<ImageView>(3)
    private var nextPhotoIndex = 0
    private val REQUEST_CODE_CAMERA = 123

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val binding = ItemStepBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StepViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        holder.bind(steps[position])
    }

    override fun getItemCount() = steps.size

    inner class StepViewHolder(val binding: ItemStepBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(step: Step) {
            if (step.action == "video"){
                binding.actions.visibility = View.VISIBLE
                binding.tvNotificationVideo.visibility = View.VISIBLE
                binding.actionsvideo.visibility = View.VISIBLE
            }
            if(step.action == "photo"){
                val photoImageViews = arrayOfNulls<ImageView>(3)
                photoImageViews[0] = binding.photoImageView1
                photoImageViews[1] = binding.photoImageView2
                photoImageViews[2] = binding.photoImageView3
                binding.actions.visibility = View.VISIBLE
                binding.tvNotificationPhoto.visibility = View.VISIBLE
                binding.actionsphoto.visibility = View.VISIBLE
                binding.actionsphoto.setOnClickListener {
                    if (nextPhotoIndex < 3) {

                    } else {
                        binding.actionsphoto.isEnabled = false
                    }
                }
            }
            if(step.action =="photo,video"){
                binding.actions.visibility = View.VISIBLE
                binding.tvNotificationPhoto.visibility = View.VISIBLE
                binding.actionsphoto.visibility = View.VISIBLE
                binding.tvNotificationVideo.visibility = View.VISIBLE
                binding.actionsvideo.visibility = View.VISIBLE
            }
            binding.tvNumber.text = "Шаг №"+step.number.toString()
            binding.tvDescription.text = step.title
            binding.root.setOnClickListener {
                val stepId = steps[adapterPosition].id
                onStepClickListener(stepId)
            }
        }

    }

    }



