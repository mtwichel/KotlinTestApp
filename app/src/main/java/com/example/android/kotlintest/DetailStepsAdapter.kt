package com.example.android.kotlintest

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.android.kotlintest.model.Step
import org.w3c.dom.Text

/**
 * Created by mtwichel on 3/16/18.
 */
class DetailStepsAdapter(var steps : List<Step>) :
        RecyclerView.Adapter<DetailStepsAdapter.StepViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): StepViewHolder {
        return StepViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.step_item, parent, false))
    }

    override fun getItemCount(): Int {
        return steps.size
    }

    override fun onBindViewHolder(holder: StepViewHolder?, position: Int) {
        val currentStep = steps[position]
        holder?.stepText?.text = currentStep.step
        holder?.stepNumber?.text = currentStep.number.toString()
    }


    inner class StepViewHolder(itemView : View) : RecyclerView.ViewHolder (itemView){
        var stepNumber : TextView?
        var stepText : TextView?

        init {

            itemView.tag = this
            stepText = itemView.findViewById(R.id.step_text) as TextView
            stepNumber = itemView.findViewById(R.id.step_number) as TextView

        }
    }
}