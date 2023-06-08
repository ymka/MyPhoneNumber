package net.ginapps.myphonenumber

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.LoadAdError
import net.ginapps.myphonenumber.analytics.Analytics
import net.ginapps.myphonenumber.holder.AddsHolder
import net.ginapps.myphonenumber.holder.PhoneHolder
import net.ginapps.myphonenumber.holder.PhoneHolder.ClickListener

/**
 * Created by Alexander Kondenko.
 */
class PhoneNumbersAdapter(
    private val mContext: Context,
    private val analytics: Analytics,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ClickListener {
    private val mPhoneData: MutableList<PhoneData>
    private var mActionListener: ActionListener? = null

    init {
        mPhoneData = ArrayList()
    }

    fun setActionListener(actionListener: ActionListener?) {
        mActionListener = actionListener
    }

    fun addPhonesData(phoneData: List<PhoneData>) {
        mPhoneData.addAll(phoneData)
    }

    fun resetPhonesData(phoneData: List<PhoneData>) {
        mPhoneData.clear()
        mPhoneData.addAll(phoneData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            ADDS_TYPE -> {
                val layout = if (mPhoneData.size > 1) {
                    R.layout.ads_banner_item
                } else {
                    R.layout.ads_item
                }
                AddsHolder(
                    LayoutInflater.from(parent.context).inflate(layout, parent, false)
                )
            }

            else -> {
                PhoneHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false),
                    this
                )
            }
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PhoneHolder -> {
                val index = if (position > 0) {
                    position - 1
                } else {
                    0
                }
                bindPhoneNumber(holder, index)
            }

            is AddsHolder -> {
                bindAdds(holder)
            }
        }
    }

    private fun bindPhoneNumber(holder: PhoneHolder, position: Int) {
        val phoneData = mPhoneData[position]
        var phoneNumber = phoneData.phoneNumber
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            phoneNumber = mContext.getString(R.string.unknown_number)
        }
        if (phoneData.isShowEditNumber) {
            holder.editBtn.visibility = View.VISIBLE
        } else {
            holder.editBtn.visibility = View.GONE
        }
        holder.phoneNumber.text = phoneNumber
        holder.operatorName.text = phoneData.operatorName
        val operatorBackgroundColor: Int = if (phoneData.color != -1) {
            phoneData.color
        } else {
            ContextCompat.getColor(
                mContext,
                sColorIds[position]
            )
        }
        val shapeDrawable =
            ContextCompat.getDrawable(mContext, R.drawable.operator_background) as GradientDrawable?
        shapeDrawable?.setColor(operatorBackgroundColor)
        holder.operatorName.background = shapeDrawable
    }

    private fun bindAdds(holder: AddsHolder) {
        val adRequest = AdRequest.Builder().build()
        holder.adView.loadAd(adRequest)
        holder.adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                analytics.sendAdsEvent("LOADED")
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                analytics.sendAdsEvent("FAILED")
            }

            override fun onAdClicked() {
                analytics.sendAdsEvent("CLICKED")
            }

            override fun onAdClosed() {
                analytics.sendAdsEvent("CLOSED")
            }
        }
    }

    override fun getItemCount(): Int {
        return mPhoneData.size + 1
    }

    override fun getItemViewType(position: Int): Int = if (mPhoneData.isNotEmpty()) {
        if (position == 1) {
            ADDS_TYPE
        } else {
            PHONE_NUMBER_TYPE
        }
    } else {
        ADDS_TYPE
    }

    override fun onCopyPhoneNumber(position: Int) {
        val phoneNumber = if (position == 0) {
            mPhoneData[position]
        } else {
            mPhoneData[position - 1]
        }.phoneNumber

        mActionListener?.onCopyPhoneNumber(phoneNumber)
    }

    override fun onSharePhoneNumber(position: Int) {
        val phoneNumber = if (position == 0) {
            mPhoneData[position]
        } else {
            mPhoneData[position - 1]
        }.phoneNumber

        mActionListener?.onSharePhoneNumber(phoneNumber)
    }

    override fun onEditPhoneNumber(position: Int) {
        mActionListener?.onEditPhoneNumber(position)
    }

    fun getItemOnPosition(position: Int): PhoneData = if (position == 0) {
        mPhoneData[position]
    } else {
        mPhoneData[position - 1]
    }

    interface ActionListener {
        fun onCopyPhoneNumber(phoneNumber: String?)
        fun onSharePhoneNumber(phoneNumber: String?)
        fun onEditPhoneNumber(position: Int)
    }

    companion object {
        @ColorRes
        private val sColorIds = intArrayOf(
            R.color.operatorBackground1,
            R.color.operatorBackground2,
            R.color.operatorBackground3
        )

        const val PHONE_NUMBER_TYPE = 0
        const val ADDS_TYPE = 1
    }
}
