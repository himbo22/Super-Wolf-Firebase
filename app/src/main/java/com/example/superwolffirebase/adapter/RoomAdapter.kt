package com.example.superwolffirebase.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.superwolffirebase.databinding.ItemListLobbyBinding
import com.example.superwolffirebase.model.Player
import com.example.superwolffirebase.model.Room
import com.example.superwolffirebase.other.MyDiffUtil
import com.example.superwolffirebase.utils.showLog
import com.example.superwolffirebase.views.mainscreen.fragments.LobbyFragmentDirections

class RoomAdapter(private val player: Player, private val listener: OnItemClickListener) : RecyclerView.Adapter<RoomAdapter.MyViewHolder>() {

    private var oldRoomList = emptyList<Room>()

    inner class MyViewHolder(val binding: ItemListLobbyBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemListLobbyBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return oldRoomList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentRoom = oldRoomList[position]
        holder.apply {
            binding.apply {
                roomName.text = currentRoom.name
                totalPlayer.text = "${currentRoom.amount}/9"
                room.setOnClickListener {
                    listener.onItemClick(currentRoom)
                }
            }
        }

    }

    fun setData(newRoomList: List<Room>){
        val diffUtil = MyDiffUtil(oldRoomList, newRoomList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        oldRoomList = newRoomList
        diffResult.dispatchUpdatesTo(this)
    }

}

interface OnItemClickListener{
    fun onItemClick(room: Room)
}