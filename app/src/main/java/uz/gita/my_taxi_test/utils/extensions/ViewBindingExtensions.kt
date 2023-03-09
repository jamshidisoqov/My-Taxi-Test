package uz.gita.my_taxi_test.utils.extensions

import androidx.viewbinding.ViewBinding

// Created by Jamshid Isoqov on 3/8/2023
fun <T : ViewBinding> T.include(block: T.() -> Unit) {
    block(this)
}
