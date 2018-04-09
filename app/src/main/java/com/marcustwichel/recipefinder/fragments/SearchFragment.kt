package com.marcustwichel.recipefinder.recipefinder.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.marcustwichel.recipefinder.DetailActivity
import com.marcustwichel.recipefinder.MainAdapter

import com.marcustwichel.recipefinder.R
import com.marcustwichel.recipefinder.recipefinder.api.RecipieRetriver
import com.marcustwichel.recipefinder.recipefinder.model.Recipe
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SearchFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment(), View.OnClickListener {

    val TAG: String? = "SearchFragment"



    private var mListener: OnFragmentInteractionListener? = null
    var recipes : List<Recipe>? = null
    var mainAdapter : MainAdapter? = null
    lateinit var recyclerView: RecyclerView
    var seachingSnackbar : Snackbar? = null
    lateinit var searchString : String

    lateinit var mAuth : FirebaseAuth
    lateinit var mDB : FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        mAuth = FirebaseAuth.getInstance()
        mDB = FirebaseFirestore.getInstance()

        var items = ArrayList<String>()
        if (mAuth.currentUser != null) {
            mDB.collection("kitchens").document(mAuth.currentUser!!.uid).addSnapshotListener(
                    EventListener() { documentSnapshot, exception ->
                        if (documentSnapshot.exists()) {
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

        recyclerView = view.findViewById(R.id.recycler_view) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        val searchButton = view.findViewById(R.id.search_button) as Button


        val linearLayout = view.findViewById(R.id.search_linear_layout) as LinearLayout

        searchButton.setOnClickListener {
            searchRecipies()
        }

        return view
    }

    private fun list2String(list: ArrayList<String>) : String{
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


    private fun searchRecipies() {
        var retriever = RecipieRetriver()
        val callback = object : Callback<List<Recipe>> {
            override fun onFailure(call: Call<List<Recipe>>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<List<Recipe>>?, response: Response<List<Recipe>>?) {
                response?.isSuccessful.let {
                    this@SearchFragment.recipes = response?.body()
                    mainAdapter = MainAdapter(this@SearchFragment.recipes!!,
                            this@SearchFragment)
                    recyclerView.adapter = mainAdapter
                    this@SearchFragment.seachingSnackbar?.dismiss()
                }
            }

        }

        retriever.getRecipes(callback, searchString)
    }

    override fun onClick(view: View?) {
        val intent = Intent(view?.context, DetailActivity::class.java)
        val holder = view?.tag as MainAdapter.RecipeViewHolder
        intent.putExtra(DetailActivity.RECIPE,
                mainAdapter?.getRecipe(holder.adapterPosition));
        startActivity(intent)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed() {
        if (mListener != null) {
//            mListener!!.onFragmentInteraction()
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
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
