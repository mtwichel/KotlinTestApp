package com.example.android.kotlintest.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.android.kotlintest.R
import com.example.android.kotlintest.RecyclerItemTouchHelper
import com.example.android.kotlintest.model.KitchenItemAdapter
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.design.widget.Snackbar
import android.content.ClipData.Item
import android.graphics.Color
import android.util.Log


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [KitchenFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [KitchenFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class KitchenFragment : Fragment(), RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    lateinit var recyclerView : RecyclerView
    lateinit var items : MutableList<String>
    lateinit var mItemAdapter : KitchenItemAdapter

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {

        }
        items = ArrayList()
        items.add("chicken")
        items.add("rice")
        items.add("coke")
        items.add("bread")
        items.add("salt")
        items.add("curry")
        items.add("beer")

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_kitchen, container, false)

        recyclerView = view.findViewById(R.id.kitchen_items_view) as RecyclerView
        mItemAdapter = KitchenItemAdapter(items)
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = mItemAdapter

        val itemTouchHelperCallback = RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)



        return view
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
//            mListener!!.onFragmentInteraction(uri)
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

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int?) {
        if (viewHolder is KitchenItemAdapter.KitchenItemViewHolder) {
            Log.d("KitchenFragment", "onSwiped called")

            // get the removed item name to display it in snack bar
            val name = items.get(viewHolder.getAdapterPosition())

            // backup of removed item for undo purpose
            val deletedItem = items.get(viewHolder.getAdapterPosition())
            val deletedIndex = viewHolder.getAdapterPosition()

            // remove the item from recycler view
            mItemAdapter.removeItem(viewHolder.getAdapterPosition())

            // showing snack bar with Undo option
            val snackbar = Snackbar
                    .make(recyclerView, name + " removed from cart!", Snackbar.LENGTH_LONG)
            snackbar.setAction("UNDO", View.OnClickListener {
                // undo is selected, restore the deleted item
                mItemAdapter.restoreItem(deletedItem, deletedIndex)
            })
            snackbar.setActionTextColor(Color.YELLOW)
            snackbar.show()
        }
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
//        fun onFragmentInteraction(uri: Uri)
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
         * @return A new instance of fragment KitchenFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): KitchenFragment {
            val fragment = KitchenFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
