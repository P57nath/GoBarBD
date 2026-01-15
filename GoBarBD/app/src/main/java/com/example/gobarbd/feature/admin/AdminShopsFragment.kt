package com.example.gobarbd.feature.admin

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gobarbd.R
import com.example.gobarbd.core.data.model.Barbershop

class AdminShopsFragment : Fragment() {

    private lateinit var viewModel: AdminShopsViewModel
    private lateinit var adapter: AdminShopAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_admin_shops, container, false)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerAdminShops)
        val progress = view.findViewById<ProgressBar>(R.id.progressAdminShops)
        val empty = view.findViewById<TextView>(R.id.txtAdminShopsEmpty)

        adapter = AdminShopAdapter(mutableListOf()) { shop ->
            openShopDialog(shop)
        }
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        viewModel = ViewModelProvider(this)[AdminShopsViewModel::class.java]
        viewModel.shops.observe(viewLifecycleOwner) { list ->
            adapter.updateData(list)
            progress.visibility = View.GONE
            empty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
        viewModel.error.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
            progress.visibility = View.GONE
        }

        view.findViewById<Button>(R.id.btnAddShop).setOnClickListener {
            openShopDialog(null)
        }

        progress.visibility = View.VISIBLE
        viewModel.load()

        return view
    }

    private fun openShopDialog(shop: Barbershop?) {
        val context = requireContext()
        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            val padding = (16 * resources.displayMetrics.density).toInt()
            setPadding(padding, padding, padding, padding)
        }
        val edtName = EditText(context).apply {
            hint = "Shop name"
            setText(shop?.name ?: "")
        }
        val edtAddress = EditText(context).apply {
            hint = "Address"
            setText(shop?.location ?: "")
        }
        val edtDescription = EditText(context).apply {
            hint = "Description"
            inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE
            setText(shop?.description ?: "")
        }
        val switchOpen = Switch(context).apply {
            text = "Open"
            isChecked = shop?.isOpen ?: true
        }
        container.addView(edtName)
        container.addView(edtAddress)
        container.addView(edtDescription)
        container.addView(switchOpen)

        AlertDialog.Builder(context)
            .setTitle(if (shop == null) "Add shop" else "Edit shop")
            .setView(container)
            .setPositiveButton("Save") { _, _ ->
                val name = edtName.text.toString().trim()
                val address = edtAddress.text.toString().trim()
                val description = edtDescription.text.toString().trim()
                val isOpen = switchOpen.isChecked
                if (name.isBlank()) {
                    Toast.makeText(context, "Name required", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                viewModel.saveShop(
                    shopId = shop?.id,
                    name = name,
                    address = address,
                    description = description,
                    isOpen = isOpen
                )
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
