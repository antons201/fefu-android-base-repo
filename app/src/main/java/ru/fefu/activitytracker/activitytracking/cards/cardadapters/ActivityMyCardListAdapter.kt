package ru.fefu.activitytracker.activitytracking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.fefu.activitytracker.R
import ru.fefu.activitytracker.activitytracking.cards.utils.TimeUtils

class ActivityMyCardListAdapter (
    cards: List<Card>) : RecyclerView.Adapter<RecyclerView.ViewHolder> (){

    val mutableCards = cards.toMutableList()

    private var itemClickListener: (Int) -> Unit = {}

    companion object {
        private const val ITEM_TYPE_ACTIVITY_CARD = 1
        private const val ITEM_TYPE_PERIOD_CARD = 2

    }

    override fun getItemViewType(position: Int): Int =
        if (mutableCards[position]::class == ActivityMyCard::class) ITEM_TYPE_ACTIVITY_CARD
        else ITEM_TYPE_PERIOD_CARD

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_TYPE_ACTIVITY_CARD) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_card_my_item, parent, false)
            ActivityMyCardListViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_period_item, parent, false)
            ActivityPeriodListViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == ITEM_TYPE_ACTIVITY_CARD) {
            (holder as ActivityMyCardListViewHolder).bind(mutableCards[position] as ActivityMyCard)
        } else {
            (holder as ActivityPeriodListViewHolder).bind(mutableCards[position] as ActivityPeriod)
        }
    }

    fun setItemClickListener(listener: (Int) -> Unit) {
        itemClickListener = listener
    }

    inner class ActivityMyCardListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val distanceActivity: TextView = itemView.findViewById(R.id.distance_activity)
        private val timeActivity: TextView = itemView.findViewById(R.id.time_activity)
        private val sportTypeActivity: TextView = itemView.findViewById(R.id.sport_type_activity)
        private val dateActivity: TextView = itemView.findViewById(R.id.date_activity)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) itemClickListener.invoke(position)
            }
        }

        fun bind(activityMyCard: ActivityMyCard) {
            distanceActivity.text = activityMyCard.distance
            timeActivity.text = TimeUtils.getDuration(activityMyCard.start_time, activityMyCard.stop_time)
            sportTypeActivity.text = activityMyCard.sport_type.type
            dateActivity.text = TimeUtils.getSpentTime(activityMyCard.stop_time)
        }
    }

    inner class ActivityPeriodListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val period: TextView = itemView.findViewById(R.id.period_activity)

        fun bind(activityPeriod: ActivityPeriod) {
            period.text = activityPeriod.period
        }
    }

    override fun getItemCount(): Int =
        mutableCards.size
}