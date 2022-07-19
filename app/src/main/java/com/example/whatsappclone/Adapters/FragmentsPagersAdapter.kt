package com.example.whatsappclone.Adapters


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter


class FragmentsPagersAdapter(fm:FragmentManager,mlifecycle:Lifecycle):FragmentStateAdapter(fm,mlifecycle) {
    private val mfragements = ArrayList<Fragment>()
    override fun getItemCount(): Int {
        return mfragements.size
    }

    override fun createFragment(position: Int): Fragment {
        return mfragements[position]
    }

     fun addFragments(fragment: Fragment){
        mfragements.add(fragment)
    }



}