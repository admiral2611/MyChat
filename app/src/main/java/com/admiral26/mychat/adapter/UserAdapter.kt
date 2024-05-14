package com.admiral26.mychat.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.admiral26.mychat.databinding.ItemUserBinding
import com.admiral26.mychat.listeners.UserListener
import com.admiral26.mychat.model.User


class UserAdapter :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    var userListener: UserListener? = null
    private val data = ArrayList<User>()


    fun setData(data: List<User>, userListener: UserListener) {
        this.data.clear()
        this.userListener = userListener
        this.data.addAll(data.shuffled())
        notifyDataSetChanged()
    }

    fun addData(data: List<User>) {
        this.data.addAll(data)
        notifyItemChanged(this.data.size)
    }

    inner class UserViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(data: User) {
            binding.tvName.text = data.name
            binding.tvEmail.text = data.email
            binding.roundedImageView.setImageBitmap(data.image?.let { imgUser(it) })
            binding.root.setOnClickListener {
                userListener?.onUserClick(data)
            }
        }

    }

    fun imgUser(url: String): Bitmap {
        val byte = Base64.decode(url, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(byte, 0, byte.size)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserViewHolder {

        return UserViewHolder(
            ItemUserBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bindData(data[position])
    }

    override fun getItemCount() = data.size
}