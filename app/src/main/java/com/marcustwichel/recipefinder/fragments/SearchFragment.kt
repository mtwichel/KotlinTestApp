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



    private var mListener: OnFragmentInteractionListener? = null
    var recipes : List<Recipe>? = null
    var mainAdapter : MainAdapter? = null
    lateinit var recyclerView: RecyclerView
    var seachingSnackbar : Snackbar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = view.findViewById(R.id.recycler_view) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        val searchButton = view.findViewById(R.id.search_button) as Button
        val searchBar = view.findViewById(R.id.search_bar) as EditText

        val linearLayout = view.findViewById(R.id.search_linear_layout) as LinearLayout

//        seachingSnackbar = Snackbar.make(activity.main_view ,
//                "Searching", Snackbar.LENGTH_INDEFINITE)

        searchButton.setOnClickListener {
            hideKeyboard()
//            seachingSnackbar?.show()
            searchRecipies(searchBar.text.toString())
        }

        searchBar.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(view: TextView?, actionId: Int, keyEvent: KeyEvent?): Boolean {
                var handled : Boolean = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard()
//                  seachingSnackbar?.show()
                    searchRecipies(searchBar.text.toString())
                    searchBar.clearFocus()
                    handled = true;
                }
                return handled;
            }
        })

        return view
    }

    private fun hideKeyboard() {
        val view = activity?.currentFocus
        if (view != null) {
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun searchRecipies(searchString : String) {
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
