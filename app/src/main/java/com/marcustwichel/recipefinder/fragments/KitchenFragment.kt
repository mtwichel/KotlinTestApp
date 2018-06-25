package com.marcustwichel.recipefinder.recipefinder.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.marcustwichel.recipefinder.adapters.RecyclerKitchenItemTouchHelper
import com.marcustwichel.recipefinder.adapters.KitchenItemAdapter
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.design.widget.Snackbar
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.marcustwichel.recipefinder.R
import com.marcustwichel.recipefinder.model.AutocompleteResult
import com.marcustwichel.recipefinder.recipefinder.api.RecipeRetriver
import retrofit2.Call
import retrofit2.Response


class KitchenFragment : Fragment(), RecyclerKitchenItemTouchHelper.RecyclerItemTouchHelperListener {

    private val TAG: String? = "KitchenFragment"

    var mDB : FirebaseFirestore = FirebaseFirestore.getInstance()
    var mAuth : FirebaseAuth = FirebaseAuth.getInstance()
    var workingDocument = mDB.collection("kitchens").document(mAuth.currentUser!!.uid)

    lateinit var recyclerView : RecyclerView
    lateinit var mItemAdapter : KitchenItemAdapter
    lateinit var mRelativeLayout : RelativeLayout
    lateinit var itemInput : AutoCompleteTextView
    lateinit var retriver : RecipeRetriver


    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retriver = RecipeRetriver()
        if (arguments != null) {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_kitchen, container, false)

        itemInput = view.findViewById(R.id.add_kitchen_item) as AutoCompleteTextView

        mRelativeLayout = view.findViewById(R.id.kitchen_frag_relative_layout) as RelativeLayout

        recyclerView = view.findViewById(R.id.kitchen_items_view) as RecyclerView
        mItemAdapter = KitchenItemAdapter(kitchenItems)
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = mItemAdapter

        val itemTouchHelperCallback = RecyclerKitchenItemTouchHelper(0, ItemTouchHelper.LEFT, this)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)


        var autocompleteList : ArrayList<String>? = null
        val callback = object : retrofit2.Callback<List<AutocompleteResult>> {
            override fun onFailure(call: Call<List<AutocompleteResult>>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<List<AutocompleteResult>>?, response: Response<List<AutocompleteResult>>?) {
                response?.isSuccessful.let {
                    val responseList = response?.body() as ArrayList
                    autocompleteList = autocompleteResultListtoStringList(responseList)
                    if(autocompleteList != null) {
                        Log.i(TAG, autocompleteList.toString())
                        var adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, autocompleteList)
                        itemInput.setAdapter(adapter)
                        if(adapter.isEmpty){
                            itemInput.dismissDropDown()
                        }else {
                            itemInput.showDropDown()
                        }
                    }else{
                        Log.d(TAG, "list not null")
                    }
                }
            }

        }


        itemInput.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d(TAG, "onTextChanged")
                retriver.getIngredientsAutocomplete(callback, s.toString())
            }

        })

        itemInput.onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                addItemToKitchen()
            }

        }

        //Set keyboard checkmark
        itemInput.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(view: TextView?, actionId: Int, keyEvent: KeyEvent?): Boolean {
                var handled : Boolean = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addItemToKitchen()
                    handled = true
                }
                return handled
            }
        })

        return view
    }

    private fun addItemToKitchen() {
        if (!itemInput.text.toString().equals("")) {
            workingDocument.update(itemInput.text.toString(), true)
            itemInput.setText("")
            itemInput.clearFocus()
        }
    }

    private fun autocompleteResultListtoStringList(input : ArrayList<AutocompleteResult>?) : ArrayList<String> {
        var ans = ArrayList<String>()
        input?.forEach{ item ->
            ans.add(item.name)
        }
        return ans
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

    private fun toTitleCase(string :String) : String{
        return when (string.length) {
            0 -> ""
            1 -> string.toUpperCase()
            else -> string[0].toUpperCase() + string.substring(1)
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int?) {
        if (viewHolder is KitchenItemAdapter.KitchenItemViewHolder) {

            // get the removed item name to display it in snack bar
            val name = mItemAdapter.getItemName(viewHolder.getAdapterPosition())



            // remove the item from recycler view
            mItemAdapter.removeItem(viewHolder.getAdapterPosition())

            // showing snack bar with Undo option
            val snackbar = Snackbar
                    .make(getActivity()!!.findViewById(android.R.id.content),
                              name + " removed from kitchen", Snackbar.LENGTH_LONG)
            snackbar.setAction("UNDO", View.OnClickListener {
                // undo is selected, restore the deleted item
                mItemAdapter.restoreLastItem()
            })
            snackbar.setActionTextColor(Color.YELLOW)
            snackbar.show() }
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
//        fun onFragmentInteraction(uri: Uri)
    }

}// Required empty public constructor
