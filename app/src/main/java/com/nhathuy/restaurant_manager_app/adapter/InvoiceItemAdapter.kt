package com.nhathuy.restaurant_manager_app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nhathuy.restaurant_manager_app.data.model.Invoice
import com.nhathuy.restaurant_manager_app.data.model.PaymentMethod
import com.nhathuy.restaurant_manager_app.databinding.ItemInvoiceAdminBinding
import java.text.NumberFormat
import java.util.Locale


/**
 * Adapter class for the invoice item recyclerview
 *
 * @return 0.1
 * @since 11-04-2025
 * @author TravisHuy
 */
class InvoiceItemAdapter(private val onViewDetailClick: (Invoice) -> Unit,
                         private val onPrintClick: (Invoice) -> Unit
): ListAdapter<Invoice, InvoiceItemAdapter.InvoiceViewHolder>(InvoiceDiffCallback()) {

    inner class InvoiceViewHolder(private val binding:ItemInvoiceAdminBinding,
                                  private val onViewDetailClick: (Invoice) -> Unit,
                                  private val onPrintClick: (Invoice) -> Unit
    ):RecyclerView.ViewHolder(binding.root){
        private var currentInvoice: Invoice? =null

        init {
            binding.btnViewDetails.setOnClickListener {
                currentInvoice?.let {
                    invoice ->
                    onViewDetailClick(invoice)
                }
            }

            binding.btnPrint.setOnClickListener {
                currentInvoice?.let {
                    invoice ->
                    onPrintClick(invoice)
                }
            }
        }
        fun bind(invoice: Invoice){
            currentInvoice = invoice

            binding.tvInvoiceId.text = invoice.id

            binding.tvPaymentStatus.text = when(invoice.paymentMethod) {
                PaymentMethod.CASH -> "Tiền mặt"
                PaymentMethod.CARD -> "Thẻ tín dụng"
                PaymentMethod.ONLINE -> "Chuyển khoản"
                else -> "Khác"
            }

            binding.tvOrderIdValue.text = invoice.orderId

            binding.tvPaymentTimeLabel.text = invoice.paymentTime

            val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
            val formattedAmount = formatter.format(invoice.totalAmount) + " đ"
            binding.tvTotalAmountValue.text = formattedAmount
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceViewHolder {
        val binding = ItemInvoiceAdminBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return InvoiceViewHolder(binding,onViewDetailClick, onPrintClick)
    }

    override fun onBindViewHolder(holder: InvoiceViewHolder, position: Int) {
        val invoice = getItem(position)
        holder.bind(invoice)
    }


    class InvoiceDiffCallback: DiffUtil.ItemCallback<Invoice>(){
        override fun areItemsTheSame(oldItem: Invoice, newItem: Invoice): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Invoice, newItem: Invoice): Boolean {
            return oldItem == newItem
        }

    }
}
