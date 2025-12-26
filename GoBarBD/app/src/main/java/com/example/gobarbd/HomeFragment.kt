package com.example.gobarbd

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        setupNearest(view)
        setupRecommended(view)
        setupActions(view)

        return view
    }

    /* -----------------------------
       NEAREST BARBERSHOPS
    ------------------------------ */
    private fun setupNearest(view: View) {
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerNearestShops)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        recycler.adapter = NearestBarbershopAdapter(
            BarbershopRepository.getNearest()
        ) { shop ->
            // TODO: Navigate to details screen
            Toast.makeText(requireContext(), shop.name, Toast.LENGTH_SHORT).show()
        }
    }

    /* -----------------------------
       RECOMMENDED BARBERSHOPS
    ------------------------------ */
    private fun setupRecommended(view: View) {
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerRecommended)
        recycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        recycler.adapter = RecommendedBarbershopAdapter(
            BarbershopRepository.getRecommended()
        ) { shop ->
            // TODO: Navigate to booking screen
            Toast.makeText(
                requireContext(),
                "Booking ${shop.name}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /* -----------------------------
       BUTTON ACTIONS
    ------------------------------ */
    private fun setupActions(view: View) {

        // SEE ALL NEAREST
        view.findViewById<Button>(R.id.btnSeeAllNearest).setOnClickListener {
            val intent = Intent(requireContext(), AllBarbershopsActivity::class.java)
            intent.putExtra("LIST_TYPE", "nearest")
            intent.putParcelableArrayListExtra(
                "BARBERSHOPS_LIST",
                ArrayList(BarbershopRepository.getNearest())
            )
            startActivity(intent)
        }

        // SEE ALL RECOMMENDED
        view.findViewById<Button>(R.id.btnSeeAllRecommended).setOnClickListener {
            val intent = Intent(requireContext(), AllBarbershopsActivity::class.java)
            intent.putExtra("LIST_TYPE", "recommended")
            intent.putParcelableArrayListExtra(
                "BARBERSHOPS_LIST",
                ArrayList(BarbershopRepository.getRecommended())
            )
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.btnFilter).setOnClickListener {
            val filterBottomSheet = FilterBottomSheet.newInstance()

            filterBottomSheet.setOnFilterAppliedListener { filterData ->
                // TEMP: Just verify filter is working
                Toast.makeText(
                    requireContext(),
                    "Filter applied\nRating: ${filterData.rating}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            filterBottomSheet.show(childFragmentManager, "FilterBottomSheet")
        }

        // FIND NOW (GOOGLE MAPS)
        view.findViewById<Button>(R.id.btnFindNow).setOnClickListener {
            val mapIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("geo:0,0?q=barber+shop+near+me")
            )
            mapIntent.setPackage("com.google.android.apps.maps")

            if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(mapIntent)
            } else {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/search/barber+shop+near+me")
                    )
                )
            }
        }
    }
}
