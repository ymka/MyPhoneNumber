package net.ginapps.myphonenumber.holder

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.ginapps.myphonenumber.R

/**
 * Created by Alexander Kondenko.
 */

class PhoneHolder(itemView: View, private val mClickListener: ClickListener) :
    RecyclerView.ViewHolder(itemView), View.OnClickListener {
    val phoneNumber: TextView = itemView.findViewById(R.id.phoneNumber)
    val operatorName: TextView = itemView.findViewById(R.id.operatorName)
    val editBtn: ImageButton = itemView.findViewById(R.id.editPhoneNumber)

    init {
        val copyToClipboard = itemView.findViewById<ImageButton>(R.id.copyPhoneToClipBoard)
        copyToClipboard.setOnClickListener(this)
        val share = itemView.findViewById<ImageButton>(R.id.sharePhone)
        share.setOnClickListener(this)
        editBtn.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.copyPhoneToClipBoard -> mClickListener.onCopyPhoneNumber(adapterPosition)
            R.id.sharePhone -> mClickListener.onSharePhoneNumber(adapterPosition)
            R.id.editPhoneNumber -> mClickListener.onEditPhoneNumber(adapterPosition)
            else -> {}
        }
    }

    interface ClickListener {
        fun onCopyPhoneNumber(position: Int)
        fun onSharePhoneNumber(position: Int)
        fun onEditPhoneNumber(position: Int)
    }
}
