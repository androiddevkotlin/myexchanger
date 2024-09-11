package com.exchange.app.presentation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.exchange.app.databinding.BalanceItemBinding
import com.exchange.app.domain.entity.Balance
import com.exchange.app.utils.amountAndCurrencyText
import timber.log.Timber

class BalanceAdapter(
    private var balances: List<Balance> = listOf(),
    private val onClickListener: OnClickListener? = null,
) : RecyclerView.Adapter<BalanceAdapter.BalanceHolder>() {

    interface OnClickListener {
        fun onBalanceClicked(position: Int, balanceItem: Balance)
    }

    class BalanceHolder(private val binding: BalanceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(balance: Balance, listener: OnClickListener?) {
            binding.balanceItemText.text = balance.amountAndCurrencyText()
            itemView.setOnClickListener {
                Timber.d("setOnClickListener: balance - $balance")
                listener?.onBalanceClicked(adapterPosition, balance)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BalanceHolder {
        val binding =
            BalanceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BalanceHolder(binding)
    }

    override fun onBindViewHolder(holder: BalanceHolder, position: Int) =
        holder.bind(balances[position], onClickListener)

    override fun getItemCount(): Int = balances.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(balances: List<Balance>) {
        this.balances = balances
        notifyDataSetChanged()
        Timber.d("setData: balances - $balances")
    }

}