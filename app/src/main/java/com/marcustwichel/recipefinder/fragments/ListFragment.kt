package com.marcustwichel.recipefinder.recipefinder.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.marcustwichel.recipefinder.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ListFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListFragment : Fragment(), RecyclerListItemTouchHelper.RecyclerItemTouchHelperListener {

    private val TAG: String = "ListFragment"

    private var mListener: OnFragmentInteractionListener? = null
    lateinit var recyclerView : RecyclerView
    lateinit var mItemAdapter : ListItemAdapter
    private lateinit var itemInput : EditText
    lateinit var moveButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_list, container, false)

//        mRelativeLayout = view.findViewById(R.id.kitchen_frag_relative_layout) as RelativeLayout

        recyclerView = view.findViewById(R.id.list_items) as RecyclerView
        mItemAdapter = ListItemAdapter(recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context) as RecyclerView.LayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = mItemAdapter

        val itemTouchHelperCallbackUnchecked = RecyclerListItemTouchHelper(0, ItemTouchHelper.LEFT, this)
        ItemTouchHelper(itemTouchHelperCallbackUnchecked).attachToRecyclerView(recyclerView)


        itemInput = view.findViewById(R.id.add_list_item) as EditText

        itemInput.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(view: TextView?, actionId: Int, keyEvent: KeyEvent?): Boolean {
                var handled : Boolean = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addItem(itemInput.text.toString())
                    itemInput.setText("")
                    itemInput.clearFocus()
                    handled = true;
                }
                return handled;
            }
        })

        moveButton = view.findViewById(R.id.move_to_kitchen_button) as Button
        moveButton.setOnClickListener(View.OnClickListener {  view ->
            mItemAdapter.moveCheckedToKitchen()
        })

        return view
    }

    private fun addItem(item: String) {
        mItemAdapter.addItem(item)
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

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int?) {
        if (viewHolder is ListItemAdapter.ListItemViewHolder) {
//            val name = itemAdapter.getItemName(viewHolder.getAdapterPosition())
            mItemAdapter.removeItem(viewHolder.getAdapterPosition())
        }
    }


    interface OnFragmentInteractionListener {

    }

}
