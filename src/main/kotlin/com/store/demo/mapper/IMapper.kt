package com.store.demo.mapper

interface IMapper<I, O> {
    fun map(input: I): O
    fun update(target: O, input: I)
}
