package com.exchange.app.presentation

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exchange.app.App
import com.exchange.app.R
import com.exchange.app.databinding.FragmentExchangeBinding
import com.exchange.app.domain.entity.Balance
import com.exchange.app.domain.model.SubmitState
import com.exchange.app.utils.SpacingItemDecorator
import com.exchange.app.utils.amountAndCurrencyText
import com.exchange.app.utils.deepEqualTo
import com.exchange.app.utils.getList
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import timber.log.Timber
import javax.inject.Inject

class ExchangeFragment : Fragment(), BalanceAdapter.OnClickListener {

    private var _binding: FragmentExchangeBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by lazy {
        ViewModelProvider(viewModelStore, viewModelFactory)[ExchangeViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDaggerComponent()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentExchangeBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun initDaggerComponent() {
        val app = (activity?.application as App)
        app.appComponent.injectFragment(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setPortraitOrientation()
        checkConnectivity()
        initViews()

        setupEditTextListener()
        setupSellCurrencyTypesSpinnerListener()
        setupReceiveCurrencyTypesSpinnerListener()
        setupSubmitButtonListener()

        observeBalances()
        observeCurrencyTypes()
        observeReceiveAmount()
        observeSubmitState()
    }

    private fun setPortraitOrientation(): Unit? {
        return activity?.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    }

    private fun checkConnectivity() {
        if (!viewModel.isConnect()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.no_internet_text),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun initViews() {
        binding.balanceContainer.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                RecyclerView.HORIZONTAL,
                false
            )
            adapter = BalanceAdapter(onClickListener = this@ExchangeFragment)
            addItemDecoration(SpacingItemDecorator(25))
        }
    }

    private fun setupEditTextListener() {
        binding.sellAmountEditText.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty()) {
                viewModel.sellAmount = 0.0
            } else {
                viewModel.sellAmount = text.toString().toDouble()
            }
        }
    }

    private fun setupSellCurrencyTypesSpinnerListener() {
        binding.sellCurrencyTypesSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                i: Int,
                l: Long,
            ) {
                viewModel.sellCurrencyType =
                    binding.sellCurrencyTypesSpinner.selectedItem.toString()

            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                return
            }
        }
    }

    private fun setupReceiveCurrencyTypesSpinnerListener() {
        binding.receiveCurrencyTypesSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                i: Int,
                l: Long,
            ) {
                viewModel.receiveCurrencyType =
                    binding.receiveCurrencyTypesSpinner.selectedItem.toString()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                return
            }
        }
    }

    private fun setupSubmitButtonListener() {
        binding.submitButton.setOnClickListener {
            viewModel.onSubmitExchange()
        }
    }

    private fun observeBalances() {
        viewModel.balances.observe(viewLifecycleOwner) { balances ->
            Timber.d("observeBalances: balances - $balances")
            (binding.balanceContainer.adapter as BalanceAdapter).setData(balances)
        }
    }

    private fun observeCurrencyTypes() {
        viewModel.currencyTypes.observe(viewLifecycleOwner) { currencyTypes ->
            val sellPosition = binding.sellCurrencyTypesSpinner.selectedItemPosition
            val receivePosition = binding.receiveCurrencyTypesSpinner.selectedItemPosition

            if (!currencyTypes.deepEqualTo(binding.sellCurrencyTypesSpinner.getList())) {
                val adapterCurrencyTypes = ArrayAdapter(
                    requireContext(),
                    R.layout.currency_type_item,
                    currencyTypes
                )
                binding.sellCurrencyTypesSpinner.adapter = adapterCurrencyTypes
                binding.receiveCurrencyTypesSpinner.adapter = adapterCurrencyTypes

                binding.sellCurrencyTypesSpinner.setSelection(sellPosition)
                binding.receiveCurrencyTypesSpinner.setSelection(receivePosition)
            }
        }
    }

    private fun observeReceiveAmount() {
        viewModel.receiveAmount.observe(viewLifecycleOwner) { receiveAmount ->
            binding.receiveAmountText.text = String.format(
                getString(R.string.receive_amount_format_text),
                receiveAmount
            )
        }
    }

    private fun observeSubmitState() {
        viewModel.submitState.observe(viewLifecycleOwner) { submitState ->
            MaterialAlertDialogBuilder(requireContext()).apply {
                val message: String = when (submitState) {
                    is SubmitState.Success -> String.format(
                        getString(R.string.submit_success_message_format_text),
                        submitState.sellMoney.amountAndCurrencyText(),
                        submitState.receiveMoney.amountAndCurrencyText(),
                        submitState.commission.amountAndCurrencyText()
                    )

                    is SubmitState.SmallAmount -> String.format(
                        getString(R.string.submit_small_amount_message_format_text),
                        submitState.storageSellBalance.amountAndCurrencyText(),
                        submitState.sellMoney.amountAndCurrencyText()
                    )

                    is SubmitState.NoBalanceType -> String.format(
                        getString(R.string.submit_no_balance_type_message_format_text),
                        submitState.sellMoney.currencyType
                    )

                    is SubmitState.SameType -> String.format(
                        getString(R.string.submit_same_type_message_format_text),
                        submitState.sellMoney.currencyType
                    )

                    is SubmitState.NoTypes -> getString(R.string.submit_no_types_message_format_text)
                }
                setTitle(submitState.type)
                setMessage(message)
                setPositiveButton(resources.getString(R.string.dialog_positive_button_text)) { dialog, _ ->
                    dialog.dismiss()
                }
                show()
            }
        }
    }

    override fun onBalanceClicked(position: Int, balanceItem: Balance) {
        Timber.d("onBalanceClicked: position - $position, balanceItem - $balanceItem")
    }
}