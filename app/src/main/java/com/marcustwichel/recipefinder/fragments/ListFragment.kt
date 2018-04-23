package com.marcustwichel.recipefinder.recipefinder.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.marcustwichel.recipefinder.*
import com.marcustwichel.recipefinder.adapters.ListItemAdapter
import com.marcustwichel.recipefinder.adapters.RecyclerListItemTouchHelper
import com.marcustwichel.recipefinder.model.ListItem
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ListFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListFragment : Fragment(), RecyclerListItemTouchHelper.RecyclerItemTouchHelperListener, View.OnClickListener {

    private val TAG: String = "ListFragment"

    lateinit var fragView : View

    var mDB : FirebaseFirestore = FirebaseFirestore.getInstance()
    var mAuth : FirebaseAuth = FirebaseAuth.getInstance()

    var workingDocument = mDB.collection("groceryLists").document(mAuth.currentUser!!.uid)
    var kitchenDocument = mDB.collection("kitchens").document(mAuth.currentUser!!.uid)

    var oldSize : Int = 0
    var deletedPos : Int? = null
    var modifiedPos : Int? = null
    var currentId : Int? = null
    var items : ArrayList<ListItem?>? = null
    var kitchenItems : ArrayList<String>? = null


    private var mListener: OnFragmentInteractionListener? = null
    lateinit var recyclerView : RecyclerView
    lateinit var mItemAdapter : ListItemAdapter
    private lateinit var itemInput : EditText
    lateinit var moveButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (mAuth.currentUser != null) {


            kitchenDocument.addSnapshotListener { documentSnapshot, exception->
                if(documentSnapshot != null && documentSnapshot.exists()){
                    kitchenItems = documentSnapshot.get("items") as ArrayList<String>
                }
            }


            workingDocument.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (documentSnapshot?.get("currentId") == null) {
                    currentId = 0
                    val idMap = HashMap<String, Int>()
                    idMap.put("currentId", 0)
                    workingDocument.set(idMap as MutableMap<String, Any>)
                } else {
                    currentId = (documentSnapshot.get("currentId") as Long).toInt()
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        fragView = inflater.inflate(R.layout.fragment_list, container, false)
        recyclerView = fragView.findViewById(R.id.list_items) as RecyclerView
        itemInput = fragView.findViewById(R.id.add_list_item) as EditText
        moveButton = fragView.findViewById(R.id.move_to_kitchen_button) as Button

        mItemAdapter = ListItemAdapter(this, recyclerView, ArrayList())
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context) as RecyclerView.LayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = mItemAdapter

        workingDocument.collection("items").addSnapshotListener { collectionSnapshot, exception ->
            Log.d(TAG, "Collection Changed")
            if(items != null){
                oldSize = items?.size!!
            }

            items = ArrayList()
            collectionSnapshot!!.documents.forEach { documentSnapshot ->
                items?.add(documentSnapshot.toObject(ListItem::class.java))
                mItemAdapter.items = items!!
            }

            Collections.sort(items)

            collectionSnapshot?.documentChanges?.forEach {documentChange ->
                when(documentChange.type){
                    DocumentChange.Type.ADDED -> {
                        mItemAdapter.notifyItemInserted(0)
                        recyclerView.scrollToPosition(0)
                    }
                    DocumentChange.Type.REMOVED -> {
                        if(items?.size == 0){
                            mItemAdapter.notifyDataSetChanged()
                        }else{
                            if(deletedPos != null){
                                mItemAdapter.notifyItemRemoved(deletedPos!!)
                            }else{
                                mItemAdapter.notifyDataSetChanged()
                            }

                        }
                    }
                    DocumentChange.Type.MODIFIED -> {
                        if(modifiedPos != null){
                            mItemAdapter.notifyItemChanged(modifiedPos!!)
                        }else{
                            mItemAdapter.notifyDataSetChanged()
                        }
                    }
                    else ->{
                        mItemAdapter.notifyDataSetChanged()
                    }
                }
                mItemAdapter.notifyDataSetChanged()
            }

            val itemTouchHelperCallbackUnchecked = RecyclerListItemTouchHelper(0, ItemTouchHelper.LEFT, this)
            ItemTouchHelper(itemTouchHelperCallbackUnchecked).attachToRecyclerView(recyclerView)


            itemInput.setOnEditorActionListener { view, actionId, keyEvent ->
                var handled = false
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Log.i(TAG, currentId!!.toString())
                    var map = HashMap<String, Any>()
                    map.put("id", currentId!!)
                    map.put("string", toTitleCase(itemInput.text.toString()))
                    map.put("checked", false)
                    workingDocument.collection("items").document(currentId!!.toString()).set(map)
                    workingDocument.update("currentId", currentId!! + 1)
                    itemInput.setText("")
                    itemInput.clearFocus()

                    handled = true
                }
                handled
            }


            moveButton.setOnClickListener(View.OnClickListener { view ->
                moveCheckedToKitchen()
            })
        }
        return fragView
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
        deletedPos = position!!
        if (viewHolder is ListItemAdapter.ListItemViewHolder) {
            workingDocument.collection("items").document(items?.get(position!!)?.id.toString()).delete()
        }
    }

    override fun onClick(view: View?) {
        val holder = view!!.tag as ListItemAdapter.ListItemViewHolder
        val position =holder.adapterPosition
        modifiedPos = position
        val previousState = items?.get(position)?.checked
        workingDocument.collection("items").document(items?.get(position)?.id.toString()).update("checked", previousState?.not())

    }

    private fun toTitleCase(string :String) : String{
        return when (string.length) {
            0 -> ""
            1 -> string.toUpperCase()
            else -> string[0].toUpperCase() + string.substring(1)
        }
    }

    fun moveCheckedToKitchen() {
        var itemsToDelete = ArrayList<ListItem>()
        items?.forEach { item ->
            if(item!!.checked){
                kitchenItems?.add(0, item.string)
                itemsToDelete.add(item)
            }
        }

        itemsToDelete.forEach {item ->
            deletedPos = null
            workingDocument.collection("items").document(item.id.toString()).delete()
        }

        kitchenDocument.update("items", kitchenItems)
    }

    interface OnFragmentInteractionListener {

    }

}
