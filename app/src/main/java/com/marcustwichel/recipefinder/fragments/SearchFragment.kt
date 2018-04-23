package com.marcustwichel.recipefinder.recipefinder.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore

import com.marcustwichel.recipefinder.R
import com.marcustwichel.recipefinder.activities.RecipeResultsActivity

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SearchFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment(), AdapterView.OnItemSelectedListener{


    val TAG: String = "SearchFragment"



    private var mListener: OnFragmentInteractionListener? = null

    lateinit var mAuth : FirebaseAuth
    lateinit var mDB : FirebaseFirestore


    lateinit var cuisineSpinner : Spinner
    lateinit var typeSpinner : Spinner
    lateinit var queryStringInput : EditText
    lateinit var rankingSpinner : Spinner

    var cuisine : String? = null
    var type : String? = null
    var queryString : String? = null
    var ranking : Int? = null
    var searchString : String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        mAuth = FirebaseAuth.getInstance()
        mDB = FirebaseFirestore.getInstance()

        var items = ArrayList<String>()
        if (mAuth.currentUser != null) {
            mDB.collection("kitchens").document(mAuth.currentUser!!.uid).addSnapshotListener(
                    EventListener() { documentSnapshot, exception ->
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            items = documentSnapshot.get("items") as ArrayList<String>
                            searchString = list2String(items)
                        }
                    })
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_search, container, false)

        val searchButton = view.findViewById(R.id.search_button) as Button


        cuisineSpinner = view.findViewById(R.id.search_cuisine)
        typeSpinner = view.findViewById(R.id.search_type)
        rankingSpinner = view.findViewById(R.id.ranking_spinner)
        queryStringInput = view.findViewById(R.id.query_string_input) as EditText

        var cuisineAdapter : ArrayAdapter<CharSequence> =
                ArrayAdapter.createFromResource(context,
                        R.array.cuisine_types,
                        android.R.layout.simple_spinner_item)
        cuisineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cuisineSpinner.setAdapter(cuisineAdapter)
        typeSpinner.setSelection(0)

        var typeAdapter : ArrayAdapter<CharSequence> =
                ArrayAdapter.createFromResource(context,
                        R.array.meal_types,
                        android.R.layout.simple_spinner_item)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.setAdapter(typeAdapter)
        typeSpinner.setSelection(0)

        cuisineSpinner.setOnItemSelectedListener(this)
        typeSpinner.setOnItemSelectedListener(this)

        var rankingAdapter : ArrayAdapter<CharSequence> =
                ArrayAdapter.createFromResource(context,
                        R.array.ranking_options,
                        android.R.layout.simple_spinner_item)
        rankingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        rankingSpinner.setAdapter(rankingAdapter)
        rankingSpinner.setSelection(0)

        cuisineSpinner.setOnItemSelectedListener(this)
        typeSpinner.setOnItemSelectedListener(this)

        searchButton.setOnClickListener {
            queryString = queryStringInput.text.toString()
            if(queryString.equals("")){
                queryString = null
            }

            if(searchString == null){
                Toast.makeText(context, "You must add items in your kitchen before searching", Toast.LENGTH_LONG).show()
            }else{
                var intent = Intent(context, RecipeResultsActivity::class.java)
                intent.putExtra("searchString", searchString)
                intent.putExtra("cuisine", cuisine)
                intent.putExtra("type", type)
                intent.putExtra("queryString", queryString)
                intent.putExtra("ranking", ranking)
                startActivity(intent)
            }
        }

        return view
    }


    private fun list2String(list: ArrayList<String>) : String?{
        if(list.size == 0){
            return null
        }
        var ans : String = ""
        list.forEach { string ->
            ans += string + ","
        }
        if(ans.get(ans.lastIndex)==','){
            ans = ans.substring(0, ans.lastIndex)
        }
        Log.i(TAG, ans)
        return ans
    }



    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(parent?.id){
            R.id.search_type -> {
                val item = parent?.getItemAtPosition(position).toString()
                if(item.equals("No Preference")){
                    cuisine = null
                }else {
                    cuisine = parent?.getItemAtPosition(position).toString()
                }
            }
            R.id.search_cuisine -> {
                val item = parent?.getItemAtPosition(position).toString()
                if(item.equals("No Preference")){
                    type = null
                }else {
                    type = parent?.getItemAtPosition(position).toString()
                }
            }
            R.id.ranking_spinner -> {
                ranking = position
            }
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
//        fun onFragmentInteraction()
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): SearchFragment {
            val fragment = SearchFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
