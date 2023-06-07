package net.ginapps.myphonenumber.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdView
import net.ginapps.myphonenumber.R

class AddsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val adView: AdView = itemView.findViewById(R.id.adView)
}
