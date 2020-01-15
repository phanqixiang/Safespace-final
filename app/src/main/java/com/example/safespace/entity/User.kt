package com.example.safespace.entity


class User(val userId: String, val name: String, val nickname: String, val email: String, val status: String){
    constructor():this("","","","","")
}