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

import com.marcustwichel.recipefinder.RecyclerKitchenItemTouchHelper
import com.marcustwichel.recipefinder.KitchenItemAdapter
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.design.widget.Snackbar
import android.graphics.Color
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.marcustwichel.recipefinder.R


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [KitchenFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [KitchenFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class KitchenFragment : Fragment(), RecyclerKitchenItemTouchHelper.RecyclerItemTouchHelperListener {

    lateinit var recyclerView : RecyclerView
    lateinit var mItemAdapter : KitchenItemAdapter
    lateinit var mRelativeLayout : RelativeLayout
    lateinit var itemInput : EditText


    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_kitchen, container, false)

        mRelativeLayout = view.findViewById(R.id.kitchen_frag_relative_layout) as RelativeLayout

        recyclerView = view.findViewById(R.id.kitchen_items_view) as RecyclerView
        mItemAdapter = KitchenItemAdapter()
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = mItemAdapter

        val itemTouchHelperCallback = RecyclerKitchenItemTouchHelper(0, ItemTouchHelper.LEFT, this)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)

        itemInput = view.findViewById(R.id.add_kitchen_item) as EditText

        itemInput.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(view: TextView?, actionId: Int, keyEvent: KeyEvent?): Boolean {
                var handled : Boolean = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if(!itemInput.text.toString().equals("")) {
                        addItem(itemInput.text.toString())
                        itemInput.setText("")
                        itemInput.clearFocus()
                    }
                handled = true;
        }
        return handled;
            }
        })

        return view
    }

    private fun addItem(item: String) {
        mItemAdapter.addItem(item)
        recyclerView.scrollToPosition(0)
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

    private val TAG: String? = "KitchenFragment"

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
